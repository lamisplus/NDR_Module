package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
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

    public void generateOnePatientHtsNDRXml(long facilityId, boolean initial, String patientId) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> "+ pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        final PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

        try {
            if (getPatientPrepNDRXml(patientId, facilityId, initial, objectFactory, ndrErrors)) {
                generatedCount.getAndIncrement();
                Optional<PatientDemographicDTO> demoOpt = ndrQueryRepository.getPrepPatientDemographics(patientId, facilityId, LocalDateTime.of(1984, 1, 1, 0, 0));
                demoOpt.ifPresent(d -> patientDemographicDTO[0] = d);
            } else {
                errorCount.getAndIncrement();
            }
        } catch (Exception e) {
            errorCount.getAndIncrement();
            log.error("Error processing single HTS patient {}: {}", patientId, e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, null, "Processing Error: " + e.getMessage()));
        }

        File folder = new File(pathname);
        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
        if (generatedCount.get() > 0 && patientDemographicDTO[0] != null) {
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

    public void generateSelectedPatientsPrepNDRXml(long facilityId, boolean initial, List<String> patientIds) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> "+ pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start;

        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        final PatientDemographicDTO[] fallbackDemographic = new PatientDemographicDTO[1];

        if (initial) {
            start = LocalDateTime.of(1984, 1, 1, 0, 0);
            log.info("generating initial prep selected.... patient size -> {}", patientIds.size());

            patientIds.parallelStream().filter(Objects::nonNull).forEach(id -> {
                try {
                    if (getPatientPrepNDRXml(id, facilityId, initial, objectFactory, ndrErrors)) {
                        generatedCount.getAndIncrement();
                        Optional<PatientDemographicDTO> demo = ndrQueryRepository.getPrepPatientDemographics(id, facilityId, start);
                        demo.ifPresent(d -> fallbackDemographic[0] = d);
                    } else {
                        errorCount.getAndIncrement();
                    }
                } catch (Exception e) {
                    errorCount.getAndIncrement();
                    log.error("Exception processing patient ID {}: {}", id, e.getMessage());
                    ndrErrors.add(new NDRErrorDTO(id, null, "Batch Processing Error: " + e.getMessage()));
                }
            });
        } else {
            log.info("generating updated prep selected....");
            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, "prep");
            if (lastGenerateDateTimeByFacilityId.isPresent()) {
                start = lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
                log.info("Last Generated Date: " + start);
                List<String> updatedPatientIds = ndrQueryRepository.getPrepId(facilityId, start);

                updatedPatientIds.parallelStream().filter(Objects::nonNull).forEach(id -> {
                    try {
                        if (getPatientPrepNDRXml(id, facilityId, false, objectFactory, ndrErrors)) {
                            generatedCount.getAndIncrement();
                            Optional<PatientDemographicDTO> demo = ndrQueryRepository.getPrepPatientDemographics(id, facilityId, start);
                            demo.ifPresent(d -> fallbackDemographic[0] = d);
                        } else {
                            errorCount.getAndIncrement();
                        }
                    } catch (Exception e) {
                        errorCount.getAndIncrement();
                        log.error("Exception processing updated patient ID {}: {}", id, e.getMessage());
                        ndrErrors.add(new NDRErrorDTO(id, null, "Batch Processing Error: " + e.getMessage()));
                    }
                });
            }
        }

        File folder = new File(pathname);
        log.info("generated {}/{} | files not generated {}/{}", generatedCount.get(), patientIds.size(), errorCount.get(), patientIds.size());
        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));

        if (generatedCount.get() > 0 && fallbackDemographic[0] != null) {
            ndrOptimizationService.zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    fallbackDemographic[0],
                    ndrErrors,
                    "prep", fallbackDemographic[0].getPatientIdentifier()
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

    public void generatePatientsPrepNDRXml(long facilityId, boolean initial) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> "+ pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        List<String> patientIds = new ArrayList<>();
        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        final PatientDemographicDTO[] fallbackDemographic = new PatientDemographicDTO[1];

        if (initial) {
            patientIds = ndrQueryRepository.getPrepId(facilityId, start);
            log.info("generating prep initial all....");
        } else {
            log.info("generating prep updated all....");
            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, "prep");
            if (lastGenerateDateTimeByFacilityId.isPresent()) {
                start = lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
                log.info("Last Generated Date: " + start);
                patientIds = ndrQueryRepository.getPrepId(facilityId, start);
            }
        }

        log.info("patient size -> "+ patientIds.size());
        LocalDateTime finalStart = start;

        patientIds.parallelStream().filter(Objects::nonNull).forEach(id -> {
            try {
                if (getPatientPrepNDRXml(id, facilityId, initial, objectFactory, ndrErrors)) {
                    generatedCount.getAndIncrement();
                    Optional<PatientDemographicDTO> demo = data.getHtsPatientDemographics(facilityId, id, finalStart);
                    demo.ifPresent(d -> fallbackDemographic[0] = d);
                } else {
                    errorCount.getAndIncrement();
                }
            } catch (Exception e) {
                errorCount.getAndIncrement();
                log.error("Exception processing batch patient ID {}: {}", id, e.getMessage());
                ndrErrors.add(new NDRErrorDTO(id, null, "Batch Processing Error: " + e.getMessage()));
            }
        });

        File folder = new File(pathname);
        log.info("generated {}/{} | files not generated {}/{}", generatedCount.get(), patientIds.size(), errorCount.get(), patientIds.size());
        log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));

        if (generatedCount.get() > 0 && fallbackDemographic[0] != null) {
            ndrOptimizationService.zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    fallbackDemographic[0],
                    ndrErrors,
                    "prep", fallbackDemographic[0].getPatientIdentifier()
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

    private boolean getPatientPrepNDRXml(String patientId, long facilityId, boolean initial, ObjectFactory obj, List<NDRErrorDTO> ndrErrors) {
        log.info("starting process patient xml file information");
        log.info("facilityId {}, patientId {}", facilityId, patientId);
        LocalDateTime start = LocalDateTime.of(1985, Month.JANUARY, 1, 0, 0);

        PatientDemographicDTO patientDemographic = getPatientDemographic(facilityId, patientId, start, ndrErrors);

        if (!initial && patientDemographic != null) {
            Optional<NdrMessageLog> messageLog =
                    data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "prep");
            if (messageLog.isPresent()) {
                start = messageLog.get().getLastUpdated();
            }
        }
        if (patientDemographic == null) return false;

        List<PrepDTO> patientPrepDetails = getPatientPrepDetails(facilityId, patientId, start);
        if (patientPrepDetails != null) {
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

    PatientDemographicDTO getPatientDemographic(long facilityId, String patientId, LocalDateTime lastModified, List<NDRErrorDTO> ndrErrors) {
        try {
            Optional<PatientDemographicDTO> prepPatientDemographics = ndrQueryRepository.getPrepPatientDemographics(patientId, facilityId, lastModified);
            if (prepPatientDemographics.isPresent()) {
                return prepPatientDemographics.get();
            }
        } catch (Exception e) {
            log.error("error getting prep demographics: {}", e.getMessage());
            if (ndrErrors != null) {
                synchronized (ndrErrors) {
                    ndrErrors.add(new NDRErrorDTO(patientId, "", Arrays.toString(e.getStackTrace())));
                }
            }
        }
        return null;
    }

    List<PrepDTO> getPatientPrepDetails(long facilityId, String patientId, LocalDateTime lastModified) {
        List<PrepDTO> screeningReports = ndrQueryRepository.getPrepReportByClientCodeAndLastModified(facilityId, patientId, lastModified);
        List<PrepDTO> enrollmentReports = ndrQueryRepository.getCardEnrollment(facilityId, patientId, lastModified);

        List<PrepDTO> combinedReports = new ArrayList<>();
        if (screeningReports != null) combinedReports.addAll(screeningReports);
        if (enrollmentReports != null) combinedReports.addAll(enrollmentReports);

        return combinedReports;
    }

    private String generatePatientPrepNDRXml(
            long facilityId,
            ObjectFactory objectFactory,
            PatientDemographicDTO patientDemographic,
            List<PrepDTO> prepReports,
            boolean initial,
            List<NDRErrorDTO> ndrErrors) {

        try {
            long id = messageId.incrementAndGet();
            Container container = new Container();

            JAXBContext jaxbContext = JAXBContext.newInstance(
                    Container.class,
                    ObjectFactory.class);

            PatientDemographicsType patientDemographics =
                    patientDemographicsMapper.getPatientDemographics(patientDemographic, "PrEP");

            if (patientDemographics != null) {
                IndividualReportType individualReportType = new IndividualReportType();
                individualReportType.setPatientDemographics(patientDemographics);

                ConditionType conditionType =
                        conditionTypeMapper.getConditionType(patientDemographic, true);
                if (conditionType != null) {
                    individualReportType.getCondition().add(conditionType);
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

                if (prepReports != null && !prepReports.isEmpty()) {
                    prEPTypeMapper.getPrEPType(
                            individualReportType,
                            objectFactory,
                            prepReports,
                            ndrErrors);
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

                return fileName;
            }
        } catch (Exception e) {
            log.error("Error generating NDR XML for hospital number {}: {}",
                    patientDemographic.getHospitalNumber(), e.getMessage());
            if (ndrErrors != null) {
                synchronized (ndrErrors) {
                    ndrErrors.add(new NDRErrorDTO(
                            patientDemographic.getHtsUuid(),
                            patientDemographic.getClientCode(),
                            e.getMessage() + " | " + Arrays.toString(e.getStackTrace())));
                }
            }
        }
        return null;
    }
}

