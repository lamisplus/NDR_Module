package org.lamisplus.modules.ndr.service;

import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.HtsReportDto;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PrepDTO;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.mapper.*;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.repositories.NDRQueryRepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.ZipUtility;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrEPService {

    private final NdrMessageLogRepository data;
    private final NDRQueryRepository ndrQueryRepository;

    private final NDRService ndrService;

    private final MessageHeaderTypeMapper messageHeaderTypeMapper;
    private final PatientDemographicsMapper patientDemographicsMapper;
    private final ConditionTypeMapper conditionTypeMapper;
    private final NdrXmlStatusRepository ndrXmlStatusRepository;
    public static final String BASE_DIR = "runtime/ndr/transfer/";
    public static final String JAXB_ENCODING = "UTF-8";
    public static final String XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION = "\n<!-- This XML was generated from LAMISPlus application -->";
    public static final String HEADER_BIND_COMMENT = "com.sun.xml.bind.xmlHeaders";
    public final AtomicLong messageId = new AtomicLong(0);

    public final HtsTypeMapper htsTypeMapper;

    public final HtsEncounterReportTypeMapper htsEncounterReportTypeMapper;

    public final PrEPTypeMapper prEPTypeMapper;

    private final NdrOptimizationService ndrOptimizationService;
    private static final String TEMP = "temp/";

    public void generateOnePatientHtsNDRXml(long facilityId, boolean initial, String clientCode) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> "+ pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        List<String> patientIds = new ArrayList<>();
        List<NDRErrorDTO> ndrErrors = new ArrayList<>();
        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

        if (initial) {
            patientIds = data.getHtsClientCode(facilityId, start);
            log.info("generating initial ....");
        }

        log.info("Prep patient single -> "+ patientIds.size());

        if (getPatientPrepNDRXml(clientCode, facilityId, initial,objectFactory, ndrErrors)) {
            generatedCount.getAndIncrement();
        } else {
            errorCount.getAndIncrement();
        }

        log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
        log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
        File folder = new File(BASE_DIR + TEMP + facilityId + "/");
        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
        if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
            log.info(BASE_DIR + TEMP + facilityId + "/" + " will be split into two");
        }
        if (generatedCount.get() > 0) {
            ndrOptimizationService.zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    patientDemographicDTO[0],
                    ndrErrors,
                    "hts", patientDemographicDTO[0].getPatientIdentifier()
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

//    public void generateSelectedPatientsPrepNDRXml(long facilityId, boolean initial, List<String> patientIds) {
//        ObjectFactory objectFactory = new ObjectFactory();
//        final String pathname = BASE_DIR + TEMP + facilityId + "/";
//        log.info("folder -> "+ pathname);
//        ndrService.cleanupFacility(facilityId, pathname);
//        AtomicInteger generatedCount = new AtomicInteger();
//        AtomicInteger errorCount = new AtomicInteger();
//        LocalDateTime start;
//
//        List<NDRErrorDTO> ndrErrors = new ArrayList<>();
//        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];
//
//        if (initial) {
//            start = LocalDateTime.of(1984, 1, 1, 0, 0);
//            //patientIds = data.getHtsClientCode(facilityId, start);
//            log.info("generating initial 4 selected....");
//            log.info("patient size -> "+ patientIds.size());
//            log.info("patient ids -> "+ patientIds);
//            patientIds.parallelStream()
//                    .forEach(id -> {
//                        if (getPatientPrepNDRXml(id, facilityId, initial,objectFactory, ndrErrors)) {
//                            generatedCount.getAndIncrement();
//                            patientDemographicDTO[0] = data.getHtsPatientDemographics(facilityId, id , start).get();
//                        } else {
//                            errorCount.getAndIncrement();
//                        }
//                    });
//        }else {
//            log.info("generating updated 4 selected....");
//            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
//                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId,"hts");
//            if (lastGenerateDateTimeByFacilityId.isPresent()) {
//                start = lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
//                log.info("Last Generated Date: " + start);
//                patientIds = data.getHtsClientCode(facilityId,start);
//
//                patientIds.parallelStream()
//                        .forEach(id -> {
//                            if (getPatientPrepNDRXml(id, facilityId, false,objectFactory, ndrErrors)) {
//                                generatedCount.getAndIncrement();
//                                patientDemographicDTO[0] = data.getHtsPatientDemographics(facilityId, id , start).get();
//                            } else {
//                                errorCount.getAndIncrement();
//                            }
//                        });
//            }
//        }
//
//        log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
//        log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
//        File folder = new File(BASE_DIR + TEMP + facilityId + "/");
//        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
//        if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
//            log.info(BASE_DIR + TEMP + facilityId + "/" + " will be split into two");
//        }
//        if (generatedCount.get() > 0) {
//            ndrOptimizationService.zipAndSaveTheFilesforDownload(
//                    facilityId,
//                    pathname,
//                    generatedCount,
//                    patientDemographicDTO[0],
//                    ndrErrors,
//                    "hts", patientDemographicDTO[0].getPatientIdentifier()
//            );
//        }
//        log.error("error list size {}", ndrErrors.size());
//    }
//
//    public void generatePatientsPrepNDRXml(long facilityId, boolean initial) {
//        ObjectFactory objectFactory = new ObjectFactory();
//        final String pathname = BASE_DIR + TEMP + facilityId + "/";
//        log.info("folder -> "+ pathname);
//        ndrService.cleanupFacility(facilityId, pathname);
//        AtomicInteger generatedCount = new AtomicInteger();
//        AtomicInteger errorCount = new AtomicInteger();
//        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
//        List<String> patientIds = new ArrayList<>();
//        List<NDRErrorDTO> ndrErrors = new ArrayList<>();
//        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];
//        if (initial) {
//            patientIds = data.getHtsClientCode(facilityId, start);
//            log.info("generating initial ....");
//        }else {
//            log.info("generating updated....");
//            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
//                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId,"prep");
//            if (lastGenerateDateTimeByFacilityId.isPresent()) {
//                start = lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
//                log.info("Last Generated Date: " + start);
//                patientIds = data.getHtsClientCode(facilityId,start);
//            }
//        }
//
//        log.info("patient size -> "+ patientIds.size());
//        LocalDateTime finalStart = start;
//        patientIds.parallelStream()
//                .forEach(id -> {
//                    if (getPatientPrepNDRXml(id, facilityId, initial,objectFactory, ndrErrors)) {
//                        generatedCount.getAndIncrement();
//                        patientDemographicDTO[0] = data.getHtsPatientDemographics(facilityId, id , finalStart).get();
//                    } else {
//                        errorCount.getAndIncrement();
//                    }
//                });
//        log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
//        log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
//        File folder = new File(BASE_DIR + TEMP + facilityId + "/");
//        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
//        if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
//            log.info(BASE_DIR + TEMP + facilityId + "/" + " will be split into two");
//        }
//        if (generatedCount.get() > 0) {
//            ndrOptimizationService.zipAndSaveTheFilesforDownload(
//                    facilityId,
//                    pathname,
//                    generatedCount,
//                    patientDemographicDTO[0],
//                    ndrErrors,
//                    "hts", patientDemographicDTO[0].getPatientIdentifier()
//            );
//        }
//        log.error("error list size {}", ndrErrors.size());
//    }

    private boolean getPatientPrepNDRXml(String patientId, long facilityId, boolean initial, ObjectFactory obj, List<NDRErrorDTO> ndrErrors) {
        log.info("starting process patient xml file information");
        log.info("facilityId {}, patientId {}", facilityId, patientId);
        LocalDateTime start = LocalDateTime.of(1985, Month.JANUARY, 1,0,0);
        log.info("start prep generation {}, ", start);
        // start 1
        PatientDemographicDTO patientDemographic =
                getPatientDemographic(facilityId, patientId, start, ndrErrors);

        if (!initial && patientDemographic != null) {
            Optional<NdrMessageLog> messageLog =
                    data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "prep");
            if (messageLog.isPresent()) {
                start = messageLog.get().getLastUpdated();
            }
        }
        if (patientDemographic == null) return false;

        // start 2
        log.info("getting prep detail information for patient");
        List<PrepDTO> patientPrepDetails = getPatientPrepDetails(facilityId, patientId, start);
        if(patientPrepDetails != null) {
            log.info("Prep data for client received: " + patientPrepDetails.size());
        }

        String fileName = generatePatientPrepNDRXml(
                facilityId,
                obj,
                patientDemographic,
                patientPrepDetails,
                initial,
                ndrErrors);
        if (fileName != null) {
            ndrOptimizationService.saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName, "prep");
        }
        return fileName != null;
    }

    PatientDemographicDTO getPatientDemographic( long facilityId, String patientId, LocalDateTime lastModified,  List<NDRErrorDTO> ndrErrors){
        try {
            Optional<PatientDemographicDTO> prepPatientDemographics = ndrQueryRepository.getPrepPatientDemographics(patientId, facilityId);
            log.info("check patient Demographics.... "+ prepPatientDemographics.isPresent());
            if(prepPatientDemographics.isPresent()){
                log.info("Prep patient available....");
                return prepPatientDemographics.get();
            }
        }catch (Exception e){
            log.error("error getting prep demographics"+ e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", Arrays.toString(e.getStackTrace())));
            e.printStackTrace();
        }
        return null;
    }

    List<PrepDTO> getPatientPrepDetails(long facilityId, String patientId, LocalDateTime lastModified ){
        log.info("Getting Prep data for client: " + patientId);
        return ndrQueryRepository.getPrepReportByClientCodeAndLastModified(facilityId, patientId,lastModified);
    }

    private String generatePatientPrepNDRXml(
            long facilityId,
            ObjectFactory objectFactory,
            PatientDemographicDTO patientDemographic,
            List<PrepDTO> prepReports,
            boolean initial,
            List<NDRErrorDTO> ndrErrors) {

        log.info("Generating NDR XML for patient uuid {} (initial={})",
                patientDemographic.getPatientIdentifier(), initial);

        try {
            long id = messageId.incrementAndGet();
            Container container = new Container();

            JAXBContext jaxbContext = JAXBContext.newInstance(
                    Container.class,
                    ObjectFactory.class);

            PatientDemographicsType patientDemographics =
                    patientDemographicsMapper.getPatientDemographics(patientDemographic, true);

            if (patientDemographics != null) {
                IndividualReportType individualReportType = new IndividualReportType();
                individualReportType.setPatientDemographics(patientDemographics);

                ConditionType conditionType =
                        conditionTypeMapper.getConditionType(patientDemographic, true);
                if (conditionType != null) {
                    individualReportType.getCondition().add(conditionType);
                } else {
                    log.warn("No ConditionType for {} — XSD requires <Condition>; " +
                                    "XML may fail schema validation",
                            patientDemographic.getPatientIdentifier());
                }

                MessageHeaderType messageHeader =
                        messageHeaderTypeMapper.getMessageHeader(patientDemographic);

                String messageStatusCode = "INITIAL";
                if (!initial) {
                    Optional<NdrMessageLog> firstByIdentifier =
                            data.findFirstByIdentifier(patientDemographic.getPatientIdentifier());
                    if (firstByIdentifier.isPresent()) {
                        messageStatusCode = "UPDATED";
                    }
                }
                messageHeader.setMessageStatusCode(messageStatusCode);
                messageHeader.setMessageUniqueID(Long.toString(id));

                container.setMessageHeader(messageHeader);
                container.setIndividualReport(individualReportType);

                boolean prepMapped = false;
                if (prepReports != null && !prepReports.isEmpty()) {
                    prepMapped = prEPTypeMapper.getPrEPType(
                            individualReportType,
                            objectFactory,
                            prepReports,
                            ndrErrors);
                    log.info("PrEP/PEP mapping result for {}: {}",
                            patientDemographic.getPatientIdentifier(), prepMapped);
                }

                Marshaller jaxbMarshaller = ndrService.getMarshaller(jaxbContext);
                jaxbMarshaller.setProperty(HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);

                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(
                        getClass().getClassLoader().getResource("NDR_XSD 1.7.2.0.xsd"));
                jaxbMarshaller.setSchema(schema);

                String fileName = ndrService.processAndGenerateNDRFile(
                        facilityId, jaxbMarshaller, container, patientDemographic, id, ndrErrors);

                if (fileName != null) {
                    log.info("NDR XML successfully generated for {}",
                            patientDemographic.getHospitalNumber());
                }
                return fileName;
            }
        } catch (Exception e) {
            log.error("Error generating NDR XML for hospital number {}",
                    patientDemographic.getHospitalNumber(), e);
            ndrErrors.add(new NDRErrorDTO(
                    patientDemographic.getHtsUuid(),
                    patientDemographic.getClientCode(),
                    e.getMessage() + " | " + Arrays.toString(e.getStackTrace())));
        }
        return null;
    }
}

