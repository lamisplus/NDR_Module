package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PmtctDTO;
import org.lamisplus.modules.ndr.domain.dto.pmtct.DeliveryChildrenDetailsProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.InfantCohortRegistrationProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.MotherInfantPairVisitProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTRegisterProjection;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.mapper.PMTCTTypeMapper;
import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
import org.lamisplus.modules.ndr.repositories.NDRQueryRepository;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
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
public class PMTCTService {

    private final NdrMessageLogRepository data;
    private final NDRQueryRepository ndrQueryRepository;
    private final NDRService ndrService;
    private final MessageHeaderTypeMapper messageHeaderTypeMapper;
    private final PatientDemographicsMapper patientDemographicsMapper;
    private final ConditionTypeMapper conditionTypeMapper;
    private final NdrXmlStatusRepository ndrXmlStatusRepository;
    private final PMTCTTypeMapper pmtctTypeMapper;
    private final NdrOptimizationService ndrOptimizationService;

    public static final String BASE_DIR = "runtime/ndr/transfer/";
    public static final String JAXB_ENCODING = "UTF-8";
    public static final String XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION = "\n<!-- This XML was generated from LAMISPlus application -->";
    public static final String HEADER_BIND_COMMENT = "com.sun.xml.bind.xmlHeaders";
    public static final String FILE_TYPE = "pmtct";
    private static final String TEMP = "temp/";

    public final AtomicLong messageId = new AtomicLong(0);

    public void generateOnePatientPmtctNDRXml(long facilityId, boolean initial, String patientId) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> {}", pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

        if (getPatientPmtctNDRXml(patientId, facilityId, initial, objectFactory, ndrErrors)) {
            generatedCount.getAndIncrement();
            ndrQueryRepository.getPmtctPatientDemographics(patientId, facilityId, start)
                    .ifPresent(dto -> patientDemographicDTO[0] = dto);
        } else {
            errorCount.getAndIncrement();
        }

        log.info("generated {}/1, files not generated {}/1", generatedCount.get(), errorCount.get());
        File folder = new File(pathname);
        log.info("fileSize {} bytes", ZipUtility.getFolderSize(folder));
        if (generatedCount.get() > 0 && patientDemographicDTO[0] != null) {
            ndrOptimizationService.zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    patientDemographicDTO[0],
                    ndrErrors,
                    FILE_TYPE,
                    patientDemographicDTO[0].getPatientIdentifier()
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

    public void generateSelectedPatientsPmtctNDRXml(long facilityId, boolean initial, List<String> patientIds) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> {}", pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start;

        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

        if (initial) {
            start = LocalDateTime.of(1984, 1, 1, 0, 0);
            log.info("generating initial pmtct selected ({})....", patientIds.size());
            LocalDateTime finalStart = start;
            patientIds.parallelStream().forEach(id -> {
                if (getPatientPmtctNDRXml(id, facilityId, true, objectFactory, ndrErrors)) {
                    generatedCount.getAndIncrement();
                    ndrQueryRepository.getPmtctPatientDemographics(id, facilityId, finalStart)
                            .ifPresent(dto -> patientDemographicDTO[0] = dto);
                } else {
                    errorCount.getAndIncrement();
                }
            });
        } else {
            log.info("generating updated pmtct selected....");
            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, FILE_TYPE);
            start = lastGenerateDateTimeByFacilityId
                    .map(Timestamp::toLocalDateTime)
                    .orElse(LocalDateTime.of(1984, 1, 1, 0, 0));
            LocalDateTime finalStart = start;
            patientIds.parallelStream().forEach(id -> {
                if (getPatientPmtctNDRXml(id, facilityId, false, objectFactory, ndrErrors)) {
                    generatedCount.getAndIncrement();
                    ndrQueryRepository.getPmtctPatientDemographics(id, facilityId, finalStart)
                            .ifPresent(dto -> patientDemographicDTO[0] = dto);
                } else {
                    errorCount.getAndIncrement();
                }
            });
        }

        log.info("generated {}/{}", generatedCount.get(), patientIds.size());
        log.info("files not generated {}/{}", errorCount.get(), patientIds.size());
        File folder = new File(pathname);
        log.info("fileSize {} bytes", ZipUtility.getFolderSize(folder));
        if (generatedCount.get() > 0 && patientDemographicDTO[0] != null) {
            ndrOptimizationService.zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    patientDemographicDTO[0],
                    ndrErrors,
                    FILE_TYPE,
                    patientDemographicDTO[0].getPatientIdentifier()
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

    public void generatePatientsPmtctNDRXml(long facilityId, boolean initial) {
        ObjectFactory objectFactory = new ObjectFactory();
        final String pathname = BASE_DIR + TEMP + facilityId + "/";
        log.info("folder -> {}", pathname);
        ndrService.cleanupFacility(facilityId, pathname);
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        List<String> patientIds = new ArrayList<>();
        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

        if (initial) {
            patientIds = ndrQueryRepository.getPmtctPatientIds(facilityId, start);
            log.info("generating pmtct initial all ({} patients)....", patientIds.size());
        } else {
            log.info("generating pmtct updated all....");
            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, FILE_TYPE);
            if (lastGenerateDateTimeByFacilityId.isPresent()) {
                start = lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
            }
            log.info("Last Generated Date: {}", start);
            patientIds = ndrQueryRepository.getPmtctPatientIds(facilityId, start);
        }

        LocalDateTime finalStart = start;
        patientIds.parallelStream().forEach(id -> {
            if (getPatientPmtctNDRXml(id, facilityId, initial, objectFactory, ndrErrors)) {
                generatedCount.getAndIncrement();
                ndrQueryRepository.getPmtctPatientDemographics(id, facilityId, finalStart)
                        .ifPresent(dto -> patientDemographicDTO[0] = dto);
            } else {
                errorCount.getAndIncrement();
            }
        });

        log.info("generated {}/{}", generatedCount.get(), patientIds.size());
        log.info("files not generated {}/{}", errorCount.get(), patientIds.size());
        File folder = new File(pathname);
        log.info("fileSize {} bytes", ZipUtility.getFolderSize(folder));
        if (generatedCount.get() > 0 && patientDemographicDTO[0] != null) {
            ndrOptimizationService.zipAndSaveTheFilesforDownload(
                    facilityId,
                    pathname,
                    generatedCount,
                    patientDemographicDTO[0],
                    ndrErrors,
                    FILE_TYPE,
                    patientDemographicDTO[0].getPatientIdentifier()
            );
        }
        log.error("error list size {}", ndrErrors.size());
    }

