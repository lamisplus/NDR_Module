package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.mapper.MortalityTypeMapper;
import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class NDROptimization4SpeedService {
    //testing the new implementation
    private final NdrMessageLogRepository data;
    private final MessageHeaderTypeMapper messageHeaderTypeMapper;
    private final PatientDemographicsMapper patientDemographicsMapper;
    private final ConditionTypeMapper conditionTypeMapper;
    private final MortalityTypeMapper mortalityTypeMapper;
    private final NdrXmlStatusRepository ndrXmlStatusRepository;

    private final NDRService ndrService;

    public static final String BASE_DIR = "runtime/ndr/transfer/";
    private static final String TEMP = "temp/";
    public static final String JAXB_ENCODING = "UTF-8";
    public static final String XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION = "\n<!-- This XML was generated from LAMISPlus application -->";
    public static final String HEADER_BIND_COMMENT = "com.sun.xml.bind.xmlHeaders";
    private static JAXBContext JAXB_CONTEXT;
    private static Schema NDR_SCHEMA;
    public final AtomicLong messageId = new AtomicLong(0);
    private static final long MAX_BATCH_SIZE = 15_000_000L;

    private static final ThreadLocal<Marshaller> MARSHALLER_CACHE = ThreadLocal.withInitial(() -> {
        try{
            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);
            marshaller.setSchema(NDR_SCHEMA);

            marshaller.setEventHandler(event -> {
                log.error("JAXB Validation Error: {}", event.getMessage());
                return false;
            });
            return marshaller;
        }catch(Exception e) {
            throw new IllegalStateException("Failed to create marshaller", e);
        }
    });

    static {
        try{
            JAXB_CONTEXT = JAXBContext.newInstance(Container.class);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            NDR_SCHEMA = sf.newSchema(NdrOptimizationService.class.getClassLoader().getResource("NDR1_6_6_2_R.xsd"));

        }catch(Exception e) {
            throw new RuntimeException("Failed to initialize JAXB context/schema", e);
        }
    }

    private static class XmlResult {
        final String patientId;
        final String xmlContent;

        XmlResult(String patientId, String xmlContent) {
            this.patientId = patientId;
            this.xmlContent = xmlContent;
        }
    }
    public void generateAllPatientsNDRXmls(long facilityId, boolean initial) {
        log.info("Generating NDR XMLs all patients.");
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        String pushIdentifier = UUID.randomUUID().toString();
        List<String> patientIds = null;

        if (initial) {
            log.info("generating initial for all patients....");
            patientIds = data.getPatientIdsEligibleForNDR(start, LocalDateTime.now(), facilityId);
            generatePatientsNDRXml4Speed(patientIds, facilityId, true, pushIdentifier);
        }else {
            log.info("generating updated for all patients....");
            Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                    ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, "treatment");
            if (lastGenerateDateTimeByFacilityId.isPresent()) {
                LocalDateTime lastModified =
                        lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
                log.info("Last Generated XML Date: " + lastModified);
                patientIds = data.getPatientIdsEligibleForNDR(lastModified, LocalDateTime.now(), facilityId);
                log.info("{} Updated Patients from: {}", patientIds.size(), lastModified);
                generatePatientsNDRXml4Speed(patientIds, facilityId, false, pushIdentifier);
            }
        }
    }

