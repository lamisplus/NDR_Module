//package org.lamisplus.modules.ndr;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.lamisplus.modules.base.service.UserService;
//import org.lamisplus.modules.ndr.controller.NDRController;
//import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
//import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
//import org.lamisplus.modules.ndr.domain.dto.PmtctDTO;
//import org.lamisplus.modules.ndr.domain.dto.pmtct.DeliveryChildrenDetailsProjection;
//import org.lamisplus.modules.ndr.domain.dto.pmtct.InfantCohortRegistrationProjection;
//import org.lamisplus.modules.ndr.domain.dto.pmtct.MotherInfantPairVisitProjection;
//import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTEligiblePatientProjection;
//import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTRegisterProjection;
//import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
//import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
//import org.lamisplus.modules.ndr.mapper.PMTCTTypeMapper;
//import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
//import org.lamisplus.modules.ndr.repositories.NDRQueryRepository;
//import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
//import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
//import org.lamisplus.modules.ndr.schema.*;
//import org.lamisplus.modules.ndr.service.*;
//import org.mockito.Mockito;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import javax.xml.XMLConstants;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Marshaller;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.validation.Schema;
//import javax.xml.validation.SchemaFactory;
//import java.io.File;
//import java.io.StringWriter;
//import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class PMTCTEndpointAndUiTest {
//
//    private NDRQueryRepository ndrQueryRepository;
//    private PMTCTService pmtctService;
//    private NDRController ndrController;
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setUp() {
//        NdrMessageLogRepository messageLogRepository = Mockito.mock(NdrMessageLogRepository.class);
//        ndrQueryRepository = Mockito.mock(NDRQueryRepository.class);
//        NDRService ndrService = Mockito.mock(NDRService.class);
//        MessageHeaderTypeMapper messageHeaderTypeMapper = Mockito.mock(MessageHeaderTypeMapper.class);
//        PatientDemographicsMapper patientDemographicsMapper = Mockito.mock(PatientDemographicsMapper.class);
//        ConditionTypeMapper conditionTypeMapper = Mockito.mock(ConditionTypeMapper.class);
//        NdrXmlStatusRepository ndrXmlStatusRepository = Mockito.mock(NdrXmlStatusRepository.class);
//        PMTCTTypeMapper pmtctTypeMapper = new PMTCTTypeMapper();
//        NdrOptimizationService ndrOptimizationService = Mockito.mock(NdrOptimizationService.class);
//        UserService userService = Mockito.mock(UserService.class);
//
//        when(userService.getUserWithRoles()).thenReturn(Optional.empty());
//
//        pmtctService = Mockito.spy(new PMTCTService(
//                messageLogRepository,
//                ndrQueryRepository,
//                ndrService,
//                messageHeaderTypeMapper,
//                patientDemographicsMapper,
//                conditionTypeMapper,
//                ndrXmlStatusRepository,
//                pmtctTypeMapper,
//                ndrOptimizationService,
//                userService
//        ));
//
//        ndrController = new NDRController(
//                ndrService,
//                ndrOptimizationService,
//                Mockito.mock(SimpMessageSendingOperations.class),
//                Mockito.mock(HtsService.class),
//                Mockito.mock(RedactService.class),
//                Mockito.mock(PrEPService.class),
//                pmtctService
//        );
//
//        mockMvc = MockMvcBuilders.standaloneSetup(ndrController).build();
//    }
//
//    @Test
//    void shouldServePmtctEligiblePatientsForUiTable() throws Exception {
//        PMTCTEligiblePatientProjection row1 = new PMTCTEligiblePatientProjection() {
//            @Override
//            public Long getPersonId() {
//                return 12841L;
//            }
//
//            @Override
//            public String getPersonUuid() {
//                return "def0e08d-f76a-4d5a-9780-ae9f3beedfcd";
//            }
//
//            @Override
//            public String getHospitalNumber() {
//                return "AKS/SLHA/PMTCT/0001";
//            }
//        };
//
//        when(ndrQueryRepository.getPmtctEligiblePatients(eq(Collections.singletonList(101L)), eq("PMTCT"), eq(10), eq(0)))
//                .thenReturn(Collections.singletonList(row1));
//        when(ndrQueryRepository.countPmtctEligiblePatients(eq(Collections.singletonList(101L)), eq("PMTCT")))
//                .thenReturn(1L);
//
//        String responseBody = mockMvc.perform(get("/api/v1/ndr/pmtct/patients")
//                        .param("facilityIds", "101")
//                        .param("pageSize", "10")
//                        .param("pageNo", "0")
//                        .param("searchValue", "PMTCT"))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        com.fasterxml.jackson.databind.JsonNode json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
//        assertEquals(1, json.get("totalRecords").asInt());
//        assertEquals(0, json.get("pageNumber").asInt());
//        assertEquals(10, json.get("pageSize").asInt());
//        assertEquals(12841L, json.get("records").get(0).get("personId").asLong());
//        assertEquals("def0e08d-f76a-4d5a-9780-ae9f3beedfcd", json.get("records").get(0).get("personUuid").asText());
//        assertEquals("AKS/SLHA/PMTCT/0001", json.get("records").get(0).get("hospitalNumber").asText());
//    }
//
//    @Test
//    void shouldTriggerPmtctGenerationEndpointsFromUi() throws Exception {
//        doNothing().when(pmtctService).generatePatientsPmtctNDRXml(anyLong(), anyBoolean());
//        doNothing().when(pmtctService).generateSelectedPatientsPmtctNDRXml(anyLong(), anyBoolean(), anyList());
//
//        // 1. All patients in selected facility (when no patient rows are checked in Pmtct.js)
//        mockMvc.perform(get("/api/v1/ndr/pmtct")
//                        .param("facilityIds", "101")
//                        .param("isInitial", "true"))
//                .andExpect(status().isOk());
//        verify(pmtctService, times(1)).generatePatientsPmtctNDRXml(101L, true);
//
//        // 2. Selected patients from Eligible Patients table in Pmtct.js
//        mockMvc.perform(get("/api/v1/ndr/generate/pmtct")
//                        .param("facilityIds", "101")
//                        .param("isInitial", "false")
//                        .param("patientIds", "def0e08d-f76a-4d5a-9780-ae9f3beedfcd,abc0e08d-f76a-4d5a-9780-ae9f3beedf99"))
//                .andExpect(status().isOk());
//        verify(pmtctService, times(1)).generateSelectedPatientsPmtctNDRXml(
//                eq(101L),
//                eq(false),
//                eq(Arrays.asList("def0e08d-f76a-4d5a-9780-ae9f3beedfcd", "abc0e08d-f76a-4d5a-9780-ae9f3beedf99"))
//        );
//    }
//
//    @Test
//    void shouldMapAndValidatePmtctContainerAgainstNdrXsd() throws Exception {
//        PMTCTTypeMapper mapper = new PMTCTTypeMapper();
//
//        PMTCTRegisterProjection reg = Mockito.mock(PMTCTRegisterProjection.class);
//        when(reg.getVisitId()).thenReturn("reg-visit-001");
//        when(reg.getVisitDate()).thenReturn(LocalDate.of(2026, 2, 10));
//        when(reg.getHospitalNo()).thenReturn("AKS/SLHA/PMTCT/0001");
//        when(reg.getAncNumber()).thenReturn("ANC-2026-001");
//        when(reg.getPregnancyBreastfeedingStatus()).thenReturn("Pregnant");
//        when(reg.getGestationalAgeAtBooking()).thenReturn(20);
//        when(reg.getPreviouslyKnownHivStatus()).thenReturn("Known HIV+");
//        when(reg.getCurrentlyOnArt()).thenReturn("Yes");
//        when(reg.getHivEarlyAcute()).thenReturn("YES_NO_NO");
//        when(reg.getDateOfInitiation()).thenReturn(LocalDate.of(2025, 6, 1));
//        when(reg.getTimingOfArtInitiation()).thenReturn("1");
//        when(reg.getSyphilisTestResult()).thenReturn("NR");
//        when(reg.getHbvTestResult()).thenReturn("NR");
//        when(reg.getTbScreeningStatus()).thenReturn("1");
//        when(reg.getVlLessThan32WeeksDate()).thenReturn(LocalDate.of(2026, 3, 1));
//        when(reg.getVlLessThan32WeeksResult()).thenReturn(new BigDecimal("20"));
//        when(reg.getPartnerAgreedToNotification()).thenReturn("Yes");
//        when(reg.getPartnerTestedHiv()).thenReturn("NR");
//
//        PMTCTRegisterProjection negAncReg = Mockito.mock(PMTCTRegisterProjection.class);
//        when(negAncReg.getVisitId()).thenReturn("8fbe2cdf-6f51-4107-9522-aa84671b30b6");
//        when(negAncReg.getVisitDate()).thenReturn(LocalDate.of(2026, 10, 12));
//        when(negAncReg.getHospitalNo()).thenReturn("FC13a2601012b1412cUyoo7");
//        when(negAncReg.getAncNumber()).thenReturn("FC13a2601012b1412cUyoo7");
//        when(negAncReg.getPregnancyBreastfeedingStatus()).thenReturn("Pregnant");
//        when(negAncReg.getGestationalAgeAtBooking()).thenReturn(25);
//        when(negAncReg.getPreviouslyKnownHivStatus()).thenReturn("No");
//        when(negAncReg.getHivEarlyAcute()).thenReturn("YES_NO_NO");
//        when(negAncReg.getHivTrAnc()).thenReturn("Negative");
//        when(negAncReg.getSyphilisTestResult()).thenReturn("SYPHILIS_RESULT_NEGATIVE");
//
//        String combinedPcrArrayJson = "["
//                + "{\"testType\":\"INFANT_TESTING_PCR_1ST_PCR_4-6_WEEKS_OF_AGE_OR_1ST_CONTACT\",\"ageAtTest\":\"2ND_PCR_CHILD_TEST_AGE_>_12_MONTHS\",\"dateSampleCollected\":\"2026-08-12\",\"dateResultReceivedByCaregiver\":[2026,8,13],\"results\":\"INFANT_PCR_RESULT_NEGATIVE\"},"
//                + "{\"testType\":\"INFANT_TESTING_PCR_2ND_PCR_12_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING_OR_AS_INDICATED\",\"ageAtTest\":\"CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS\",\"dateSampleCollected\":[2026,9,18],\"results\":\"INFANT_PCR_RESULT_NEGATIVE\"}"
//                + "]";
//
//        MotherInfantPairVisitProjection visit = Mockito.mock(MotherInfantPairVisitProjection.class);
//        when(visit.getVisitId()).thenReturn("pair-visit-001");
//        when(visit.getVisitDate()).thenReturn(LocalDate.of(2026, 3, 15));
//        when(visit.getGestationalAgeWeeks()).thenReturn(25);
//        when(visit.getMotherWeightKg()).thenReturn(new BigDecimal("68.5"));
//        when(visit.getSfhLengthCm()).thenReturn(new BigDecimal("25.0"));
//        when(visit.getMotherCurrentStatus()).thenReturn("Active in PMTCT Cohort");
//        when(visit.getMotherCurrentArtStatus()).thenReturn("On ART");
//        when(visit.getRegimenCode()).thenReturn("1a");
//        when(visit.getRegimenDescription()).thenReturn("TDF+3TC+DTG");
//
//        DeliveryChildrenDetailsProjection delivery = Mockito.mock(DeliveryChildrenDetailsProjection.class);
//        when(delivery.getDeliveryUuid()).thenReturn("del-001");
//        when(delivery.getDateOfDelivery()).thenReturn(LocalDate.of(2026, 6, 20));
//        when(delivery.getModeOfDelivery()).thenReturn("SVD");
//        when(delivery.getBirthWeightKg()).thenReturn("3.2");
//        when(delivery.getBirthLengthCm()).thenReturn(new BigDecimal("50"));
//        when(delivery.getBirthOutcome()).thenReturn("Alive");
//        when(delivery.getChildHospitalNumber()).thenReturn("AKS/SLHA/HEI/0001");
//        when(delivery.getChildEntryPoint()).thenReturn("1");
//        when(delivery.getSex()).thenReturn("Female");
//        when(delivery.getChildGivenArvWithin72()).thenReturn("Yes");
//        when(delivery.getInfantPcrDataJson()).thenReturn(combinedPcrArrayJson);
//
//        InfantCohortRegistrationProjection cohort = Mockito.mock(InfantCohortRegistrationProjection.class);
//        when(cohort.getInfantUuid()).thenReturn("inf-001");
//        when(cohort.getDateOfBirth()).thenReturn(LocalDate.of(2026, 6, 20));
//        when(cohort.getDateOfFirstVisit()).thenReturn(LocalDate.of(2026, 8, 1));
//        when(cohort.getAgeAtFirstVisit()).thenReturn(6);
//        when(cohort.getChildHospitalRegNo()).thenReturn("AKS/SLHA/HEI/0001");
//        when(cohort.getInfantSex()).thenReturn("Female");
//        when(cohort.getChildEntryPoint()).thenReturn("1");
//        when(cohort.getMotherHospitalNumber()).thenReturn("AKS/SLHA/PMTCT/0001");
//        when(cohort.getMotherAncNumber()).thenReturn("ANC-2026-001");
//        when(cohort.getMotherStatus()).thenReturn("HIV+");
//        when(cohort.getMotherArtInitiationTiming()).thenReturn("1");
//        when(cohort.getMotherRegimenCode()).thenReturn("1a");
//        when(cohort.getMotherRegimenDescription()).thenReturn("TDF+3TC+DTG");
//        when(cohort.getInfantPcrDataJson()).thenReturn(combinedPcrArrayJson);
//
//        PmtctDTO pmtctDTO = new PmtctDTO(
//                Arrays.asList(reg, negAncReg),
//                Collections.singletonList(visit),
//                Collections.singletonList(delivery),
//                Collections.singletonList(cohort)
//        );
//
//        ObjectFactory objectFactory = new ObjectFactory();
//        IndividualReportType individualReport = new IndividualReportType();
//        PatientDemographicsType demographics = new PatientDemographicsType();
//        demographics.setPatientIdentifier("DATIM123_def0e08d-f76a-4d5a-9780-ae9f3beedfcd");
//        FacilityType treatmentFacility = new FacilityType();
//        treatmentFacility.setFacilityName("St. Luke's Hospital Anua");
//        treatmentFacility.setFacilityID("DATIM123");
//        treatmentFacility.setFacilityTypeCode("FAC");
//        demographics.setTreatmentFacility(treatmentFacility);
//        demographics.setPatientDateOfBirth(DatatypeFactory.newInstance().newXMLGregorianCalendar("1995-04-12"));
//        demographics.setPatientSexCode("F");
//        individualReport.setPatientDemographics(demographics);
//
//        ConditionType condition = new ConditionType();
//        condition.setConditionCode("86406008");
//        ProgramAreaType programArea = new ProgramAreaType();
//        programArea.setProgramAreaCode("HIV");
//        condition.setProgramArea(programArea);
//        individualReport.getCondition().add(condition);
//
//        List<NDRErrorDTO> errors = new ArrayList<>();
//        boolean mapped = mapper.getPMTCTType(individualReport, objectFactory, pmtctDTO, errors, demographics.getPatientIdentifier());
//        assertTrue(mapped, "PMTCTTypeMapper should map PMTCT records successfully");
//        assertTrue(errors.isEmpty(), "PMTCTTypeMapper should not record any errors");
//        assertEquals(1, individualReport.getPMTCT().size());
//        assertEquals(KnownHIVPositiveStatusType.ON_ART, individualReport.getPMTCT().get(0).getPMTCTRegister().get(0).getKnownHIVPositive(),
//                "Known HIV+ mother on ART must have KnownHIVPositive=OnART");
//        assertNull(individualReport.getPMTCT().get(0).getPMTCTRegister().get(1).getKnownHIVPositive(),
//                "HIV-negative ANC client with previouslyKnownHivStatus=No must not have KnownHIVPositive set");
//        assertEquals("NotSuspected", individualReport.getPMTCT().get(0).getPMTCTRegister().get(1).getHIVEarlyAcute());
//        assertEquals(2, individualReport.getPMTCT().get(0).getDeliveryChildrenDetails().get(0).getDNAPCR().size());
//        assertNotNull(individualReport.getPMTCT().get(0).getInfantCohortRegistration().get(0).getFirstPCR());
//        assertNotNull(individualReport.getPMTCT().get(0).getInfantCohortRegistration().get(0).getSecondPCR());
//
//        Container container = new Container();
//        MessageHeaderType header = new MessageHeaderType();
//        header.setMessageStatusCode("INITIAL");
//        header.setMessageCreationDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-10-27T09:00:00"));
//        header.setMessageSchemaVersion(new BigDecimal("1.7"));
//        header.setMessageUniqueID("1");
//        FacilityType sendingOrg = new FacilityType();
//        sendingOrg.setFacilityName("St. Luke's Hospital Anua");
//        sendingOrg.setFacilityID("DATIM123");
//        sendingOrg.setFacilityTypeCode("FAC");
//        header.setMessageSendingOrganization(sendingOrg);
//        container.setMessageHeader(header);
//        container.setIndividualReport(individualReport);
//
//        JAXBContext jaxbContext = JAXBContext.newInstance(Container.class, ObjectFactory.class);
//        Marshaller marshaller = jaxbContext.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR_XSD 1.7.2.0.xsd"));
//        marshaller.setSchema(schema);
//
//        StringWriter sw = new StringWriter();
//        marshaller.marshal(container, sw);
//        String xml = sw.toString();
//        File sampleXmlFile = new File("target/sample-pmtct-ndr.xml");
//        Files.write(sampleXmlFile.toPath(), xml.getBytes(StandardCharsets.UTF_8));
//        assertTrue(sampleXmlFile.exists());
//        assertTrue(xml.contains("<PMTCT>"));
//        assertTrue(xml.contains("<PMTCTRegister>"));
//        assertTrue(xml.contains("<HIVEarlyAcute>NotSuspected</HIVEarlyAcute>"));
//        assertTrue(xml.contains("<MotherInfantPairVisit>"));
//        assertTrue(xml.contains("<DeliveryChildrenDetails>"));
//        assertTrue(xml.contains("<InfantCohortRegistration>"));
//        assertTrue(xml.contains("<FirstPCR>"));
//        assertTrue(xml.contains("<SecondPCR>"));
//    }
//
//    @Test
//    void shouldGenerateAndValidatePndr1XmlAgainstNdrXsd() throws Exception {
//        PMTCTTypeMapper mapper = new PMTCTTypeMapper();
//
//        PMTCTRegisterProjection pndr1Reg = Mockito.mock(PMTCTRegisterProjection.class);
//        when(pndr1Reg.getVisitId()).thenReturn("f898945e-4932-4bb5-895e-a6b015ee99b8");
//        when(pndr1Reg.getVisitDate()).thenReturn(LocalDate.of(2024, 10, 1));
//        when(pndr1Reg.getHospitalNo()).thenReturn("PNDR-1");
//        when(pndr1Reg.getAncNumber()).thenReturn("PNDR-1");
//        when(pndr1Reg.getPregnancyBreastfeedingStatus()).thenReturn("P");
//        when(pndr1Reg.getGestationalAgeAtBooking()).thenReturn(4);
//        when(pndr1Reg.getPreviouslyKnownHivStatus()).thenReturn("No");
//        when(pndr1Reg.getCurrentlyOnArt()).thenReturn("Yes");
//        when(pndr1Reg.getHivEarlyAcute()).thenReturn("YES_NO_NO");
//        when(pndr1Reg.getHivTrAnc()).thenReturn("Positive");
//        when(pndr1Reg.getDateOfInitiation()).thenReturn(LocalDate.of(2024, 10, 1));
//        when(pndr1Reg.getTimingOfArtInitiation()).thenReturn("TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD");
//        when(pndr1Reg.getSyphilisTestResult()).thenReturn("SYPHILIS_RESULT_POSITIVE");
//        when(pndr1Reg.getSyphilisTreated()).thenReturn("Yes");
//        when(pndr1Reg.getSyphilisReferred()).thenReturn("Yes");
//        when(pndr1Reg.getHbvKnownPositive()).thenReturn("Yes");
//        when(pndr1Reg.getHbvTestResult()).thenReturn("Negative");
//        when(pndr1Reg.getHbvTreated()).thenReturn("New on Prophylaxis");
//        when(pndr1Reg.getHbvReferred()).thenReturn("No");
//        when(pndr1Reg.getInitiatedOnProphylaxis()).thenReturn("New on Prophylaxis");
//        when(pndr1Reg.getTbScreeningStatus()).thenReturn("Presumptive TB and Referred for evaluation");
//        when(pndr1Reg.getVl32To36WeeksDate()).thenReturn(LocalDate.of(2025, 2, 5));
//        when(pndr1Reg.getVl32To36WeeksResult()).thenReturn(new BigDecimal("19"));
//        when(pndr1Reg.getVlBreastfeedingDate()).thenReturn(LocalDate.of(2025, 7, 13));
//        when(pndr1Reg.getVlBreastfeedingResult()).thenReturn(new BigDecimal("19"));
//        when(pndr1Reg.getPartnerAgreedToNotification()).thenReturn("Yes");
//        when(pndr1Reg.getPartnerTestedHiv()).thenReturn("Negative");
//        when(pndr1Reg.getPartnerTestedSyphilis()).thenReturn("Positive");
//        when(pndr1Reg.getPartnerTestedHepatitisB()).thenReturn("Negative");
//        when(pndr1Reg.getPartnerReferral()).thenReturn("PARTNER_REFERRED_PMTCT_PREP");
//
//        MotherInfantPairVisitProjection visit1 = Mockito.mock(MotherInfantPairVisitProjection.class);
//        when(visit1.getVisitId()).thenReturn("a6a5e17b-5832-4c76-9f61-5c5c70562e75");
//        when(visit1.getVisitDate()).thenReturn(LocalDate.of(2025, 2, 5));
//        when(visit1.getGestationalAgeWeeks()).thenReturn(33);
//        when(visit1.getMotherWeightKg()).thenReturn(new BigDecimal("96"));
//        when(visit1.getSfhLengthCm()).thenReturn(new BigDecimal("11"));
//        when(visit1.getMotherCurrentStatus()).thenReturn("Pregnant");
//        when(visit1.getMotherCurrentArtStatus()).thenReturn("Active");
//        when(visit1.getRegimenCode()).thenReturn("1c");
//        when(visit1.getRegimenDescription()).thenReturn("ABC+3TC+DTG");
//        when(visit1.getRegimenNdrCode()).thenReturn("NDR1c0009");
//        when(visit1.getMotherCurrentHbvStatus()).thenReturn("On Prophylaxis");
//        when(visit1.getMotherCurrentHbvDrugName()).thenReturn("drug b");
//        when(visit1.getMotherCurrentSyphilisStatus()).thenReturn("Treated");
//        when(visit1.getSyphilisDrugAdministered()).thenReturn("drug s");
//        when(visit1.getVlDateSampleCollected()).thenReturn(LocalDate.of(2025, 2, 5));
//        when(visit1.getVlDateResultReceived()).thenReturn(LocalDate.of(2025, 2, 12));
//        when(visit1.getVlResultCopiesPerMl()).thenReturn(new BigDecimal("19"));
//        when(visit1.getNextAppointmentDate()).thenReturn(LocalDate.of(2025, 2, 26));
//
//        MotherInfantPairVisitProjection visit2 = Mockito.mock(MotherInfantPairVisitProjection.class);
//        when(visit2.getVisitId()).thenReturn("38108297-1d17-4191-81a9-5780425be525");
//        when(visit2.getVisitDate()).thenReturn(LocalDate.of(2025, 7, 13));
//        when(visit2.getGestationalAgeWeeks()).thenReturn(23);
//        when(visit2.getMotherWeightKg()).thenReturn(new BigDecimal("88"));
//        when(visit2.getSfhLengthCm()).thenReturn(new BigDecimal("35"));
//        when(visit2.getMotherCurrentStatus()).thenReturn("Breastfeeding");
//        when(visit2.getMotherCurrentArtStatus()).thenReturn("Active");
//        when(visit2.getRegimenCode()).thenReturn("1c");
//        when(visit2.getRegimenDescription()).thenReturn("ABC+3TC+DTG");
//        when(visit2.getRegimenNdrCode()).thenReturn("NDR1c0009");
//        when(visit2.getMotherCurrentHbvStatus()).thenReturn("Negative");
//        when(visit2.getMotherCurrentSyphilisStatus()).thenReturn("Negative");
//        when(visit2.getVlDateSampleCollected()).thenReturn(LocalDate.of(2025, 7, 13));
//        when(visit2.getVlDateResultReceived()).thenReturn(LocalDate.of(2025, 7, 14));
//        when(visit2.getVlResultCopiesPerMl()).thenReturn(new BigDecimal("19"));
//        when(visit2.getInfantFeedingPractice()).thenReturn("FEEDING_PRACTICE_EXCLUSIVE_BREASTFEEDING_(EBF)");
//        when(visit2.getInfantOnCtx()).thenReturn("Yes");
//        when(visit2.getNextAppointmentDate()).thenReturn(LocalDate.of(2025, 7, 14));
//
//        String babyInfoJson = "{\"sexOfBaby\": \"\", \"babyPreterm\": \"\", \"babyAbortion\": \"\", "
//                + "\"deadOutcomes\": [{\"babyAbortion\": \"SA\", \"babyStillBirthType\": \"FSB\", \"babyDeadWithin7Days\": \"Yes\", \"babyNotBreathingAtBirth\": \"No\"}], "
//                + "\"aliveOutcomes\": [{\"sexOfBaby\": \"Male\", \"babyPreterm\": \"Yes\", \"babyResuscitated\": \"Yes\", \"babyTimeOfDelivery\": \"10:44\", \"babyLiveBirthWeight\": \"Less than 2.5kg\", \"babyNotBreathingAtBirth\": \"\", \"babyLiveBirthHivPositive\": \"Yes\"}]}";
//
//        DeliveryChildrenDetailsProjection twinDead = Mockito.mock(DeliveryChildrenDetailsProjection.class);
//        when(twinDead.getDeliveryUuid()).thenReturn("0535122b-cd1d-4704-87eb-584e481f4dad");
//        when(twinDead.getInfantUuid()).thenReturn("3d808f64-7f95-4d91-b038-1b8a42797e0a");
//        when(twinDead.getDateOfDelivery()).thenReturn(LocalDate.of(2025, 1, 1));
//        when(twinDead.getModeOfDelivery()).thenReturn("MODE_DELIVERY_NORMAL_VAGINAL_DELIVERY");
//        when(twinDead.getBirthWeightKg()).thenReturn("1.2");
//        when(twinDead.getBirthLengthCm()).thenReturn(new BigDecimal("32"));
//        when(twinDead.getBirthOutcome()).thenReturn("Dead");
//        when(twinDead.getChildHospitalNumber()).thenReturn("PNDR-1/2");
//        when(twinDead.getChildEntryPoint()).thenReturn("ANC");
//        when(twinDead.getSex()).thenReturn("Male");
//        when(twinDead.getBabyInfoJson()).thenReturn(babyInfoJson);
//
//        String pndr1InfantArvJson = "{\"ageAtCtx\": \"0\", \"dateOfArv\": [2025, 1, 1], \"dateOfCtx\": [2025, 1, 1], "
//                + "\"infantArvTime\": \"Within 72 hour\", \"infantArvType\": \"TYPE_PROPHYLAXIS_AZT_+_3TC_+_NVP_(TRIPLE)\", "
//                + "\"arvDeliveryPoint\": \"Facility Delivery\"}";
//        String pndr1HbvVacJson = "[{\"dose\": \"First Dose (Birth Dose)\", \"ageAtVaccination\": null, \"dateOfVaccination\": \"2025-01-01\"}]";
//        String pndr1VisitHbvJson = "{\"firstDoseDate\": \"2025-01-01\", \"secondDoseDate\": \"2025-02-05\", \"thirdDoseDate\": \"2025-12-01\"}";
//        String pndr1CombinedPcrJson = "["
//                + "{\"results\": \"INFANT_PCR_RESULT_NEGATIVE\", \"testType\": \"INFANT_TESTING_PCR_1ST_PCR_4-6_WEEKS_OF_AGE_OR_1ST_CONTACT\", \"ageAtTest\": \"CHILD_TEST_AGE_<_72_HRS\", \"dateSampleCollected\": [2025, 1, 1], \"dateResultReceivedByCaregiver\": [2025, 1, 1]}, "
//                + "["
//                + "{\"results\": \"INFANT_PCR_RESULT_NEGATIVE\", \"testType\": \"INFANT_TESTING_PCR_2ND_PCR_12_WEEKS_AFTER_CESSATION_OF_BREASTFEEDING_OR_AS_INDICATED\", \"ageAtTest\": \"CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS\", \"dateSampleCollected\": [2025, 2, 5], \"dateResultReceivedByCaregiver\": [2025, 2, 19]}, "
//                + "{\"results\": \"INFANT_PCR_RESULT_POSITIVE\", \"testType\": \"INFANT_TESTING_PCR_CONFIRMATORY_PCR___IF_PREVIOUS_TEST_POSITIVE\", \"ageAtTest\": \"CHILD_TEST_AGE_2-12_MONTHS\", \"dateSampleCollected\": [2025, 12, 1], \"dateResultReceivedByCaregiver\": [2025, 12, 15]}, "
//                + "{\"results\": \"INFANT_PCR_RESULT_POSITIVE\", \"testType\": \"INFANT_TESTING_PCR_CONFIRMATORY_PCR\", \"ageAtTest\": \"CHILD_TEST_AGE_>12_MONTHS\", \"dateSampleCollected\": [2026, 7, 1], \"dateResultReceivedByCaregiver\": [2026, 7, 12]}"
//                + "]"
//                + "]";
//        String pndr1RapidJson = "{\"result\": \"INFANT_PCR_RESULT_POSITIVE\", \"ageAtTest\": \"CHILD_TEST_AGE_>12_MONTHS\", \"dateOfTest\": [2026, 7, 1], \"rapidTestType\": \"Second Rapid Antibody\"}";
//
//        DeliveryChildrenDetailsProjection twinAlive = Mockito.mock(DeliveryChildrenDetailsProjection.class);
//        when(twinAlive.getDeliveryUuid()).thenReturn("0535122b-cd1d-4704-87eb-584e481f4dad");
//        when(twinAlive.getInfantUuid()).thenReturn("e8f0f9a8-92d0-49cb-a307-5e92f21a79e4");
//        when(twinAlive.getDateOfDelivery()).thenReturn(LocalDate.of(2025, 1, 1));
//        when(twinAlive.getModeOfDelivery()).thenReturn("MODE_DELIVERY_NORMAL_VAGINAL_DELIVERY");
//        when(twinAlive.getBirthWeightKg()).thenReturn("1.8");
//        when(twinAlive.getBirthLengthCm()).thenReturn(new BigDecimal("43"));
//        when(twinAlive.getBirthOutcome()).thenReturn("Alive");
//        when(twinAlive.getChildHospitalNumber()).thenReturn("PNDR-1/1");
//        when(twinAlive.getChildEntryPoint()).thenReturn("ANC");
//        when(twinAlive.getSex()).thenReturn("Male");
//        when(twinAlive.getBabyInfoJson()).thenReturn(babyInfoJson);
//        when(twinAlive.getInfantArvDataJson()).thenReturn(pndr1InfantArvJson);
//        when(twinAlive.getHbvVaccinationsJson()).thenReturn(pndr1HbvVacJson);
//        when(twinAlive.getVisitHbvVaccinationDataJson()).thenReturn(pndr1VisitHbvJson);
//        when(twinAlive.getInfantPcrDataJson()).thenReturn(pndr1CombinedPcrJson);
//        when(twinAlive.getRapidTestDataJson()).thenReturn(pndr1RapidJson);
//        when(twinAlive.getOutcomeAt18Months()).thenReturn("INFANT_OUTCOME_AT_18_MONTHS_HIV-POSITIVE_NOT_LINKED_TO_ART");
//
//        InfantCohortRegistrationProjection cohortAlive = Mockito.mock(InfantCohortRegistrationProjection.class);
//        when(cohortAlive.getInfantUuid()).thenReturn("e8f0f9a8-92d0-49cb-a307-5e92f21a79e4");
//        when(cohortAlive.getDateOfBirth()).thenReturn(LocalDate.of(2025, 1, 1));
//        when(cohortAlive.getDateOfFirstVisit()).thenReturn(LocalDate.of(2025, 2, 5));
//        when(cohortAlive.getAgeAtFirstVisit()).thenReturn(5);
//        when(cohortAlive.getChildHospitalRegNo()).thenReturn("PNDR-1/1");
//        when(cohortAlive.getInfantSex()).thenReturn("Male");
//        when(cohortAlive.getChildEntryPoint()).thenReturn("ANC");
//        when(cohortAlive.getMotherHospitalNumber()).thenReturn("PNDR-1");
//        when(cohortAlive.getMotherAncNumber()).thenReturn("PNDR-1");
//        when(cohortAlive.getMotherStatus()).thenReturn("Hepatitis B+");
//        when(cohortAlive.getMotherArtInitiationTiming()).thenReturn("TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD");
//        when(cohortAlive.getMotherRegimenCode()).thenReturn("1c");
//        when(cohortAlive.getMotherRegimenDescription()).thenReturn("ABC+3TC+DTG");
//        when(cohortAlive.getMotherRegimenNdrCode()).thenReturn("NDR1c0009");
//        when(cohortAlive.getMotherSyphilisTreatmentStartDate()).thenReturn("2026-07-01");
//        when(cohortAlive.getMotherHepatitisBTreatmentStartDate()).thenReturn("2026-07-01");
//        when(cohortAlive.getHbvVaccinationsJson()).thenReturn(pndr1HbvVacJson);
//        when(cohortAlive.getVisitHbvVaccinationDataJson()).thenReturn(pndr1VisitHbvJson);
//        when(cohortAlive.getInfantArvDataJson()).thenReturn(pndr1InfantArvJson);
//        when(cohortAlive.getInfantPcrDataJson()).thenReturn(pndr1CombinedPcrJson);
//        when(cohortAlive.getRapidTestDataJson()).thenReturn(pndr1RapidJson);
//        when(cohortAlive.getOutcomeAt18Months()).thenReturn("INFANT_OUTCOME_AT_18_MONTHS_HIV-POSITIVE_NOT_LINKED_TO_ART");
//
//        PmtctDTO pmtctDTO = new PmtctDTO(
//                Collections.singletonList(pndr1Reg),
//                Arrays.asList(visit1, visit2),
//                Arrays.asList(twinDead, twinAlive),
//                Collections.singletonList(cohortAlive)
//        );
//
//        ObjectFactory objectFactory = new ObjectFactory();
//        IndividualReportType individualReport = new IndividualReportType();
//        PatientDemographicsType demographics = new PatientDemographicsType();
//        demographics.setPatientIdentifier("SvuAvAPSwgQ_b0a00ca4-425a-4e64-9420-08ecd723ede3");
//        FacilityType treatmentFacility = new FacilityType();
//        treatmentFacility.setFacilityName("St Luke's Hospital Anua");
//        treatmentFacility.setFacilityID("SvuAvAPSwgQ");
//        treatmentFacility.setFacilityTypeCode("FAC");
//        demographics.setTreatmentFacility(treatmentFacility);
//        demographics.setPatientDateOfBirth(DatatypeFactory.newInstance().newXMLGregorianCalendar("2000-01-01"));
//        demographics.setPatientSexCode("F");
//        individualReport.setPatientDemographics(demographics);
//
//        ConditionType condition = new ConditionType();
//        condition.setConditionCode("86406008");
//        ProgramAreaType programArea = new ProgramAreaType();
//        programArea.setProgramAreaCode("HIV");
//        condition.setProgramArea(programArea);
//        individualReport.getCondition().add(condition);
//
//        List<NDRErrorDTO> errors = new ArrayList<>();
//        boolean mapped = mapper.getPMTCTType(individualReport, objectFactory, pmtctDTO, errors, demographics.getPatientIdentifier());
//        assertTrue(mapped, "PNDR-1 should map successfully");
//        assertTrue(errors.isEmpty(), "PNDR-1 mapping should not produce errors");
//
//        PMTCTType pmtct = individualReport.getPMTCT().get(0);
//        assertEquals("P", pmtct.getPMTCTRegister().get(0).getPregnancyBreastfeedingStatus());
//        assertEquals(2, pmtct.getDeliveryChildrenDetails().size());
//        assertEquals(0, pmtct.getDeliveryChildrenDetails().get(0).getDNAPCR().size(), "Dead twin PNDR-1/2 should not have PNDR-1/1's PCRs");
//        assertEquals(2, pmtct.getDeliveryChildrenDetails().get(1).getDNAPCR().size(), "Alive twin PNDR-1/1 should have 1st and 2nd DNA PCRs");
//        assertNotNull(pmtct.getDeliveryChildrenDetails().get(1).getHBVVaccination().getFirstDoseDate());
//        assertEquals("Within24hrs", pmtct.getDeliveryChildrenDetails().get(1).getHBVVaccination().getFirstDoseTiming());
//        assertNotNull(pmtct.getDeliveryChildrenDetails().get(1).getHBVVaccination().getSecondDoseDate());
//        assertNotNull(pmtct.getDeliveryChildrenDetails().get(1).getHBVVaccination().getThirdDoseDate());
//
//        assertEquals(1, pmtct.getInfantCohortRegistration().size());
//        assertEquals("LT36", pmtct.getPMTCTRegister().get(0).getTimingOfArtInitiation());
//        assertEquals(Boolean.TRUE, pmtct.getPMTCTRegister().get(0).getPartnerNotification().isReferredToPrEP());
//        InfantCohortRegistrationType cohortXml = pmtct.getInfantCohortRegistration().get(0);
//        assertEquals(5, cohortXml.getAgeAtFirstVisit());
//        assertEquals("InitiatedLessThan36Weeks", cohortXml.getMotherARTInitiationTiming());
//        assertNotNull(cohortXml.getFirstPCR());
//        assertNotNull(cohortXml.getSecondPCR());
//        assertNotNull(cohortXml.getConfirmatoryPCR());
//        assertNotNull(cohortXml.getRapidAntibodyTest());
//        assertEquals(Boolean.TRUE, cohortXml.getInfantHBVProphylaxis().isBirthDoseWithin24Hrs());
//        assertEquals(Boolean.TRUE, cohortXml.getInfantHBVProphylaxis().isSecondDoseGiven());
//        assertEquals(Boolean.TRUE, cohortXml.getInfantHBVProphylaxis().isThirdDoseGiven());
//        assertNotNull(cohortXml.getMotherSyphilisTreatmentStartDate());
//        assertNotNull(cohortXml.getMotherHepatitisBTreatmentStartDate());
//        assertEquals(InfantOutcomeStatusType.HIV_POSITIVE_NOT_LINKED_TO_ART, cohortXml.getOutcomeAt18Months());
//
//        Container container = new Container();
//        MessageHeaderType header = new MessageHeaderType();
//        header.setMessageStatusCode("INITIAL");
//        header.setMessageCreationDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-11-04T12:00:00"));
//        header.setMessageSchemaVersion(new BigDecimal("1.7"));
//        header.setMessageUniqueID("b0a00ca4-425a-4e64-9420-08ecd723ede3");
//        header.setMessageSendingOrganization(treatmentFacility);
//        container.setMessageHeader(header);
//        container.setIndividualReport(individualReport);
//
//        JAXBContext jaxbContext = JAXBContext.newInstance(Container.class, ObjectFactory.class);
//        Marshaller marshaller = jaxbContext.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR_XSD 1.7.2.0.xsd"));
//        marshaller.setSchema(schema);
//
//        StringWriter sw = new StringWriter();
//        marshaller.marshal(container, sw);
//        String xml = sw.toString();
//        File pndr1XmlFile = new File("target/pndr-1-sample-ndr.xml");
//        Files.write(pndr1XmlFile.toPath(), xml.getBytes(StandardCharsets.UTF_8));
//        assertTrue(pndr1XmlFile.exists());
//    }
//
//    @Test
//    void shouldHavePmtctUiSourceAndStaticBundleWired() throws Exception {
//        File pmtctSource = new File("src/main/webapp/jsx/components/ndr/Pmtct.js");
//        assertTrue(pmtctSource.exists(), "Pmtct.js component must exist");
//        String sourceContent = new String(Files.readAllBytes(pmtctSource.toPath()), StandardCharsets.UTF_8);
//        assertTrue(sourceContent.contains("ndr/generate/pmtct?"));
//        assertTrue(sourceContent.contains("ndr/pmtct?"));
//        assertTrue(sourceContent.contains("ndr/pmtct/patients?"));
//        assertTrue(sourceContent.contains("Generate PMTCT XML"));
//
//        File indexHtml = new File("src/main/resources/views/static/ndr/index.html");
//        assertTrue(indexHtml.exists());
//        String html = new String(Files.readAllBytes(indexHtml.toPath()), StandardCharsets.UTF_8);
//
//        java.util.regex.Matcher matcher = java.util.regex.Pattern
//                .compile("src=\"\\./(static/js/[^\"]+\\.chunk\\.js)\"")
//                .matcher(html);
//        File mainChunk = null;
//        int chunkCount = 0;
//        while (matcher.find()) {
//            chunkCount++;
//            String relPath = matcher.group(1);
//            File chunkFile = new File("src/main/resources/views/static/ndr/" + relPath);
//            assertTrue(chunkFile.exists(), "Referenced chunk must exist: " + relPath);
//            if (relPath.contains("main.")) {
//                mainChunk = chunkFile;
//            }
//        }
//        assertTrue(chunkCount >= 2, "index.html must reference vendor and main chunks");
//        assertNotNull(mainChunk, "index.html must reference a main.*.chunk.js file");
//
//        String mainJs = new String(Files.readAllBytes(mainChunk.toPath()), StandardCharsets.UTF_8);
//        assertTrue(mainJs.contains("PMTCT XML"));
//        assertTrue(mainJs.contains("ndr/generate/pmtct?"));
//        assertTrue(mainJs.contains("ndr/pmtct?"));
//        assertTrue(mainJs.contains("ndr/pmtct/patients?"));
//    }
//
//    @Test
//    void shouldHaveTypeSafeCoalesceInPmtctNativeQueries() throws Exception {
//        String[] pmtctMethods = {
//                "getPmtctPatientIds",
//                "getPmtctPatientDemographics",
//                "getPmtctRegisterByPatient",
//                "getMotherInfantPairVisitsByPatient",
//                "getDeliveryChildrenDetailsByPatient",
//                "getInfantCohortRegistrationsByPatient",
//                "getPmtctEligiblePatients",
//                "countPmtctEligiblePatients"
//        };
//        for (java.lang.reflect.Method method : NDRQueryRepository.class.getDeclaredMethods()) {
//            if (Arrays.asList(pmtctMethods).contains(method.getName())) {
//                org.springframework.data.jpa.repository.Query query =
//                        method.getAnnotation(org.springframework.data.jpa.repository.Query.class);
//                assertNotNull(query, "Missing @Query on " + method.getName());
//                String sql = query.value();
//                assertFalse(
//                        sql.contains("archived, false)") || sql.contains("archived, 0)"),
//                        "Query " + method.getName() + " must not mix boolean/integer with archived in COALESCE"
//                );
//                assertFalse(
//                        sql.contains("COALESCE(mv.ga_of_viral_load, mv.ga_weeks, 0)"),
//                        "Query " + method.getName() + " must cast ga_of_viral_load and ga_weeks before COALESCE"
//                );
//            }
//        }
//    }
//}