    private boolean getPatientPmtctNDRXml(
            String patientId,
            long facilityId,
            boolean initial,
            ObjectFactory obj,
            List<NDRErrorDTO> ndrErrors) {
        log.info("starting process pmtct patient xml file: facilityId {}, patientId {}", facilityId, patientId);
        LocalDateTime start = LocalDateTime.of(1985, Month.JANUARY, 1, 0, 0);

        PatientDemographicDTO patientDemographic = getPatientDemographic(facilityId, patientId, start, ndrErrors);
        if (patientDemographic == null) {
            return false;
        }

        if (!initial) {
            Optional<NdrMessageLog> messageLog =
                    data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), FILE_TYPE);
            if (messageLog.isPresent()) {
                start = messageLog.get().getLastUpdated();
            }
        }

        PmtctDTO pmtctDetails = getPatientPmtctDetails(facilityId, patientId, start);
        if (pmtctDetails == null || pmtctDetails.isEmpty()) {
            log.info("No PMTCT records found for patient {}", patientId);
            return false;
        }

        String fileName = generatePatientPmtctNDRXml(
                facilityId,
                obj,
                patientDemographic,
                pmtctDetails,
                initial,
                ndrErrors);
        if (fileName != null) {
            ndrOptimizationService.saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName, FILE_TYPE);
        }
        return fileName != null;
    }

    PatientDemographicDTO getPatientDemographic(
            long facilityId,
            String patientId,
            LocalDateTime lastModified,
            List<NDRErrorDTO> ndrErrors) {
        try {
            Optional<PatientDemographicDTO> demographics =
                    ndrQueryRepository.getPmtctPatientDemographics(patientId, facilityId, lastModified);
            if (demographics.isPresent()) {
                return demographics.get();
            }
        } catch (Exception e) {
            log.error("error getting pmtct demographics: {}", e.getMessage(), e);
            ndrErrors.add(new NDRErrorDTO(patientId, "", Arrays.toString(e.getStackTrace())));
        }
        return null;
    }

    PmtctDTO getPatientPmtctDetails(long facilityId, String patientId, LocalDateTime lastModified) {
        List<PMTCTRegisterProjection> registers =
                ndrQueryRepository.getPmtctRegisterByPatient(facilityId, patientId, lastModified);
        List<MotherInfantPairVisitProjection> pairVisits =
                ndrQueryRepository.getMotherInfantPairVisitsByPatient(facilityId, patientId, lastModified);
        List<DeliveryChildrenDetailsProjection> deliveries =
                ndrQueryRepository.getDeliveryChildrenDetailsByPatient(facilityId, patientId, lastModified);
        List<InfantCohortRegistrationProjection> cohorts =
                ndrQueryRepository.getInfantCohortRegistrationsByPatient(facilityId, patientId, lastModified);

        return new PmtctDTO(registers, pairVisits, deliveries, cohorts);
    }

    private String generatePatientPmtctNDRXml(
            long facilityId,
            ObjectFactory objectFactory,
            PatientDemographicDTO patientDemographic,
            PmtctDTO pmtctDTO,
            boolean initial,
            List<NDRErrorDTO> ndrErrors) {

        log.info("Generating PMTCT NDR XML for patient {} (initial={})",
                patientDemographic.getPatientIdentifier(), initial);

        try {
            long id = messageId.incrementAndGet();
            Container container = new Container();

            JAXBContext jaxbContext = JAXBContext.newInstance(
                    Container.class,
                    ObjectFactory.class);

            PatientDemographicsType patientDemographics =
                    patientDemographicsMapper.getPatientDemographics(patientDemographic, "PMTCT");

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

                boolean pmtctMapped = pmtctTypeMapper.getPMTCTType(
                        individualReportType,
                        objectFactory,
                        pmtctDTO,
                        ndrErrors,
                        patientDemographic.getPatientIdentifier());
                log.info("PMTCT mapping result for {}: {}",
                        patientDemographic.getPatientIdentifier(), pmtctMapped);

                if (!pmtctMapped) {
                    return null;
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
                    log.info("PMTCT NDR XML successfully generated for {}",
                            patientDemographic.getHospitalNumber());
                }
                return fileName;
            }
        } catch (Exception e) {
            log.error("Error generating PMTCT NDR XML for hospital number {}",
                    patientDemographic.getHospitalNumber(), e);
            ndrErrors.add(new NDRErrorDTO(
                    patientDemographic.getPersonUuid(),
                    patientDemographic.getHospitalNumber(),
                    e.getMessage() + " | " + Arrays.toString(e.getStackTrace())));
        }
        return null;
    }
}
