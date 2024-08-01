package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientRedactedDemographicDTO;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.lamisplus.modules.ndr.mapper.redacted.MessageHeaderTypeRedactedMapper;
import org.lamisplus.modules.ndr.mapper.redacted.PatientDemographicsRedactedMapper;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.redacted.Container;
import org.lamisplus.modules.ndr.schema.redacted.MessageHeaderType;
import org.lamisplus.modules.ndr.schema.redacted.PatientDemographicsType;
import org.lamisplus.modules.ndr.utility.ZipUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedactService {
    private static final String BASE_DIR = "runtime/ndr_redact/transfer/";
    @Autowired
    private NdrMessageLogRepository ndrMessageLogRepository;
    private final NdrRedactService ndrService;
    private final NdrMessageLogRepository data;
    private final NdrXmlStatusRepository ndrXmlStatusRepository;
    public final AtomicLong messageId = new AtomicLong(0);
    private final PatientDemographicsRedactedMapper patientDemographicsMapper;
    private final MessageHeaderTypeRedactedMapper messageHeaderTypeMapper;

    public static final String JAXB_ENCODING = "UTF-8";
    public static final String XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION = "\n<!-- This XML was generated from LAMISPlus application -->";
    public static final String HEADER_BIND_COMMENT = "com.sun.xml.bind.xmlHeaders";
    /**
     * NDR REDACTED XML FOR PATIENT IN LAMISPLUS
     * IMPLEMENTED BY VICTOR AJOR
     *
     * **/
    public void generatePatientsRedactedXml(long facilityId, boolean initial) {
        final String pathname = BASE_DIR + "temp/" + facilityId + "/";
        log.info("folder -> "+ pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        List<String> patientIds = new ArrayList<String>();
        List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();
        if (initial) {
            patientIds = data.getPatientIdsEligibleForRedaction(start, facilityId);
            log.info("starting initial redaction....");
        }else {
            log.info("starting updated redaction....");
            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, "redacted");
            if (lastGenerateDateTimeByFacilityId.isPresent()) {
                LocalDateTime lastModified =
                        lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
                log.info("Last Generated Date: " + lastModified);
                patientIds = data.getPatientIdsEligibleForRedaction(lastModified, facilityId);
            }
        }
        PatientRedactedDemographicDTO[] patientRedactedDemographicDTO = new PatientRedactedDemographicDTO[1];

        log.info("patient size -> "+ patientIds.size());

        String pushIdentifier = UUID.randomUUID().toString();

        patientIds.parallelStream()
                .forEach(id -> {
                    if (getPatientRedactedXml(id, facilityId, initial, ndrErrors, pushIdentifier)) {
                        generatedCount.getAndIncrement();
                        patientRedactedDemographicDTO[0] = data.getRedactedPatientDemographics(id, facilityId).get();
                    } else {
                        errorCount.getAndIncrement();
                    }
                });
        log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
        log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
        File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
        if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
            log.info(BASE_DIR + "temp/" + facilityId + "/" + " will be split into two");
        }
        if (generatedCount.get() > 0) {
            zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    patientRedactedDemographicDTO[0],
                    ndrErrors,
                    "redacted", pushIdentifier
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

    private boolean getPatientRedactedXml(String patientId, long facilityId, boolean initial, List<NDRErrorDTO> ndrErrors, String pushIdentifier) {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("starting process patient xml file information");
        log.info("facilityId {}, patientId {}", facilityId, patientId);
        LocalDate start = LocalDate.of(1985, Month.JANUARY, 1);
        LocalDate end = LocalDate.now().plusDays(1);
        log.info("start {}, end {}", start, end);

        PatientRedactedDemographicDTO patientDemographic =
                getPatientDemographic(patientId, facilityId, ndrErrors);

//        if (!initial && patientDemographic != null) {
//            Optional<NdrMessageLog> messageLog =
//                    data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "treatment");
//            if (messageLog.isPresent()) {
//                start = messageLog.get().getLastUpdated().toLocalDate();
//            }
//        }
        //redacted visits
        if (patientDemographic == null) return false;

        String fileName = generatePatientRedactedXml(
                facilityId, patientDemographic,
                initial,
                ndrErrors, pushIdentifier);
        if (fileName != null) {
            saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName,"redacted");
        }
        return fileName != null;
    }
    public String generatePatientRedactedXml(long facilityId, PatientRedactedDemographicDTO patientDemographic,
                                             boolean initial, List<NDRErrorDTO> ndrErrors, String pushIdentifier) {

        try {
            log.info("generating redacted xml for patient with uuid {}", patientDemographic.getPatientIdentifier());
            log.info("fetching redacted patient demographics....");
            long id = messageId.incrementAndGet();
            Container container = new Container();
            JAXBContext jaxbContext = JAXBContext.newInstance(Container.class);
            //caching this because is static
            PatientDemographicsType patientRedactedDemographic =
                    patientDemographicsMapper.getRedactedPatient(patientDemographic);

            if (patientRedactedDemographic != null) {
               MessageHeaderType messageHeader = messageHeaderTypeMapper.getMessageHeader(patientDemographic);
                String messageStatusCode = "INITIAL";
                if (!initial) {
                    Optional<NdrMessageLog> firstByIdentifier =
                            data.findFirstByIdentifier(patientDemographic.getPatientIdentifier());
                    if (firstByIdentifier.isPresent()) {
                        messageStatusCode = "UPDATED";
                    }
                }

                messageHeader.setMessageUniqueID(Long.toString(id));

                container.setEmrType("LAMISPlus");
                container.setMessageHeader(messageHeader);
                container.setPatientDemographics(patientRedactedDemographic);

                Marshaller jaxbMarshaller = ndrService.getMarshaller(jaxbContext);
                jaxbMarshaller.setProperty(HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                URL resourceUrl = getClass().getClassLoader().getResource("NDR_RD_1.xsd");
                if (resourceUrl != null) {
                    Schema schema = sf.newSchema(resourceUrl);
                    jaxbMarshaller.setSchema(schema);
                    //String identifier = patientRedactedDemographic.getPatientIdentifier();
                    System.out.println("XSD Resource URL: " + resourceUrl);
                } else {
                    // Resource not found
                    System.err.println("XSD Resource not found.");
                }

                String fileName = ndrService.processAndGenerateRedactedFile(facilityId, jaxbMarshaller, container, patientDemographic, id, ndrErrors);
                if (fileName != null) {
                    log.info("Redacted XML was successfully generated for patient with hospital number " + patientDemographic.getHospitalNumber());
                }
                return fileName;
            }
        } catch (Exception e) {
            log.error("An error occur when generating person with hospital number {}",
                    patientDemographic.getHospitalNumber());
            log.error("error: " + e.toString());
            e.printStackTrace();
            ndrErrors.add(new NDRErrorDTO(patientDemographic.getPersonUuid(),
                    patientDemographic.getHospitalNumber(), e.toString()));
        }
        return null;
    }

    private PatientRedactedDemographicDTO getPatientDemographic(String patientId, long facilityId, List<NDRErrorDTO> ndrErrors) {
        try {
            PatientRedactedDemographicDTO patientRedactedDemographicDTO;
            Optional<PatientRedactedDemographicDTO> patientDemographicDTOOptional =
                    data.getRedactedPatientDemographics(patientId, facilityId);
            if (patientDemographicDTOOptional.isPresent()) {
                log.info("patient demographic information were retrieved successfully");
                patientRedactedDemographicDTO = patientDemographicDTOOptional.get();
                return patientRedactedDemographicDTO;
            }
        } catch (Exception e) {
            log.error("An error occur while fetching patient with uuid {} information error {}", patientId, e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return null;
    }

    public void zipAndSaveTheFilesforDownload(
            Long facilityId,
            String pathname,
            AtomicInteger count,
            PatientRedactedDemographicDTO patient, List<NDRErrorDTO> ndrErrors, String type, String identifier) {
        try {
            zipFiles(patient, facilityId, pathname, ndrErrors,type,identifier);
        } catch (Exception e) {
            log.error("An error occurred while zipping files error {}", e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patient.getPersonUuid(), patient.getHospitalNumber(), e.getMessage()));
        }
    }
    public void zipFiles(PatientRedactedDemographicDTO demographic,
                         long facilityId,
                         String sourceFolder,
                         List<NDRErrorDTO> ndrErrors, String type, String identifier) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String sCode = demographic.getStateCode();
        String lCode = demographic.getLgaCode();
        String fileName = StringUtils.leftPad(sCode, 2, "0") +
                StringUtils.leftPad(lCode, 3, "0") + "_" + demographic.getFacilityId() +
                "_" + demographic.getFacilityName() +"_"+type+ "_" + dateFormat.format(new Date());
        fileName = RegExUtils.replaceAll(fileName, "/", "-");
        log.info("file name for download {}", fileName);
        String finalFileName = fileName.replace(" ", "").replace(",", "")
                .replace(".", "");
        String outputZipFile = null;
        try {
            outputZipFile = BASE_DIR + "ndr/" + finalFileName;
            new File(BASE_DIR + "ndr").mkdirs();
            new File(Paths.get(outputZipFile).toAbsolutePath().toString()).createNewFile();
            List<File> files = new ArrayList<>();
            files = ndrService.getFiles(sourceFolder, files);
            long thirtyMB = (FileUtils.ONE_MB * 15)*2;
            File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
            if (ZipUtility.getFolderSize(folder) > thirtyMB) {
                List<List<File>> splitFiles = split(files, thirtyMB);
                for (int i = 0; i < splitFiles.size(); i++) {
                    String splitFileName = finalFileName + "_" + (i + 1);
                    String splitOutputZipFile = BASE_DIR + "ndr/" + splitFileName;
                    Path path = Paths.get(splitOutputZipFile);
                    new File(path.toAbsolutePath().toString()).createNewFile();
                    zip(splitFiles.get(i), path.toAbsolutePath().toString());
                    storeTheFileInBD(facilityId, new AtomicInteger(splitFiles.get(i).size()), demographic, ndrErrors, splitFileName,type, identifier);
                }
            } else {
                ZipUtility.zip(files, Paths.get(outputZipFile).toAbsolutePath().toString(), thirtyMB);
                storeTheFileInBD(facilityId, new AtomicInteger(files.size()), demographic, ndrErrors, finalFileName,type, identifier);
            }
        } catch (Exception exception) {
            ndrErrors.add(new NDRErrorDTO(demographic.getPersonUuid(), demographic.getHospitalNumber(), exception.getMessage()));
            log.error("An error occurred while creating temporary file " + outputZipFile);
        }
    }

    public static List<List<File>> split(List<File> files, long sizeLimit) {
        List<List<File>> splitFiles = new ArrayList<>();
        List<File> currentSplit = new ArrayList<>();
        long currentSize = 0;
        for (File file : files) {
            long fileSize = file.length();
            if (currentSize + fileSize > sizeLimit) {
                splitFiles.add(currentSplit);
                currentSplit = new ArrayList<>();
                currentSize = 0;
            }
            currentSplit.add(file);
            currentSize += fileSize;
        }
        splitFiles.add(currentSplit);
        return splitFiles;
    }

    public static void zip(List<File> files, String outputZipFile) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputZipFile))) {
            for (File file : files) {
                try (FileInputStream fileIn = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOut.putNextEntry(zipEntry);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fileIn.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeTheFileInBD(Long facilityId, AtomicInteger count, PatientRedactedDemographicDTO patient,
                                 List<NDRErrorDTO> ndrErrors, String zipFileName, String type, String identifier) {
        NdrXmlStatus ndrXmlStatus = new NdrXmlStatus();
        if(ndrErrors.size() > 0){
            JsonNode node = getNode(ndrErrors);
            ndrXmlStatus.setError(node);
        }
        ndrXmlStatus.setFacilityId(facilityId);
        ndrXmlStatus.setFiles(count.get());
        ndrXmlStatus.setFileName(zipFileName);
        ndrXmlStatus.setLastModified(LocalDateTime.now());
        ndrXmlStatus.setPushIdentifier(patient.getFacilityId().concat("_").concat(identifier));
        ndrXmlStatus.setCompletelyPushed(Boolean.FALSE);
        ndrXmlStatus.setPercentagePushed(0L);
        ndrXmlStatus.setType(type);
        ndrXmlStatusRepository.save(ndrXmlStatus);
    }

    private JsonNode getNode(List<NDRErrorDTO> values) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return  mapper.valueToTree(values);
        } catch (Exception e) {
            log.error("An error occurred while converting error list to JsonB");
        }
        return null;
    }

    public void saveTheXmlFile(String identifier, String fileName, String fileTye) {
        NdrMessageLog ndrMessageLog = new NdrMessageLog();
        ndrMessageLog.setIdentifier(identifier);
        ndrMessageLog.setFile(fileName);
        ndrMessageLog.setLastUpdated(LocalDateTime.now());
        ndrMessageLog.setFileType(fileTye);
        data.save(ndrMessageLog);
    }
}