//    public void generatePatientsNDRXml_ByLastRecord(long facilityId, boolean initial, List<String> patientIds) {
//        final String pathname = BASE_DIR + TEMP + facilityId + "/";
//        //log.info("folder -> "+ pathname);
//        ndrService.cleanupFacility(facilityId, pathname);
//        AtomicInteger generatedCount = new AtomicInteger();
//        AtomicInteger errorCount = new AtomicInteger();
//        List<NDRErrorDTO> ndrErrors = new ArrayList<>();
//
//        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];
//
//        String pushIdentifier = UUID.randomUUID().toString();
//
//        patientIds.parallelStream()
//                .forEach(id -> {
//                    if (getPatientNDRXml_lastRecord(id, facilityId, initial, ndrErrors, pushIdentifier)) {
//                        generatedCount.getAndIncrement();
//                        patientDemographicDTO[0] = data.getPatientDemographics(id, facilityId).get();
//                    } else {
//                        errorCount.getAndIncrement();
//                    }
//                });
//    }

    private void deleteOldZipFolders(String facilityId) {
        Path zipDir = Paths.get(BASE_DIR, "ndr");
        try{
            Files.createDirectories(zipDir);
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(zipDir, "*.zip")){
                for (Path file : stream) {
                    if (file.getFileName().toString().contains(facilityId)) {
                        Files.deleteIfExists(file);
                        log.info("Deleted old xml zip file: {}", file.getFileName());
                    }
                }

            }
        }catch (IOException e) {
            log.error(" Failed to clean old Zip files for facility {}: {}", facilityId, e.getMessage());
        }
    }


    public void generatePatientsNDRXml(long facilityId, boolean initial, List<String> patientUuidList){
        String pushIdentifier = UUID.randomUUID().toString();
        List<String> patientIds = null;
        assert patientUuidList != null;

        if (initial) {
            log.info("generating initial for selected patients....");
            generatePatientsNDRXml4Speed(patientUuidList, facilityId, true, pushIdentifier);
        }else {
            log.info("generating updated for selected patients....");
            generatePatientsNDRXml4Speed(patientUuidList, facilityId, false, pushIdentifier);
        }

    }

    public void generatePatientsNDRXml4Speed(List<String> patientIds, Long facilityId, boolean initial, String pushIdentifier) {
        deleteOldZipFolders(String.valueOf(facilityId));
        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

        if (patientIds == null || patientIds.isEmpty()) {
            log.warn("No patient IDs provided for NDR XML generation");
            return;
        }else{
            patientDemographicDTO[0] = data.getPatientDemographics(patientIds.get(0), facilityId).orElse(null);
        }

        Path outputDir = Paths.get(BASE_DIR + TEMP, facilityId.toString());
        try {
            Files.createDirectories(outputDir);
            Files.createDirectories(outputDir.resolve("errors"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create output directory: " + e);
        }

        int availableCores = Runtime.getRuntime().availableProcessors();
        int producerPoolSize = Math.max(4, availableCores * 2);
        int writerPoolSize = Math.max(2, availableCores / 2);
        log.info("{} cores available -> Producers: {} | Writers: {}",
                availableCores, producerPoolSize, writerPoolSize);

        ExecutorService producerExecutor = Executors.newFixedThreadPool(producerPoolSize);
        ExecutorService writerExecutor = Executors.newFixedThreadPool(writerPoolSize);

        BlockingQueue<XmlResult> resultsQueue = new LinkedBlockingQueue<>(2000);
        AtomicInteger processedCount = new AtomicInteger();
        AtomicInteger generatedCount = new AtomicInteger();

        // --- Writer Task: store XMLs directly into facilityDir ---
        Runnable writerTask = () -> {
            try {
                while (true) {
                    XmlResult result = resultsQueue.poll(2, TimeUnit.SECONDS);
                    if (result == null) {
                        if (producerExecutor.isTerminated() && resultsQueue.isEmpty()) {
                            break;
                        }
                        continue;
                    }

                    if (result.xmlContent == null || result.xmlContent.trim().isEmpty()) {
                        log.error("Dropped empty XML for patient {}", result.patientId);
                        continue;
                    }

                    Path patientXml = outputDir.resolve("patient_" + result.patientId + ".xml");
                    try (OutputStream os = new BufferedOutputStream(
                            Files.newOutputStream(patientXml, StandardOpenOption.CREATE,
                                    StandardOpenOption.TRUNCATE_EXISTING))) {
                        os.write(result.xmlContent.getBytes(StandardCharsets.UTF_8));
                    }
                    generatedCount.incrementAndGet();
                }

            } catch (Exception e) {
                log.error("Writer thread error: {}", e.getMessage(), e);
            }
        };

        for (int i = 0; i < writerPoolSize; i++) {
            writerExecutor.submit(writerTask);
        }

        // --- Producer: generate XMLs ---
        for (String patientId : patientIds) {
            producerExecutor.submit(() -> {
                try {
                    String xml = getPatientNDRXml(patientId, facilityId, initial, ndrErrors, pushIdentifier);

                    if (xml != null && !xml.trim().isEmpty()) {
                        resultsQueue.put(new XmlResult(patientId, xml));
                    } else {
                        log.warn("Skipping patient {} - No XML generated", patientId);
                    }
                } catch (Exception e) {
                    log.error("Error generating XML: {}", e.getMessage());
                    ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
                }
                processedCount.incrementAndGet();
            });
        }

        // --- Wait for all tasks ---
        producerExecutor.shutdown();
        try {
            producerExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Producer pool interrupted", e);
        }

        writerExecutor.shutdown();
        try {
            writerExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Writer pool interrupted", e);
        }

        // --- Write Error Log ---
        if (!ndrErrors.isEmpty()) {

            Path errorFile = outputDir.resolve("errors").resolve("ndr_errors.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(errorFile, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (NDRErrorDTO err : ndrErrors) {
                    writer.write(String.format("PatientID=%s | Hospital No=%s | Error=%s%n",
                            err.getPatientUuid(), err.getHospitalNumber(), err.getErrorMessage()));
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed writing error log file: " + e.getMessage(), e);
            }
        }

        // --- Zip All XMLs for Download ---
        if (patientDemographicDTO[0] != null) {
            zipAndSaveTheFilesforDownload(
                    facilityId,
                    outputDir.toString(),
                    generatedCount,
                    patientDemographicDTO[0],
                    ndrErrors,
                    "treatment",
                    pushIdentifier
            );
        }

        log.info("NDR XML generation completed for facility {}. Total patients: {}. Generated: {}. Errors: {}",
                facilityId, patientIds.size(), generatedCount.get(), ndrErrors.size());
    }

    private String getPatientNDRXml(String patientId, Long facilityId, boolean initial, List<NDRErrorDTO> ndrErrors, String pushIdentifier) {
        try{
            Container container = createContainerForPatient(patientId, facilityId, initial, ndrErrors, pushIdentifier);

            if (container == null) {
                throw new IllegalStateException("Null container for patient " + patientId);
            }

            try(StringWriter writer = new StringWriter()){
                Marshaller marshaller = MARSHALLER_CACHE.get();
                synchronized (marshaller) {
                    marshaller.marshal(container, writer);
                }
                return writer.toString();
            }catch (Exception e) {
                throw new RuntimeException("Error marshalling patient " + patientId + ": " + e.getMessage());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error processing patient xml container " + patientId, e);
        }
    }
    private Container createContainerForPatient(
            String patientId,
            Long facilityId,
            boolean initial,
            List<NDRErrorDTO> ndrErrors,
            String pushIdentifier
    ) {
        try {
            long id = messageId.incrementAndGet();

            // --- Demographics ---
            PatientDemographicDTO patientDemographic = getPatientDemographic(patientId, facilityId, ndrErrors);
            if (patientDemographic == null) {
                String msg = "No demographic found for patient " + patientId;
                ndrErrors.add(new NDRErrorDTO(patientId, "", msg));
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            LocalDate start = LocalDate.of(1985, Month.JANUARY, 1);
            LocalDate end = LocalDate.now().plusDays(1);

            Optional<NdrMessageLog> messageLog =
                    data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "treatment");

            List<EncounterDTO> patientEncounters;
            List<RegimenDTO> patientRegimens;
            List<LaboratoryEncounterDTO> patientLabEncounters;

            // --- Fetch Encounters / Regimens / Labs ---
            if (!initial && messageLog.isPresent()) {
                start = messageLog.get().getLastUpdated().toLocalDate();

                patientEncounters = safeList(getPatientEncounters(patientId, facilityId, objectMapper, start, end, ndrErrors));
                if (patientEncounters.isEmpty()) {
                    patientEncounters = safeList(getPatientEncounters_lastRecord(patientId, facilityId, objectMapper, ndrErrors));
                }

                patientRegimens = safeList(getPatientRegimens(patientId, facilityId, objectMapper, start, end, ndrErrors));
                if (patientRegimens.isEmpty()) {
                    patientRegimens = safeList(getPatientLastRegimen(patientId, facilityId, objectMapper, ndrErrors));
                }

                patientLabEncounters = safeList(getPatientLabEncounter(patientId, facilityId, objectMapper, start, end, ndrErrors));
                if (patientLabEncounters.isEmpty()) {
                    patientLabEncounters = safeList(getPatientLastLabEncounter(patientId, facilityId, objectMapper, ndrErrors));
                }
            } else {
                patientEncounters = safeList(getPatientEncounters(patientId, facilityId, objectMapper, start, end, ndrErrors));
                patientRegimens = safeList(getPatientRegimens(patientId, facilityId, objectMapper, start, end, ndrErrors));
                patientLabEncounters = safeList(getPatientLabEncounter(patientId, facilityId, objectMapper, start, end, ndrErrors));
            }

            // --- Mortality ---
            MortalityType mortality = mortalityTypeMapper.getMortalityType(patientId, facilityId, start, end, ndrErrors);

            // --- Map Patient Demographics ---
            PatientDemographicsType patientDemographics =
                    patientDemographicsMapper.getPatientDemographics(patientDemographic);

            IndividualReportType individualReportType = new IndividualReportType();
            individualReportType.setPatientDemographics(patientDemographics);

            // --- Conditions ---
            ConditionType conditionType =
                    conditionTypeMapper.getConditionType(patientDemographic, patientEncounters, patientRegimens, patientLabEncounters);
            if (conditionType != null) {
                individualReportType.getCondition().add(conditionType);
            }

            // --- Mortality ---
            if (mortality != null) {
                //log.info("Mortality generated with visit Id {}", mortality.getVisitID());
                individualReportType.getMortality().add(mortality);
            }

            // --- HIV Testing Report (optional, safe check) ---
//        try {
//            log.warn("No HIV Testing data found for patient {}", patientId);
//        } catch (Exception e) {
//            log.error("Error mapping HIV Testing Report for patient {}", patientId, e);
//        }

            // --- PMTCT (optional, safe check) ---
//        try {
//            log.warn("No PMTCT data found for patient {}", patientId);
//        } catch (Exception e) {
//            log.error("Error mapping PMTCT section for patient {}", patientId, e);
//        }

            // --- Message Header ---
            MessageHeaderType messageHeader = messageHeaderTypeMapper.getMessageHeader(patientDemographic);
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

            return buildContainer(messageHeader, individualReportType);
        }catch (Exception e){
            throw new RuntimeException("Error mapping patient data to container" + patientId, e);
        }
    }

    /**
     * Build the container safely
     */
    private Container buildContainer(MessageHeaderType messageHeader, IndividualReportType individualReportType) {
        Container container = new Container();
        container.setMessageHeader(messageHeader);
        container.setIndividualReport(individualReportType);
        return container;
    }

    /**
     * Utility to avoid null lists
     */
    private <T> List<T> safeList(List<T> input) {
        return input != null ? input : Collections.emptyList();
    }

    private List<RegimenDTO> getPatientRegimenList(PatientPharmacyEncounterDTO pharmacyEncounterDTO, ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(pharmacyEncounterDTO.getRegimens(), typeFactory.constructCollectionType(List.class, RegimenDTO.class));
        } catch (Exception e) {
            log.error("Error reading regimens of patient with uuid {}  errorMsg {}",
                    pharmacyEncounterDTO.getPatientUuid(), e.getMessage());
            ndrErrors.add(new NDRErrorDTO(pharmacyEncounterDTO.getPatientUuid(), "", e.getMessage()));
        }
        return new ArrayList<>();
    }

    private List<EncounterDTO> getPatientEncounterDTOList(
            PatientEncounterDTO patientEncounterDTO,
            ObjectMapper objectMapper,
            List<NDRErrorDTO> ndrErrors) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(patientEncounterDTO.getEncounters(), typeFactory.constructCollectionType(List.class, EncounterDTO.class));
        } catch (Exception e) {
            log.error("Error reading encounters of patient with uuid {} errorMsg {}",
                    patientEncounterDTO.getPatientUuid(), e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientEncounterDTO.getPatientUuid(),
                    "", e.getMessage()));

        }
        return new ArrayList<>();
    }

    private PatientDemographicDTO getPatientDemographic(String patientId, long facilityId, List<NDRErrorDTO> ndrErrors) {
        try {
            PatientDemographicDTO patientDemographicDTO;
            Optional<PatientDemographicDTO> patientDemographicDTOOptional =
                    data.getPatientDemographics(patientId, facilityId);
            if (patientDemographicDTOOptional.isPresent()) {
                log.info("patient demographic information were retrieved successfully");
                patientDemographicDTO = patientDemographicDTOOptional.get();
                return patientDemographicDTO;
            }
        } catch (Exception e) {
            log.error("An error occur while fetching patient with uuid {} information error {}", patientId, e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return null;
    }

    private List<LaboratoryEncounterDTO> getPatientLabEncounterDTOList(
            PatientLabEncounterDTO laboratoryEncounter,
            ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(laboratoryEncounter.getLabs(),
                    typeFactory.constructCollectionType(List.class, LaboratoryEncounterDTO.class));
        } catch (Exception e) {
            log.error("Error reading lab encounters of patient with uuid {} errorMsg {}",
                    laboratoryEncounter.getPatientUuid(), e.getMessage());
            ndrErrors.add(new NDRErrorDTO(laboratoryEncounter.getPatientUuid(),
                    "", e.getMessage()));
        }
        return new ArrayList<>();
    }
    private List<RegimenDTO> getPatientRegimens(String patientId, long facilityId,
                                                ObjectMapper objectMapper, LocalDate start, LocalDate end, List<NDRErrorDTO> ndrErrors) {
        try {
            PatientPharmacyEncounterDTO patientPharmacyEncounterDTO;
            Optional<PatientPharmacyEncounterDTO> patientPharmacyEncounter =
                    data.getPatientPharmacyEncounter(patientId, facilityId, start, end);
            if (patientPharmacyEncounter.isPresent()) {
                patientPharmacyEncounterDTO = patientPharmacyEncounter.get();
                return getPatientRegimenList(patientPharmacyEncounterDTO, objectMapper, ndrErrors);
            }
        } catch (Exception e) {
            log.error("An error occurred while getting patient regimen list error {}", e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return new ArrayList<>();
    }
    private List<RegimenDTO> getPatientLastRegimen(String patientId, long facilityId, ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors) {
        try {
            PatientPharmacyEncounterDTO patientPharmacyEncounterDTO;
            Optional<PatientPharmacyEncounterDTO> patientPharmacyEncounter = data.getPatientLastPharmacyEncounter(patientId, facilityId);
            if (patientPharmacyEncounter.isPresent()) {
                patientPharmacyEncounterDTO = patientPharmacyEncounter.get();
                return getPatientRegimenList(patientPharmacyEncounterDTO, objectMapper, ndrErrors);
            }
        } catch (Exception e) {
            log.error("An error occurred while getting patient regimen list error {}", e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return new ArrayList<>();
    }

    private List<EncounterDTO> getPatientEncounters(
            String patientId,
            long facilityId,
            ObjectMapper objectMapper,
            LocalDate start, LocalDate end,
            List<NDRErrorDTO> ndrErrors) {
        PatientEncounterDTO patientEncounterDTO;
        Optional<PatientEncounterDTO> patientEncounter =
                data.getPatientEncounter(patientId, facilityId, start, end);
        if (patientEncounter.isPresent()) {
            patientEncounterDTO = patientEncounter.get();
            return getPatientEncounterDTOList(patientEncounterDTO, objectMapper, ndrErrors);
        }

        return new ArrayList<>();
    }

    private List<EncounterDTO> getPatientEncounters_lastRecord(String patientId, long facilityId, ObjectMapper objectMapper,
                                                               List<NDRErrorDTO> ndrErrors) {
        PatientEncounterDTO patientEncounterDTO;
        Optional<PatientEncounterDTO> patientEncounter = data.getPatientLastEncounter(patientId, facilityId);
        if (patientEncounter.isPresent()) {
            patientEncounterDTO = patientEncounter.get();
            return getPatientEncounterDTOList(patientEncounterDTO, objectMapper, ndrErrors);
        }
        return new ArrayList<>();
    }

    private List<LaboratoryEncounterDTO> getPatientLabEncounter(
            String patientId,
            long facilityId,
            ObjectMapper objectMapper,
            LocalDate start, LocalDate end,
            List<NDRErrorDTO> ndrErrors
    ){
        try{
            PatientLabEncounterDTO laboratoryEncounter;

            Optional<PatientLabEncounterDTO> patientLabEncounter =
                    data.getPatientLabEncounter(patientId, facilityId, start, end);

            if(patientLabEncounter.isPresent()){
                laboratoryEncounter = patientLabEncounter.get();
                return getPatientLabEncounterDTOList(laboratoryEncounter, objectMapper, ndrErrors);
            }

        }catch (Exception e) {
            log.error("An error occurred while getting patient Lab list error {}", e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return new ArrayList<>();
    }

    private List<LaboratoryEncounterDTO> getPatientLastLabEncounter(String patientId, long facilityId, ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors){
        try{
            PatientLabEncounterDTO laboratoryEncounter;
            Optional<PatientLabEncounterDTO> patientLabEncounter = data.getPatientLastLabEncounter(patientId, facilityId);
            if(patientLabEncounter.isPresent()){
                System.out.println("Last lab is present");
                laboratoryEncounter = patientLabEncounter.get();
                return getPatientLabEncounterDTOList(laboratoryEncounter, objectMapper, ndrErrors);
            }

        }catch (Exception e) {
            log.error("An error occurred while getting patient Lab list error {}", e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return new ArrayList<>();
    }


    public void zipAndSaveTheFilesforDownload(
            Long facilityId,
            String pathname,
            AtomicInteger count,
            PatientDemographicDTO patient, List<NDRErrorDTO> ndrErrors, String type, String identifier) {
        try {
            zipFiles(patient, facilityId, pathname, ndrErrors,type,identifier);
        } catch (Exception e) {
            log.error("An error occurred while zipping files error {}", e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patient.getPersonUuid(), patient.getHospitalNumber(), e.getMessage()));
        }
    }

    public void zipFiles(PatientDemographicDTO demographic,
                         long facilityId,
                         String sourceFolder,
                         List<NDRErrorDTO> ndrErrors, String type, String identifier) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String sCode = demographic.getStateCode();
        String lCode = demographic.getLgaCode();

        String fileName = StringUtils.leftPad(sCode, 2, "0") +
                StringUtils.leftPad(lCode, 3, "0") + "_" +
                demographic.getFacilityId() + "_" +
                demographic.getFacilityName() + "_" +
                type + "_" + dateFormat.format(new Date());

        fileName = RegExUtils.replaceAll(fileName, "/", "-");
        fileName = fileName.replace(" ", "").replace(",", "").replace(".", "");

        String outputZipFile = BASE_DIR + "ndr/" + fileName + ".zip";

        try {
            Files.createDirectories(Paths.get(BASE_DIR, "ndr")); // ensure folder exists

            List<File> files = ndrService.getFiles(sourceFolder, new ArrayList<>());
            long thirtyMB = (FileUtils.ONE_MB * 30); // 30MB

            File folder = new File(BASE_DIR + TEMP + facilityId + "/");

            if (ZipUtility.getFolderSize(folder) > thirtyMB) {
                List<List<File>> splitFiles = split(files, thirtyMB);
                for (int i = 0; i < splitFiles.size(); i++) {
                    String splitFileName = fileName + "_" + (i + 1) + ".zip";
                    Path splitPath = Paths.get(BASE_DIR, "ndr", splitFileName);
                    zip(splitFiles.get(i), splitPath.toAbsolutePath().toString());
                    storeTheFileInBD(facilityId, new AtomicInteger(splitFiles.get(i).size()), demographic, ndrErrors, splitFileName,type, identifier);
                }
            } else {
                ZipUtility.zip(files, Paths.get(outputZipFile).toAbsolutePath().toString(), thirtyMB);
                storeTheFileInBD(facilityId, new AtomicInteger(files.size()), demographic, ndrErrors, fileName,type, identifier);
            }

        } catch (Exception exception) {
            ndrErrors.add(new NDRErrorDTO(demographic.getPersonUuid(),
                    demographic.getHospitalNumber(), exception.getMessage()));
            log.error("An error occurred while creating zip file {}", outputZipFile, exception);
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

    public static void zip(List<File> files, String outputZipFile) throws java.io.IOException {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeTheFileInBD(Long facilityId, AtomicInteger count, PatientDemographicDTO patient,
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
    //end
}
