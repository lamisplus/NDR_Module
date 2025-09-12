package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.jsonwebtoken.io.IOException;
import liquibase.pro.packaged.C;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.mapper.MortalityTypeMapper;
import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.schema.*;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
                log.warn("JAXB Validation Error: {}", event.getMessage());
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

    public void generatePatientsNDRXml(long facilityId, boolean initial, List<String> patientUuidList){
        log.info("NDR XML Speed generation started.");
        LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
        String pushIdentifier = UUID.randomUUID().toString();
        List<String> patientIds = null;
        PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];
        List<String> idsNotGenerated = new LinkedList<>();
        AtomicInteger generatedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();

        if (initial) {
            if (patientUuidList != null) {
                generatePatientsNDRXml4Speed(patientUuidList, facilityId, true, pushIdentifier);
            }else{
                patientIds = data.getPatientIdsEligibleForNDR(start, LocalDateTime.now(), facilityId);
                log.info("generating initial ....");
                generatePatientsNDRXml4Speed(patientIds, facilityId, true, pushIdentifier);
            }
        }else { //updated
            log.info("generating updated....");
        }

        assert patientUuidList != null;
//        log.info("generated  {}/{}", generatedCount.get(), patientUuidList.size());
//        log.info("files not generated  {}/{}", errorCount.get(), patientUuidList.size());
//        log.info("patientIds of files not generated: {}", idsNotGenerated);
    }

    public void generatePatientsNDRXml4Speed(List<String> patientIds, Long facilityId, boolean initial, String pushIdentifier) {
        List<NDRErrorDTO> ndrErrors = Collections.synchronizedList(new ArrayList<>());
        if (patientIds == null || patientIds.isEmpty()) {
            log.warn("No patient IDs provided for NDR XML generation");
            return;
        }

        Path outputDir = Paths.get(BASE_DIR + TEMP, facilityId.toString());
        try {
            Files.createDirectories(outputDir);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create output directory: " + e);
        }

        int availableCores = Runtime.getRuntime().availableProcessors();
        int producerPoolSize = Math.max(4, availableCores * 2);
        int writerPoolSize = Math.max(2, availableCores / 2);
        log.info("{} cores available -> Producers: {} | writers: {}",
                availableCores, producerPoolSize, writerPoolSize);

        ExecutorService producerExecutor = Executors.newFixedThreadPool(producerPoolSize);
        ExecutorService writerExecutor = Executors.newFixedThreadPool(writerPoolSize);

        AtomicInteger processedCount = new AtomicInteger(0);


        BlockingQueue<XmlResult> resultsQueue = new LinkedBlockingQueue<>(2000);

        Runnable writerTask = () -> {
            long currentBatchSize = 0;
            int batchCounter = 1;
            Path currentBatchDir = outputDir.resolve("batch_" + batchCounter);

            try {
                Files.createDirectories(currentBatchDir);
            } catch (IOException | java.io.IOException e) {
                throw new RuntimeException("Failed to create initial batch directory", e);
            }

            try {
                while (true) {
                    XmlResult result = resultsQueue.poll(2, TimeUnit.SECONDS);
                    log.info("result worker.. {}", result);
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


                    byte[] data = result.xmlContent.getBytes(StandardCharsets.UTF_8);

                    if (currentBatchSize + data.length > MAX_BATCH_SIZE){
                        batchCounter++;
                        currentBatchDir = outputDir.resolve("batch_" + batchCounter);
                        Files.createDirectories(currentBatchDir);
                    }

                    Path patientXml = currentBatchDir.resolve("patient_" + result.patientId + ".xml");

                    try (OutputStream os = new BufferedOutputStream(
                            Files.newOutputStream(patientXml, StandardOpenOption.CREATE,
                                    StandardOpenOption.TRUNCATE_EXISTING)
                    )){
                        os.write(data);
                    }

                    currentBatchSize += data.length;
                }
            } catch (Exception e) {
                log.error("Writer thread error: " + e.getMessage());
                e.printStackTrace();
            }
        };

        for (int i = 0; i < writerPoolSize; i++) {
            writerExecutor.submit(writerTask);
        }

        for (String patientId : patientIds) {
            producerExecutor.submit(() -> {
                try{
                    String xml = getPatientNDRXml(patientId, facilityId, initial, ndrErrors, pushIdentifier);
                    log.info("xml {}", xml);
                    if (xml != null && !xml.trim().isEmpty()) {
                        resultsQueue.add(new XmlResult(patientId, xml));
                    }else {
                        log.warn("Skipping patient {} - No XML generated", patientId);
                    }
                }catch(Exception e){
                    log.error("Error generating XML: {}", e.getMessage());
                }
                processedCount.incrementAndGet();
            });
        }

        producerExecutor.shutdown();

        try {
            producerExecutor.awaitTermination(1, TimeUnit.HOURS);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Producer Execution pool interrupted", e);
        }

        writerExecutor.shutdown();

        try {
            writerExecutor.awaitTermination(1, TimeUnit.HOURS);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Writer Execution pool interrupted", e);
        }

        if (!ndrErrors.isEmpty()) {
            Path errorFile = outputDir.resolve("ndr_errors.txt");
            try(BufferedWriter writer = Files.newBufferedWriter(errorFile, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
                for (NDRErrorDTO err : ndrErrors) {
                    writer.write(String.format("PatientID=%s | Hospital No=%s | Error=%s%n",
                            err.getPatientUuid(), err.getHospitalNumber(), err.getErrorMessage()));
                }

            }catch(java.io.IOException e){
                throw new RuntimeException("Failed in writing error log ti file: {} ", e);
            }
        }

        log.info("NDR XML generation completed for facility {}. Total patients: {}. Errors: {}",
                facilityId, patientIds.size(), ndrErrors.size());
    }

    private void sanitizeContainer(Container container) {
        if (container.getMessageHeader() == null) {
            container.setMessageHeader(new MessageHeaderType());
        }

        if (container.getIndividualReport() == null) {
            container.setIndividualReport(new IndividualReportType());
        }

        IndividualReportType report = container.getIndividualReport();

        for (ConditionType cond : report.getCondition()) {
            if (cond.getEncounters() == null) {
                cond.setEncounters(new EncountersType());
            }
        }

    }

    private String getPatientNDRXml(String patientId, Long facilityId, boolean initial, List<NDRErrorDTO> ndrErrors, String pushIdentifier) {
        try{
            Container container = createContainerForPatient(patientId, facilityId, initial, ndrErrors, pushIdentifier);

            if (container == null) {
                throw new IllegalStateException("Null container for patient " + patientId);
            }

            sanitizeContainer(container);

            try(StringWriter writer = new StringWriter()){
                Marshaller marshaller = MARSHALLER_CACHE.get();
                synchronized (marshaller) {
                    marshaller.marshal(container, writer);
                }
                return writer.toString();
            }catch (Exception e) {
                throw new RuntimeException("Error marshalling patient " + patientId + ": " + e.getMessage());
            }

//            log.info("container {}", container);
//            Marshaller marshaller = MARSHALLER_CACHE.get();
//            StringWriter writer = new StringWriter();
//
//            synchronized (marshaller) {
//                marshaller.marshal(container, writer);
//            }
//
//            return writer.toString();

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
        log.info("in the container mapping");
        long id = messageId.incrementAndGet();

        // --- Demographics ---
        PatientDemographicDTO patientDemographic = getPatientDemographic(patientId, facilityId, ndrErrors);
        if (patientDemographic == null) {
            String msg = "No demographic found for patient " + patientId;
            log.warn("No demographic found for patient {} at facility {}", patientId, facilityId);
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
            log.info("Mortality generated with visit Id {}", mortality.getVisitID());
            individualReportType.getMortality().add(mortality);
        }

        // --- HIV Testing Report (optional, safe check) ---
        try {
            log.warn("No HIV Testing data found for patient {}", patientId);
        } catch (Exception e) {
            log.error("Error mapping HIV Testing Report for patient {}", patientId, e);
        }

        // --- PMTCT (optional, safe check) ---
        try {
            log.warn("No PMTCT data found for patient {}", patientId);
        } catch (Exception e) {
            log.error("Error mapping PMTCT section for patient {}", patientId, e);
        }

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

    //end
}
