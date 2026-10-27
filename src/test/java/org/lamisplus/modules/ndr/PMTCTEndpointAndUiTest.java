package org.lamisplus.modules.ndr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.ndr.controller.NDRController;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PmtctDTO;
import org.lamisplus.modules.ndr.domain.dto.pmtct.DeliveryChildrenDetailsProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.InfantCohortRegistrationProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.MotherInfantPairVisitProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTEligiblePatientProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTRegisterProjection;
import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.mapper.PMTCTTypeMapper;
import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
import org.lamisplus.modules.ndr.repositories.NDRQueryRepository;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.service.*;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PMTCTEndpointAndUiTest {

    private NDRQueryRepository ndrQueryRepository;
    private PMTCTService pmtctService;
    private NDRController ndrController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        NdrMessageLogRepository messageLogRepository = Mockito.mock(NdrMessageLogRepository.class);
        ndrQueryRepository = Mockito.mock(NDRQueryRepository.class);
        NDRService ndrService = Mockito.mock(NDRService.class);
        MessageHeaderTypeMapper messageHeaderTypeMapper = Mockito.mock(MessageHeaderTypeMapper.class);
        PatientDemographicsMapper patientDemographicsMapper = Mockito.mock(PatientDemographicsMapper.class);
        ConditionTypeMapper conditionTypeMapper = Mockito.mock(ConditionTypeMapper.class);
        NdrXmlStatusRepository ndrXmlStatusRepository = Mockito.mock(NdrXmlStatusRepository.class);
        PMTCTTypeMapper pmtctTypeMapper = new PMTCTTypeMapper();
        NdrOptimizationService ndrOptimizationService = Mockito.mock(NdrOptimizationService.class);
        UserService userService = Mockito.mock(UserService.class);

        when(userService.getUserWithRoles()).thenReturn(Optional.empty());

        pmtctService = Mockito.spy(new PMTCTService(
                messageLogRepository,
                ndrQueryRepository,
                ndrService,
                messageHeaderTypeMapper,
                patientDemographicsMapper,
                conditionTypeMapper,
                ndrXmlStatusRepository,
                pmtctTypeMapper,
                ndrOptimizationService,
                userService
        ));

        ndrController = new NDRController(
                ndrService,
                ndrOptimizationService,
                Mockito.mock(SimpMessageSendingOperations.class),
                Mockito.mock(HtsService.class),
                Mockito.mock(RedactService.class),
                Mockito.mock(PrEPService.class),
                pmtctService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(ndrController).build();
    }

    @Test
    void shouldServePmtctEligiblePatientsForUiTable() throws Exception {
        PMTCTEligiblePatientProjection row1 = new PMTCTEligiblePatientProjection() {
            @Override
            public Long getPersonId() {
                return 12841L;
            }

            @Override
            public String getPersonUuid() {
                return "def0e08d-f76a-4d5a-9780-ae9f3beedfcd";
            }

            @Override
            public String getHospitalNumber() {
                return "AKS/SLHA/PMTCT/0001";
            }
        };

        when(ndrQueryRepository.getPmtctEligiblePatients(eq(Collections.singletonList(101L)), eq("PMTCT"), eq(10), eq(0)))
                .thenReturn(Collections.singletonList(row1));
        when(ndrQueryRepository.countPmtctEligiblePatients(eq(Collections.singletonList(101L)), eq("PMTCT")))
                .thenReturn(1L);

        String responseBody = mockMvc.perform(get("/api/v1/ndr/pmtct/patients")
                        .param("facilityIds", "101")
                        .param("pageSize", "10")
                        .param("pageNo", "0")
                        .param("searchValue", "PMTCT"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.fasterxml.jackson.databind.JsonNode json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
        assertEquals(1, json.get("totalRecords").asInt());
        assertEquals(0, json.get("pageNumber").asInt());
        assertEquals(10, json.get("pageSize").asInt());
        assertEquals(12841L, json.get("records").get(0).get("personId").asLong());
        assertEquals("def0e08d-f76a-4d5a-9780-ae9f3beedfcd", json.get("records").get(0).get("personUuid").asText());
        assertEquals("AKS/SLHA/PMTCT/0001", json.get("records").get(0).get("hospitalNumber").asText());
    }

    @Test
    void shouldTriggerPmtctGenerationEndpointsFromUi() throws Exception {
        doNothing().when(pmtctService).generatePatientsPmtctNDRXml(anyLong(), anyBoolean());
        doNothing().when(pmtctService).generateSelectedPatientsPmtctNDRXml(anyLong(), anyBoolean(), anyList());

        // 1. All patients in selected facility (when no patient rows are checked in Pmtct.js)
        mockMvc.perform(get("/api/v1/ndr/pmtct")
                        .param("facilityIds", "101")
                        .param("isInitial", "true"))
                .andExpect(status().isOk());
        verify(pmtctService, times(1)).generatePatientsPmtctNDRXml(101L, true);

        // 2. Selected patients from Eligible Patients table in Pmtct.js
        mockMvc.perform(get("/api/v1/ndr/generate/pmtct")
                        .param("facilityIds", "101")
                        .param("isInitial", "false")
                        .param("patientIds", "def0e08d-f76a-4d5a-9780-ae9f3beedfcd,abc0e08d-f76a-4d5a-9780-ae9f3beedf99"))
                .andExpect(status().isOk());
        verify(pmtctService, times(1)).generateSelectedPatientsPmtctNDRXml(
                eq(101L),
                eq(false),
                eq(Arrays.asList("def0e08d-f76a-4d5a-9780-ae9f3beedfcd", "abc0e08d-f76a-4d5a-9780-ae9f3beedf99"))
        );
    }

    @Test
    void shouldMapAndValidatePmtctContainerAgainstNdrXsd() throws Exception {
        PMTCTTypeMapper mapper = new PMTCTTypeMapper();

        PMTCTRegisterProjection reg = Mockito.mock(PMTCTRegisterProjection.class);
        when(reg.getVisitId()).thenReturn("reg-visit-001");
        when(reg.getVisitDate()).thenReturn(LocalDate.of(2026, 2, 10));
        when(reg.getHospitalNo()).thenReturn("AKS/SLHA/PMTCT/0001");
        when(reg.getAncNumber()).thenReturn("ANC-2026-001");
        when(reg.getPregnancyBreastfeedingStatus()).thenReturn("Pregnant");
        when(reg.getGestationalAgeAtBooking()).thenReturn(20);
        when(reg.getPreviouslyKnownHivStatus()).thenReturn("Known HIV+");
        when(reg.getCurrentlyOnArt()).thenReturn("Yes");
        when(reg.getDateOfInitiation()).thenReturn(LocalDate.of(2025, 6, 1));
        when(reg.getTimingOfArtInitiation()).thenReturn("1");
        when(reg.getSyphilisTestResult()).thenReturn("NR");
        when(reg.getHbvTestResult()).thenReturn("NR");
        when(reg.getTbScreeningStatus()).thenReturn("1");
        when(reg.getVlLessThan32WeeksDate()).thenReturn(LocalDate.of(2026, 3, 1));
        when(reg.getVlLessThan32WeeksResult()).thenReturn(new BigDecimal("20"));
        when(reg.getPartnerAgreedToNotification()).thenReturn("Yes");
        when(reg.getPartnerTestedHiv()).thenReturn("NR");

        MotherInfantPairVisitProjection visit = Mockito.mock(MotherInfantPairVisitProjection.class);
        when(visit.getVisitId()).thenReturn("pair-visit-001");
        when(visit.getVisitDate()).thenReturn(LocalDate.of(2026, 3, 15));
        when(visit.getGestationalAgeWeeks()).thenReturn(25);
        when(visit.getMotherWeightKg()).thenReturn(new BigDecimal("68.5"));
        when(visit.getSfhLengthCm()).thenReturn(new BigDecimal("25.0"));
        when(visit.getMotherCurrentStatus()).thenReturn("Active in PMTCT Cohort");
        when(visit.getMotherCurrentArtStatus()).thenReturn("On ART");
        when(visit.getRegimenCode()).thenReturn("1a");
        when(visit.getRegimenDescription()).thenReturn("TDF+3TC+DTG");

        DeliveryChildrenDetailsProjection delivery = Mockito.mock(DeliveryChildrenDetailsProjection.class);
        when(delivery.getDeliveryUuid()).thenReturn("del-001");
        when(delivery.getDateOfDelivery()).thenReturn(LocalDate.of(2026, 6, 20));
        when(delivery.getModeOfDelivery()).thenReturn("SVD");
        when(delivery.getBirthWeightKg()).thenReturn("3.2");
        when(delivery.getBirthLengthCm()).thenReturn(new BigDecimal("50"));
        when(delivery.getBirthOutcome()).thenReturn("Alive");
        when(delivery.getChildHospitalNumber()).thenReturn("AKS/SLHA/HEI/0001");
        when(delivery.getChildEntryPoint()).thenReturn("1");
        when(delivery.getSex()).thenReturn("Female");
        when(delivery.getChildGivenArvWithin72()).thenReturn("Yes");

        InfantCohortRegistrationProjection cohort = Mockito.mock(InfantCohortRegistrationProjection.class);
        when(cohort.getInfantUuid()).thenReturn("inf-001");
        when(cohort.getDateOfBirth()).thenReturn(LocalDate.of(2026, 6, 20));
        when(cohort.getDateOfFirstVisit()).thenReturn(LocalDate.of(2026, 8, 1));
        when(cohort.getAgeAtFirstVisit()).thenReturn(6);
        when(cohort.getChildHospitalRegNo()).thenReturn("AKS/SLHA/HEI/0001");
        when(cohort.getInfantSex()).thenReturn("Female");
        when(cohort.getChildEntryPoint()).thenReturn("1");
        when(cohort.getMotherHospitalNumber()).thenReturn("AKS/SLHA/PMTCT/0001");
        when(cohort.getMotherAncNumber()).thenReturn("ANC-2026-001");
        when(cohort.getMotherStatus()).thenReturn("HIV+");
        when(cohort.getMotherArtInitiationTiming()).thenReturn("1");
        when(cohort.getMotherRegimenCode()).thenReturn("1a");
        when(cohort.getMotherRegimenDescription()).thenReturn("TDF+3TC+DTG");

        PmtctDTO pmtctDTO = new PmtctDTO(
                Collections.singletonList(reg),
                Collections.singletonList(visit),
                Collections.singletonList(delivery),
                Collections.singletonList(cohort)
        );

        ObjectFactory objectFactory = new ObjectFactory();
        IndividualReportType individualReport = new IndividualReportType();
        PatientDemographicsType demographics = new PatientDemographicsType();
        demographics.setPatientIdentifier("DATIM123_def0e08d-f76a-4d5a-9780-ae9f3beedfcd");
        FacilityType treatmentFacility = new FacilityType();
        treatmentFacility.setFacilityName("St. Luke's Hospital Anua");
        treatmentFacility.setFacilityID("DATIM123");
        treatmentFacility.setFacilityTypeCode("FAC");
        demographics.setTreatmentFacility(treatmentFacility);
        demographics.setPatientDateOfBirth(DatatypeFactory.newInstance().newXMLGregorianCalendar("1995-04-12"));
        demographics.setPatientSexCode("F");
        individualReport.setPatientDemographics(demographics);

        ConditionType condition = new ConditionType();
        condition.setConditionCode("86406008");
        ProgramAreaType programArea = new ProgramAreaType();
        programArea.setProgramAreaCode("HIV");
        condition.setProgramArea(programArea);
        individualReport.getCondition().add(condition);

        List<NDRErrorDTO> errors = new ArrayList<>();
        boolean mapped = mapper.getPMTCTType(individualReport, objectFactory, pmtctDTO, errors, demographics.getPatientIdentifier());
        assertTrue(mapped, "PMTCTTypeMapper should map PMTCT records successfully");
        assertTrue(errors.isEmpty(), "PMTCTTypeMapper should not record any errors");
        assertEquals(1, individualReport.getPMTCT().size());

        Container container = new Container();
        MessageHeaderType header = new MessageHeaderType();
        header.setMessageStatusCode("INITIAL");
        header.setMessageCreationDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-10-27T09:00:00"));
        header.setMessageSchemaVersion(new BigDecimal("1.7"));
        header.setMessageUniqueID("1");
        FacilityType sendingOrg = new FacilityType();
        sendingOrg.setFacilityName("St. Luke's Hospital Anua");
        sendingOrg.setFacilityID("DATIM123");
        sendingOrg.setFacilityTypeCode("FAC");
        header.setMessageSendingOrganization(sendingOrg);
        container.setMessageHeader(header);
        container.setIndividualReport(individualReport);

        JAXBContext jaxbContext = JAXBContext.newInstance(Container.class, ObjectFactory.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR_XSD 1.7.2.0.xsd"));
        marshaller.setSchema(schema);

        StringWriter sw = new StringWriter();
        marshaller.marshal(container, sw);
        String xml = sw.toString();
        assertTrue(xml.contains("<PMTCT>"));
        assertTrue(xml.contains("<PMTCTRegister>"));
        assertTrue(xml.contains("<MotherInfantPairVisit>"));
        assertTrue(xml.contains("<DeliveryChildrenDetails>"));
        assertTrue(xml.contains("<InfantCohortRegistration>"));
    }

    @Test
    void shouldHavePmtctUiSourceAndStaticBundleWired() throws Exception {
        File pmtctSource = new File("src/main/webapp/jsx/components/ndr/Pmtct.js");
        assertTrue(pmtctSource.exists(), "Pmtct.js component must exist");
        String sourceContent = new String(Files.readAllBytes(pmtctSource.toPath()), StandardCharsets.UTF_8);
        assertTrue(sourceContent.contains("ndr/generate/pmtct?"));
        assertTrue(sourceContent.contains("ndr/pmtct?"));
        assertTrue(sourceContent.contains("ndr/pmtct/patients?"));
        assertTrue(sourceContent.contains("Generate PMTCT XML"));

        File indexHtml = new File("src/main/resources/views/static/ndr/index.html");
        assertTrue(indexHtml.exists());
        String html = new String(Files.readAllBytes(indexHtml.toPath()), StandardCharsets.UTF_8);
        assertTrue(html.contains("./static/js/2.5e7e1301.chunk.js"));
        assertTrue(html.contains("./static/js/main.616e913c.chunk.js"));

        File vendorChunk = new File("src/main/resources/views/static/ndr/static/js/2.5e7e1301.chunk.js");
        File mainChunk = new File("src/main/resources/views/static/ndr/static/js/main.616e913c.chunk.js");
        assertTrue(vendorChunk.exists(), "Referenced vendor chunk must exist");
        assertTrue(mainChunk.exists(), "Referenced main chunk must exist");

        String mainJs = new String(Files.readAllBytes(mainChunk.toPath()), StandardCharsets.UTF_8);
        assertTrue(mainJs.contains("PMTCT XML"));
        assertTrue(mainJs.contains("ndr/generate/pmtct?"));
        assertTrue(mainJs.contains("ndr/pmtct?"));
        assertTrue(mainJs.contains("ndr/pmtct/patients?"));
    }
}
