package org.lamisplus.modules.ndr.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.HtsReportDto;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class HtsEncounterReportTypeMapper {

    private final ObjectMapper objectMapper;
    private static final Map<String, String> SETTING_MAPPING = new HashMap<>();
    private static final Map<String, String> MODALITY_MAPPING = new HashMap<>();
    private static final Map<String, String> SESSION_TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> MARITAL_STATUS_MAPPING = new HashMap<>();
    private static final Map<String, String> REFERRED_FROM_MAPPING = new HashMap<>();
    private static final Map<String, String> CLIENT_CATEGORY_MAPPING = new HashMap<>();
    private static final Map<String, String> RELATIONSHIP_TO_INDEX_MAPPING = new HashMap<>();
    private static final Map<String, String> NOTIFICATION_METHOD_MAPPING = new HashMap<>();
    private static final Map<String, String> FOLLOW_UP_LOCATION_MAPPING = new HashMap<>();
    private static final Map<Boolean, String> BOOLEAN_TO_YN = new HashMap<>();
    private static final Map<Boolean, String> BOOLEAN_TO_YES_NO = new HashMap<>();
    private static final Map<String, String> RELATIONSHIP_TO_INDEX = new HashMap<>();


    static {
        // Setting mappings
        SETTING_MAPPING.put("CT", HstSetting.TEST_SETTING_CT.getValue());
        SETTING_MAPPING.put("TB", HstSetting.TEST_SETTING_TB.getValue());
        SETTING_MAPPING.put("STI", HstSetting.TEST_SETTING_STI.getValue());
        SETTING_MAPPING.put("FP", HstSetting.TEST_SETTING_FP.getValue());
        SETTING_MAPPING.put("WARD", HstSetting.TEST_SETTING_WARD.getValue());
        SETTING_MAPPING.put("OUTREACH", HstSetting.TEST_SETTING_OUTREACH.getValue());
        SETTING_MAPPING.put("STANDALONE", HstSetting.TEST_SETTING_STANDALONE_HTS.getValue());

        MODALITY_MAPPING.put("CT", "C");
        MODALITY_MAPPING.put("Inpatient", "I");
        MODALITY_MAPPING.put("Outpatient", "O");
        MODALITY_MAPPING.put("Others", "S");

        // Session Type mappings
        SESSION_TYPE_MAPPING.put("INDIVIDUAL", "1");
        SESSION_TYPE_MAPPING.put("COUPLE", "2");
        SESSION_TYPE_MAPPING.put("INDEX_CONTACT_TESTING", "3");
        SESSION_TYPE_MAPPING.put("INDEX", "3");
        SESSION_TYPE_MAPPING.put("PREVIOUSLY_SELF_TESTED", "4");
        SESSION_TYPE_MAPPING.put("SELF_TESTED", "4");

        //Relationship to index
        RELATIONSHIP_TO_INDEX.put("Mother", "M");
        RELATIONSHIP_TO_INDEX.put("Father", "F");
        RELATIONSHIP_TO_INDEX.put("Biological Child", "C");
        RELATIONSHIP_TO_INDEX.put("Spouse", "S");
        RELATIONSHIP_TO_INDEX.put("Live-in Partner", "L");
        RELATIONSHIP_TO_INDEX.put("Boyfriend/Girlfriend", "B");
        RELATIONSHIP_TO_INDEX.put("Casual Partner", "P");
        RELATIONSHIP_TO_INDEX.put("Social Network", "N");

        // Marital status mappings
        MARITAL_STATUS_MAPPING.put("Married", "M");
        MARITAL_STATUS_MAPPING.put("Single", "S");
        MARITAL_STATUS_MAPPING.put("Widowed", "W");
        MARITAL_STATUS_MAPPING.put("Separated", "A");
        MARITAL_STATUS_MAPPING.put("Divorced", "D");
        MARITAL_STATUS_MAPPING.put("Living together", "G");

        // Referred from mappings
        REFERRED_FROM_MAPPING.put("Self", "1");
        REFERRED_FROM_MAPPING.put("TB", "2");
        REFERRED_FROM_MAPPING.put("STI", "3");
        REFERRED_FROM_MAPPING.put("FP", "4");
        REFERRED_FROM_MAPPING.put("OPD", "5");
        REFERRED_FROM_MAPPING.put("Ward", "6");
        REFERRED_FROM_MAPPING.put("Blood Bank", "7");
        REFERRED_FROM_MAPPING.put("Others", "8");

        // Boolean mappings
        BOOLEAN_TO_YN.put(true, "Y");
        BOOLEAN_TO_YN.put(false, "N");
        BOOLEAN_TO_YES_NO.put(true, "Yes");
        BOOLEAN_TO_YES_NO.put(false, "No");

        CLIENT_CATEGORY_MAPPING.put("NEWLY_DIAGNOSED", "ND");
        CLIENT_CATEGORY_MAPPING.put("VIRALLY_UNSUPPRESSED", "VU");
        CLIENT_CATEGORY_MAPPING.put("RETURNED_TO_TREATMENT", "RTT");
        CLIENT_CATEGORY_MAPPING.put("OTHER", "OT");

        // Relationship to Index mappings (NDR numeric codes)
        RELATIONSHIP_TO_INDEX_MAPPING.put("MOTHER", "1");
        RELATIONSHIP_TO_INDEX_MAPPING.put("FATHER", "2");
        RELATIONSHIP_TO_INDEX_MAPPING.put("BIOLOGICAL_CHILD", "3");
        RELATIONSHIP_TO_INDEX_MAPPING.put("SPOUSE", "4");
        RELATIONSHIP_TO_INDEX_MAPPING.put("LIVE_IN_PARTNER", "5");
        RELATIONSHIP_TO_INDEX_MAPPING.put("BOYFRIEND_GIRLFRIEND", "6");
        RELATIONSHIP_TO_INDEX_MAPPING.put("CASUAL_PARTNER", "7");
        RELATIONSHIP_TO_INDEX_MAPPING.put("SOCIAL_NETWORK", "8");

        // Notification Method mappings
        NOTIFICATION_METHOD_MAPPING.put("PASSIVE_CLIENT_REFERRAL", "A");
        NOTIFICATION_METHOD_MAPPING.put("PROVIDER_ASSISTED", "B");
        NOTIFICATION_METHOD_MAPPING.put("CONTRACT", "C");
        NOTIFICATION_METHOD_MAPPING.put("DUAL_REFERRAL", "D");
        NOTIFICATION_METHOD_MAPPING.put("NO_NOTIFICATION_NEEDED", "E");
        NOTIFICATION_METHOD_MAPPING.put("NOT_RECOMMENDED", "F");

        // Follow-up Location mappings
        FOLLOW_UP_LOCATION_MAPPING.put("FACILITY", "FAC");
        FOLLOW_UP_LOCATION_MAPPING.put("WORKPLACE", "WRK");
        FOLLOW_UP_LOCATION_MAPPING.put("HOME", "HOM");
        FOLLOW_UP_LOCATION_MAPPING.put("OTHER", "OTH");
    }

    public boolean getHivTestingReportType(
            IndividualReportType individualReportType,
            ObjectFactory objectFactory,
            List<HtsReportDto> projections,
            List<NDRErrorDTO> errors) {

        log.info("HTS Mapping started... ");
        if (projections == null || projections.isEmpty()) {
            return false;
        }

        List<HIVTestingReportType> hivTestingReport = individualReportType.getHIVTestingReport();

        projections.parallelStream().forEach(projection -> {
            try {
                HIVTestingReportType reportType = NDRObjectFactory.createHIVTestingReportType();
                mapProjectionToReportType(projection, reportType, objectFactory);
                hivTestingReport.add(reportType);
            } catch (Exception e) {
                errors.add(new NDRErrorDTO(
                        projection.getClientCode(),
                        null,
                        e.getMessage() + " | " + Arrays.toString(e.getStackTrace())
                ));
                log.error("Error mapping projection for client: {}", projection.getClientCode(), e);
            }
        });

        return true;
    }

    private void mapProjectionToReportType(
            HtsReportDto projection,
            HIVTestingReportType reportType,
            ObjectFactory objectFactory) {
        log.info("HTS projection for client: {}", projection.toString());
        validateAndSet(projection.getClientCode(), reportType::setClientCode, "ClientCode"); // required
        validateAndSet(projection.getVisitId(), reportType::setVisitID, "VisitID"); // required
        validateAndSetDate(projection.getVisitDate(), reportType::setVisitDate, "VisitDate"); // required

        setIfPresent(projection.getSetting().contains("HTS_ENTRY_POINT_FACILITY") ? "F" : "C", reportType::setSetting); // required
        setIfPresent(projection.getModality() != null ? "C" : "S", reportType::setModality); // TODO - get the modalities // required
        if (projection.getClientAge() != null) {
            setIfPresent(Integer.parseInt(projection.getClientAge()), reportType::setClientAge); // required
        }
        setIfPresent(projection.getSex(), reportType::setSex, this::mapSex); // required
        setIfPresent(projection.getMaritalStatus(), reportType::setMaritalStatus, this::mapMaritalStatus);
        setIfPresent(projection.getNoOfAllWives(), reportType::setNoOfAllWives);
        setIfPresent(projection.getNoOfOwnChildrenLessThan15Years(), reportType::setNoOfOwnChildrenLessThan15Years);
        setIfPresent(projection.getStateOfResidence(), reportType::setStateOfResidence);
        setIfPresent(projection.getLgaOfResidence(), reportType::setLGAOfResidence);
        setIfPresent(projection.getSessionType(), reportType::setSessionType, this::mapSessionType); // required
        setIfPresent(projection.getHivstResult(), reportType::setModality, this::mapTestResult);
        setIfPresent(projection.getIndexClientId(), reportType::setIndexClientId);
        setIfPresent(projection.getRelationshipToIndex(), reportType::setRelationshipToIndex, this::mapRelationshipToIndex);
        setIfPresent(projection.getClientIsPregnant(), reportType::setClientIsPregnant, this::mapYesNoToCode);
        setIfPresent(!Objects.equals(projection.getBreastfeeding(), "false") ? "Yes" : "No", reportType::setBreastfeeding);
        setIfPresent(projection.getDurationOfBreastfeeding(), reportType::setDurationOfBreastfeeding, this::mapDurationOfBreastfeeding);
        setIfPresent(projection.getSyphilisTestResult(), reportType::setSyphilisTestResult, this::mapSyphilisResult);

//        reportType.setPreTestInformation(buildPreTestInformation(objectFactory, projection));
//        reportType.setPostTestCounselling(buildPostTestCounselling(objectFactory, projection));
//        reportType.setIndexContactTesting(buildIndexContactTesting(objectFactory, projection));
//        reportType.setHIVTestResult(buildHIVTestResult(objectFactory, projection));
    }

    // ==================== Pre-Test Information ====================

    private PreTestInformationType buildPreTestInformation(ObjectFactory factory, HtsReportDto p) {
        PreTestInformationType preTest = factory.createPreTestInformationType();

        preTest.setKnowledgeAssessment(buildKnowledgeAssessment(factory, p));
        preTest.setHIVRiskAssessment(buildHIVRiskAssessment(factory, p));
        preTest.setClinicalTBScreening(buildClinicalTBScreening(factory, p));
        preTest.setSyndromicSTIScreening(buildSyndromicSTIScreening(factory, p));
        preTest.setSexPartnerRiskAssessment(buildSexPartnerRiskAssessment(factory, p));

        return preTest;
    }

    private KnowledgeAssessmentType buildKnowledgeAssessment(ObjectFactory factory, HtsReportDto p) {
        KnowledgeAssessmentType assessment = factory.createKnowledgeAssessmentType();

        setBooleanIfPresent(p.getPreviouslyTestedHIVNegative(), assessment::setPreviouslyTestedHIVNegative);
        setBooleanIfPresent(p.getClientInformedAboutHIVTransmissionRoutes(), assessment::setClientInformedAboutHIVTransmissionRoutes);
        setBooleanIfPresent(p.getClientPregnant(), assessment::setClientPregnant);
        setBooleanIfPresent(p.getClientInformedOfHIVTransmissionRiskFactors(), assessment::setClientInformedOfHIVTransmissionRiskFactors);
        setBooleanIfPresent(p.getClientInformedAboutPreventingHIV(), assessment::setClientInformedAboutPreventingHIV);
        setBooleanIfPresent(p.getClientInformedAboutPossibleTestResults(), assessment::setClientInformedAboutPossibleTestResults);
        setBooleanIfPresent(p.getInformedConsentForHIVTestingGiven(), assessment::setInformedConsentForHIVTestingGiven);
        setIfPresent(p.getTimeOfLastNegativeTest(), assessment::setTimeOfLastHIVNegativeTest);

        return assessment;
    }

    private HIVRiskAssessmentType buildHIVRiskAssessment(ObjectFactory factory, HtsReportDto p) {
        HIVRiskAssessmentType assessment = factory.createHIVRiskAssessmentType();

        setBooleanIfPresent(p.getEverHadSexualIntercourse(), assessment::setEverHadSexualIntercourse);
        setBooleanIfPresent(p.getBloodTransfussionInLast3Months(), assessment::setBloodTransfussionInLast3Months);
        setBooleanIfPresent(p.getUnprotectedSexWithCasualPartnerinLast3Months(), assessment::setUnprotectedSexWithCasualPartnerinLast3Months);
        setBooleanIfPresent(p.getUnprotectedSexWithRegularPartnerInLast3Months(), assessment::setUnprotectedSexWithRegularPartnerInLast3Months);
        setBooleanIfPresent(p.getMoreThan1SexPartnerDuringLast3Months(), assessment::setMoreThan1SexPartnerDuringLast3Months);
        setBooleanIfPresent(p.getStiInLast3Months(), assessment::setSTIInLast3Months);
        setBooleanIfPresent(p.getSexUnderInfluenceOfDrugsOrAlcohol(), assessment::setSexUnderInfluenceOfDrugsOrAlcohol);
        setBooleanIfPresent(p.getUnprotectedVaginalSex(), assessment::setUnprotectedVaginalSex);

        return assessment;
    }

    private ClinicalTBScreeningType buildClinicalTBScreening(ObjectFactory factory, HtsReportDto p) {
        ClinicalTBScreeningType screening = factory.createClinicalTBScreeningType();

        setBooleanIfPresent(p.getCurrentlyCough(), screening::setCurrentlyCough);
        setBooleanIfPresent(p.getWeightLoss(), screening::setWeightLoss);
        setBooleanIfPresent(p.getFever(), screening::setFever);
        setBooleanIfPresent(p.getNightSweats(), screening::setNightSweats);

        return screening;
    }

    private SyndromicSTIScreeningType buildSyndromicSTIScreening(ObjectFactory factory, HtsReportDto p) {
        SyndromicSTIScreeningType screening = factory.createSyndromicSTIScreeningType();

        setBooleanIfPresent(p.getVaginalDischargeOrBurningWhenUrinating(), screening::setVaginalDischargeOrBurningWhenUrinating);
        setBooleanIfPresent(p.getLowerAbdominalPainsWithOrWithoutVaginalDischarge(), screening::setLowerAbdominalPainsWithOrWithoutVaginalDischarge);
        setBooleanIfPresent(p.getUrethralDischargeOrBurningWhenUrinating(), screening::setUrethralDischargeOrBurningWhenUrinating);
        setBooleanIfPresent(p.getScrotalSwellingAndPain(), screening::setScrotalSwellingAndPain);
        setBooleanIfPresent(p.getGenitalSore(), screening::setGenitalSore);
        setBooleanIfPresent(p.getGenitalSoreOrSwollenInguinalLymphNodes(), screening::setGenitalSoreOrSwollenInguinalLymphNodes);

        return screening;
    }

    private SexPartnerRiskAssessmentType buildSexPartnerRiskAssessment(ObjectFactory factory, HtsReportDto p) {
        SexPartnerRiskAssessmentType assessment = factory.createSexPartnerRiskAssessmentType();

        setBooleanIfPresent(p.getPartnerNewlyDiagnosedOnARTLessThan3To6Months(), assessment::setPartnerNewlyDiagnosedOnARTLessThan3To6Months);
        setBooleanIfPresent(p.getPartnerPregnantReceivingARVForPMTCT(), assessment::setPartnerPregnantReceivingARVForPMTCT);
        setBooleanIfPresent(p.getPartnerAdolescent10To19KnownHIVInfected(), assessment::setPartnerAdolescent10To19KnownHIVInfected);
        setBooleanIfPresent(p.getPartnerKnownPositiveNotRegularlyOnDrugs(), assessment::setPartnerKnownPositiveNotRegularlyOnDrugs);
        setBooleanIfPresent(p.getPartnerKnownPositiveRecentlyReturnedAfterLTFU(), assessment::setPartnerKnownPositiveRecentlyReturnedAfterLTFU);

        return assessment;
    }

    // ==================== HIV Test Result ====================

    private HIVTestResultType buildHIVTestResult(ObjectFactory factory, HtsReportDto p) {
        HIVTestResultType result = factory.createHIVTestResultType();
        TestResultType testResult = factory.createTestResultType();

        // Screening test
        String screeningResult = mapTestResult(p.getScreeningTestResult());
        if (screeningResult != null) {
            testResult.setScreeningTestResult(screeningResult);
        }

        // Confirmatory test
        String confirmatoryResult = mapTestResult(p.getConfirmatoryTestResult());
        if (confirmatoryResult != null) {
            testResult.setConfirmatoryTestResult(confirmatoryResult);
            testResult.setFinalTestResult(determineFinalResult(confirmatoryResult));
        }

        // Dates
        setDateIfPresent(p.getScreeningTestResultDate(), testResult::setScreeningTestResultDate);
        setDateIfPresent(p.getConfirmatoryTestResultDate(), testResult::setConfirmatoryTestResultDate);

        // Suspected acute infection
        if (p.getSuspectedAcuteHIVInfection() != null) {
            testResult.setSuspectedAcuteHIVInfection(BOOLEAN_TO_YES_NO.get(p.getSuspectedAcuteHIVInfection()));
        }

        result.setTestResult(testResult);

        // Recency testing
//        if (StringUtils.isNotBlank(p.getRecencyNumber())) {
//            buildRecencyTesting(factory, result, p);
//        }

        return result;
    }

    // ==================== Index Contact ============================
    private IndexContactTestingType buildIndexContactTesting(ObjectFactory factory, HtsReportDto p) {
        boolean hasIndexData = StringUtils.isNotBlank(p.getClientCategory()) ||
                StringUtils.isNotBlank(p.getOfferedPns()) ||
                StringUtils.isNotBlank(p.getAcceptedPns()) ||
                StringUtils.isNotBlank(p.getRelationshipToIndex());

        if (!hasIndexData) {
            return null;
        }

        IndexContactTestingType indexContact = factory.createIndexContactTestingType();

        setIfPresent(p.getArtClinic(), indexContact::setARTClinic, this::mapYesNoToCode);
        if (indexContact.getARTClinic() == null) {
            indexContact.setARTClinic("N");
        }
        setIfPresent(p.getIndexClientIDType(), indexContact::setIndexClientIDType, this::mapIndexClientIdType);
        setIfPresent(p.getIndexClientID(), indexContact::setIndexClientID);
        setIfPresent(p.getIndexClientLGA(), indexContact::setIndexClientLGA);
        setIfPresent(p.getIndexClientState(), indexContact::setIndexClientState);
        setIfPresent(p.getClientCategory(), indexContact::setClientCategory, this::mapClientCategory);
        setIfPresent(p.getOfferedIndexTestingServices(), indexContact::setOfferedIndexTestingServices, this::mapYesNoToCode);
        setIfPresent(p.getAcceptedIndexTestingServices(), indexContact::setAcceptedIndexTestingServices, this::mapYesNoToCode);

        List<IndexContactType> contacts = buildIndexContacts(factory, p);
        if (!contacts.isEmpty()) {
            indexContact.getIndexContact().addAll(contacts);
        }

        return indexContact;
    }

    private List<IndexContactType> buildIndexContacts(ObjectFactory factory, HtsReportDto p) {
        List<IndexContactType> contacts = new ArrayList<>();

        // Check if we have contact data
        boolean hasContactData = StringUtils.isNotBlank(p.getRelationshipToIndex()) ||
                StringUtils.isNotBlank(p.getNotificationMethod());

        if (!hasContactData) {
            return contacts;
        }

        IndexContactType contact = factory.createIndexContactType();

        contact.setSerialNo(p.getSerialNo());

        setIfPresent(p.getRelationshipToIndex(), contact::setRelationshipToIndex, this::mapRelationshipToIndex);

        setIfPresent(p.getSex(), contact::setSex, this::mapSex);

        setIfPresent(p.getAgeGroup(), contact::setAgeGroup, this::mapAgeGroup);

        setIfPresent(p.getNotificationMethod(), contact::setNotificationMethod, this::mapNotificationMethod);

        setIfPresent(p.getFollowUpAppointmentLocation(), contact::setFollowUpAppointmentLocation, this::mapFollowUpLocation);

        setIfPresent(p.getContactAttempts(), contact::setContactAttempts, this::parseInteger);

        setIfPresent(p.getKnownHIVPositive(), contact::setKnownHIVPositive, this::mapYesNoToCode);

        setIfPresent(p.getHivTestResult(), contact::setHIVTestResult, this::mapTestResultCode);

        setDateIfPresent(p.getDateTested(), contact::setDateTested);
        setDateIfPresent(p.getDateEnrolledOnART(), contact::setDateEnrolledOnART);
        setDateIfPresent(p.getDateEnrolledInOVC(), contact::setDateEnrolledInOVC);

        setIfPresent(p.getOvcid(), contact::setOVCID);

        contacts.add(contact);

        return contacts;
    }

    // ==================== Post-Test Counselling ====================
    private PostTestCounsellingType buildPostTestCounselling(ObjectFactory factory, HtsReportDto p) {
        PostTestCounsellingType postTest = factory.createPostTestCounsellingType();

        // Required field
        String priorTestStatus = determinePriorTestStatus(p.getTestedForHIVBeforeWithinThisYear());
        postTest.setTestedForHIVBeforeWithinThisYear(priorTestStatus);

        // Boolean fields
        setBooleanAsYesNo(p.getAcceptedIndexTesting(), postTest::setAcceptedIndexTesting);
        setBooleanAsYesNo(p.getProvidedWithInformationOnFPandDualContraception(), postTest::setProvidedWithInformationOnFPandDualContraception);
        setBooleanAsYesNo(p.getClientOrPartnerUseFPMethodsOtherThanCondoms(), postTest::setClientOrPartnerUseFPMethodsOtherThanCondoms);
        setBooleanAsYesNo(p.getClientOrPartnerUseCondomsAsOneFPMethods(), postTest::setClientOrPartnerUseCondomsAsOneFPMethods);
        setBooleanAsYesNo(p.getClientRecievedHIVTestResult(), postTest::setClientRecievedHIVTestResult);
        setBooleanAsYesNo(p.getCorrectCondomUseDemonstrated(), postTest::setCorrectCondomUseDemonstrated);
        setBooleanAsYesNo(p.getCondomsProvidedToClient(), postTest::setCondomsProvidedToClient);
        setBooleanAsYesNo(p.getHivSelfTestKitsProvided(), postTest::setHIVSelfTestKitsProvided);
        setIfPresent(p.getHivSelfTestKitsCount(), postTest::setHIVSelfTestKitsCount);
        setBooleanAsYesNo(p.getClientReferredToOtherServices(), postTest::setClientReferredToOtherServices);

        // Category of client
        setIfPresent(p.getCategoryOfClient(), postTest::setCategoryOfClient, this::mapCategoryOfClient);

        return postTest;
    }

    // ==================== Mapping Helper Methods ====================

    private String mapRelationshipToIndex(String relation) {
        if (relation == null || relation.isEmpty()) {
            return "P"; // Default to Individual
        }

        String upperType = relation.toUpperCase().replace("RELATIONSHIP_CONTACT_", "").trim();

        String mappedValue = RELATIONSHIP_TO_INDEX_MAPPING.get(upperType);
        if (mappedValue != null) {
            return mappedValue;
        }

        log.warn("Unknown session type: {}, defaulting to Individual (1)", relation);
        return "P";
    }
    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private String mapTestResultCode(String result) {
        if (result == null) {
            return null;
        }
        String upperResult = result.toUpperCase();
        if (upperResult.equals("POSITIVE") || upperResult.equals("POS") || upperResult.equals("R")) {
            return "Pos";
        }
        if (upperResult.equals("NEGATIVE") || upperResult.equals("NEG") || upperResult.equals("NR")) {
            return "Neg";
        }
        return null;
    }
    private String mapNotificationMethod(String method) {
        if (method == null) {
            return null;
        }
        String upperMethod = method.toUpperCase();
        for (Map.Entry<String, String> entry : NOTIFICATION_METHOD_MAPPING.entrySet()) {
            if (upperMethod.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (upperMethod.contains("PASSIVE") || upperMethod.contains("CLIENT REFERRAL")) return "A";
        if (upperMethod.contains("PROVIDER")) return "B";
        if (upperMethod.contains("CONTRACT")) return "C";
        if (upperMethod.contains("DUAL")) return "D";
        if (upperMethod.contains("NO NOTIFICATION") || upperMethod.contains("KNOWN POSITIVE")) return "E";
        if (upperMethod.contains("NOT RECOMMENDED") || upperMethod.contains("SAFETY")) return "F";
        return null;
    }
    private String mapFollowUpLocation(String location) {
        if (location == null) {
            return null;
        }
        String upperLoc = location.toUpperCase();
        for (Map.Entry<String, String> entry : FOLLOW_UP_LOCATION_MAPPING.entrySet()) {
            if (upperLoc.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (upperLoc.contains("FACILITY") || upperLoc.contains("CLINIC")) return "FAC";
        if (upperLoc.contains("WORK") || upperLoc.contains("OFFICE")) return "WRK";
        if (upperLoc.contains("HOME") || upperLoc.contains("RESIDENCE")) return "HOM";
        return "OTH";
    }
    private String mapSex(String sex) {
        if (sex == null) {
            return null;
        }
        String upperSex = sex.toUpperCase();
        if (upperSex.contains("SEX_MALE")) return "M";
        if (upperSex.startsWith("SEX_FEMALE")) return "F";
        return null;
    }
    private String mapAgeGroup(String ageGroup) {
        if (ageGroup == null) {
            return null;
        }
        String upperGroup = ageGroup.toUpperCase();
        if (upperGroup.contains("LT15") || upperGroup.contains("LESS THAN 15") || upperGroup.contains("<15")) {
            return "LT15";
        }
        if (upperGroup.contains("GTE15") || upperGroup.contains("15 AND ABOVE") || upperGroup.contains("≥15") || upperGroup.contains(">=")) {
            return "GTE15";
        }

        try {
            int age = Integer.parseInt(ageGroup);
            return age < 15 ? "LT15" : "GTE15";
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private String mapDurationOfBreastfeeding(String duration) {
        if (duration == null) {
            return "";
        }
        String upperGroup = duration.toUpperCase();
        if (upperGroup.contains("LT15") || upperGroup.contains("LESS THAN 15") || upperGroup.contains("<15")) {
            return "LT15";
        }
        if (upperGroup.contains("GTE15") || upperGroup.contains("15 AND ABOVE") || upperGroup.contains("≥15") || upperGroup.contains(">=")) {
            return "GTE15";
        }

        try {
            int age = Integer.parseInt(duration);
            return age < 15 ? "LT6" : "GTE6";
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private String mapSessionType(String sessionType) {
        if (sessionType == null || sessionType.isEmpty()) {
            return "1"; // Default to Individual
        }

        String upperType = sessionType.toUpperCase().replace("COUNSELING_TYPE_", "").trim();

        // Direct mapping using HashMap (O(1) complexity)
        String mappedValue = SESSION_TYPE_MAPPING.get(upperType);
        if (mappedValue != null) {
            return mappedValue;
        }

        log.warn("Unknown session type: {}, defaulting to Individual (1)", sessionType);
        return "1";
    }

    private String mapMaritalStatus(String status) {
        if (status == null) {
            return null;
        }

        for (Map.Entry<String, String> entry : MARITAL_STATUS_MAPPING.entrySet()) {
            if (status.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return status;
    }

    private String mapTestResult(String result) {
        if (result == null) {
            return null;
        }
        String upperType = result.toUpperCase().replace("HIV_CONFIRMATORY_TEST_RESULT_", "").trim();
        return upperType.equalsIgnoreCase("POSITIVE") ? "R" : "NR";
    }

    private String mapSyphilisResult(String result) {
        if (result == null) {
            return null;
        }
        String upperType = result.toUpperCase().replace("SYPHILIS_RESULT_", "").trim();
        return upperType.equalsIgnoreCase("POSITIVE") ? "R" : "NR";
    }

    private String mapCategoryOfClient(String category) {
        if (category == null) {
            return "S";
        }
        String upperCategory = category.toUpperCase();
        switch (upperCategory) {
            case "PARTNER":
                return "P";
            case "CAREGIVER":
                return "CG";
            case "SOCIAL_NETWORK":
                return "SN";
            default:
                return "S";
        }
    }

    private String determineFinalResult(String confirmatoryResult) {
        if (confirmatoryResult == null) {
            return null;
        }
        return "R".equals(confirmatoryResult) ? "Pos" : "Neg";
    }

    private String determinePriorTestStatus(String previouslyTested) {
        if (previouslyTested == null) {
            return "1";
        }
        return Boolean.parseBoolean(previouslyTested) ? "2" : "1";
    }

    // ==================== Generic Helper Methods ====================

    private <T> void validateAndSet(T value, Consumer<T> setter, String fieldName) {
        if (value != null) {
            setter.accept(value);
        } else {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private <T> void setIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private <T, R> void setIfPresent(T value, Consumer<R> setter, Function<T, R> mapper) {
        if (value != null) {
            R mapped = mapper.apply(value);
            if (mapped != null) {
                setter.accept(mapped);
            }
        }
    }

    private void setBooleanIfPresent(Boolean value, Consumer<Boolean> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private void setIfPresent(String value, Consumer<String> setter, Predicate<String> condition) {
        if (value != null && condition.test(value)) {
            setter.accept(value);
        }
    }

    private void setBooleanAsYN(Boolean value, Consumer<YNCodeType> setter) {
        if (value != null) {
            setter.accept(value ? YNCodeType.YES : YNCodeType.NO);
        }
    }

    private void setBooleanAsYesNo(Boolean value, Consumer<Boolean> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private void validateAndSetDate(LocalDate date, Consumer<javax.xml.datatype.XMLGregorianCalendar> setter, String fieldName) {
        if (date != null) {
            try {
                setter.accept(DateUtil.getXmlDate(Date.valueOf(date)));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException("Error setting " + fieldName + ": " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private void setDateIfPresent(LocalDate date, Consumer<javax.xml.datatype.XMLGregorianCalendar> setter) {
        if (date != null) {
            try {
                setter.accept(DateUtil.getXmlDate(Date.valueOf(date)));
            } catch (DatatypeConfigurationException e) {
                log.warn("Error setting date: {}", e.getMessage());
            }
        }
    }
    private String mapYesNoToCode(String value) {
        if (value == null) {
            return null;
        }
        String upperValue = value.toUpperCase().replace("YES_NO_", "").trim();
        if (upperValue.equals("YES") || upperValue.equals("Y") || upperValue.equals("TRUE")) {
            return "Yes";
        }
        if (upperValue.equals("NO") || upperValue.equals("N") || upperValue.equals("FALSE")) {
            return "No";
        }
        return null;
    }

    private String mapClientCategory(String category) {
        if (category == null) {
            return "OT";
        }
        String upperCategory = category.toUpperCase();
        for (Map.Entry<String, String> entry : CLIENT_CATEGORY_MAPPING.entrySet()) {
            if (upperCategory.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "OT";
    }

    private String mapIndexClientIdType(String type) {
        if (type == null) {
            return null;
        }
        String upperType = type.toUpperCase();
        if (upperType.contains("HTS")) return "HTS";
        if (upperType.contains("ART")) return "ART";
        return null;
    }

}

