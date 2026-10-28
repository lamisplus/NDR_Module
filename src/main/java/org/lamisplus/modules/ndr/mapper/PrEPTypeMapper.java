package org.lamisplus.modules.ndr.mapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PrepDTO;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Component;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class PrEPTypeMapper {
    private static final Map<String, String> REFERREDFROM_MAPPING = new HashMap<>();
    private static final Map<String, String> SETTING_MAPPING = new HashMap<>();
    private static final Map<String, String> VISIT_TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> POPULATION_TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> TYPE_SESSION_MAPPING = new HashMap<>();
    private static final Map<String, String> SEX_PARTNER_MAPPING = new HashMap<>();
    private static final Map<String, String> PREGNANCY_STATUS_MAPPING = new HashMap<>();
    private static final Map<String, String> EDUCATION_LEVEL_MAPPING = new HashMap<>();
    private static final Map<String, String> ENROLLMENT_TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> MARITAL_STATUS_MAPPING = new HashMap<>();
    private static final Map<String, String> HTS_ENTRY_POINT_MAPPING = new HashMap<>();
    private static final Map<String, String> HIV_TEST_RESULT_MAPPING = new HashMap<>();
    private static final Map<String, String> PREP_TYPE_MAPPING = new HashMap<>();
    private static final Map<String, String> PREP_REGIMEN_MAPPING = new HashMap<>();
    private static final Map<String, String> PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING = new HashMap<>();
    private static final Map<String, String> PREP_URINALYSIS_RESULT_MAPPING = new HashMap<>();
    private static final Map<String, String> PREP_SERVICE_PROVIDED_MAPPING = new HashMap<>();
    private static final Map<String, String> LAST_HIV_TEST_TIME_MAPPING = new HashMap<>();

    private static final DatatypeFactory DATATYPE_FACTORY;
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();


    static {
        try {
            DATATYPE_FACTORY = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Failed to initialize DatatypeFactory", e);
        }


        REFERREDFROM_MAPPING.put("PREP_SOURCE_REFERRAL_SELF", "Self");
        REFERREDFROM_MAPPING.put("PREP_SOURCE_REFERRAL_COUNSELOR", "Counselor");
        REFERREDFROM_MAPPING.put("PREP_SOURCE_REFERRAL_PEER", "Peer");
        REFERREDFROM_MAPPING.put("PREP_SOURCE_REFERRAL_OTHERS-SPECIFY", "Other");


        LAST_HIV_TEST_TIME_MAPPING.put("1Months", "LessThan1Month");
        LAST_HIV_TEST_TIME_MAPPING.put("1-3 Months", "1To3Months");
        LAST_HIV_TEST_TIME_MAPPING.put("4-6Months", "4To6Months");
        LAST_HIV_TEST_TIME_MAPPING.put("6Months", "MoreThan6Months");


        SETTING_MAPPING.put("PREP_SETTINGS_CT", "CT");
        SETTING_MAPPING.put("PREP_SETTINGS_TB", "TB");
        SETTING_MAPPING.put("PREP_SETTINGS_STI", "STI");
        SETTING_MAPPING.put("PREP_SETTINGS_FP", "FP");
        SETTING_MAPPING.put("PREP_SETTINGS_OPD", "OPD");
        SETTING_MAPPING.put("PREP_SETTINGS_WARD", "Ward");
        SETTING_MAPPING.put("PREP_SETTINGS_OUTREACH", "Outreach");
        SETTING_MAPPING.put("PREP_SETTINGS_STANDALONE_HTS", "StandaloneHTS");
        SETTING_MAPPING.put("PREP_SETTINGS_OTHERS", "Other");


        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_INITIATION", "1");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_SECOND_INITIATION", "2");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_REFILL_RE-INJECTION", "3");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_METHOD_SWITCH", "4");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_RESTART", "5");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_TRANSFER_IN", "6");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_NO_PREP_PROVIDED", "7");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_DISCONTINUATION", "8");
        VISIT_TYPE_MAPPING.put("PREP_VISIT_TYPE_DISCONTINUATION_FOLLOW-UP", "9");


        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_SERODISCORDANT_COUPLES_(SDC)", "1");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_SEX_WORKER", "2");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_SEX_WORKERS", "2");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_PARTNERS_OF_SEX_WORKERS", "3");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_INJECTING_DRUG_USERS", "4");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_INDIVIDUALS_WHO_ENGAGE_IN_ANAL_SEX_ON_A_PROLONGED_AND_REGULAR_BASIS", "5");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_EXPOSED_ADOLESCENTS_AND_YOUNG_PEOPLE", "6");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_TRANSGENDER", "7");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_OTHER_POPULATION", "8");
        POPULATION_TYPE_MAPPING.put("POPULATION_TYPE_AT_RISK_PREGNANT_&_BREASTFEEDING_WOMEN", "9");


        TYPE_SESSION_MAPPING.put("COUNSELING_TYPE_INDIVIDUAL", "Individual");
        TYPE_SESSION_MAPPING.put("COUNSELING_TYPE_PREVIOUSLY_SELF-TESTED", "Individual");
        TYPE_SESSION_MAPPING.put("COUNSELING_TYPE_INDEX_CONTACT_TESTING", "Individual");
        TYPE_SESSION_MAPPING.put("COUNSELING_TYPE_COUPLE", "Couple");
        TYPE_SESSION_MAPPING.put("COUNSELING_TYPE_GROUP", "Couple");


        SEX_PARTNER_MAPPING.put("Male", "Male");
        SEX_PARTNER_MAPPING.put("Female", "Female");
        SEX_PARTNER_MAPPING.put("Both", "Both");
        SEX_PARTNER_MAPPING.put("SEX_PARTNERS_MALE", "Male");
        SEX_PARTNER_MAPPING.put("SEX_PARTNERS_FEMALE", "Female");
        SEX_PARTNER_MAPPING.put("SEX_PARTNERS_BOTH", "Both");


        PREGNANCY_STATUS_MAPPING.put("PREGANACY_STATUS_PREGNANT", "P");
        PREGNANCY_STATUS_MAPPING.put("PREGANACY_STATUS_BREASTFEEDING", "BF");
        PREGNANCY_STATUS_MAPPING.put("PREGANACY_STATUS_NOT_PREGNANT", "NP");
        PREGNANCY_STATUS_MAPPING.put("PREGANACY_STATUS_POST_PARTUM", "NP");


        ENROLLMENT_TYPE_MAPPING.put("PREP_PEP_ENROLLMENT_TYPE_PREP", "PrEP");
        ENROLLMENT_TYPE_MAPPING.put("PREP_PEP_ENROLLMENT_TYPE_PEP", "PEP");


        MARITAL_STATUS_MAPPING.put("MARITAL_STATUS_SINGLE", "1");
        MARITAL_STATUS_MAPPING.put("MARITAL_STATUS_MARRIED", "2");
        MARITAL_STATUS_MAPPING.put("MARITAL_STATUS_DIVORCED", "3");
        MARITAL_STATUS_MAPPING.put("MARITAL_STATUS_SEPARATED", "4");
        MARITAL_STATUS_MAPPING.put("MARITAL_STATUS_COHABITING", "5");
        MARITAL_STATUS_MAPPING.put("MARITAL_STATUS_WIDOWED", "6");


        EDUCATION_LEVEL_MAPPING.put("EDUCATION_LEVEL_NO_FORMAL_EDUCATION", "1");
        EDUCATION_LEVEL_MAPPING.put("EDUCATION_LEVEL_PRIMARY", "2");
        EDUCATION_LEVEL_MAPPING.put("EDUCATION_SENIOR_SECONDARY", "3");
        EDUCATION_LEVEL_MAPPING.put("EDUCATION_JUNIOR_SECONDARY", "3");
        EDUCATION_LEVEL_MAPPING.put("EDUCATION_POST_SECONDARY", "4");
        EDUCATION_LEVEL_MAPPING.put("EDUCATION_NONE", "1");


        HTS_ENTRY_POINT_MAPPING.put("HTS_ENTRY_POINT_FACILITY", "1");
        HTS_ENTRY_POINT_MAPPING.put("HTS_ENTRY_POINT_COMMUNITY", "2");
        HTS_ENTRY_POINT_MAPPING.put("HTS_ENTRY_POINT_OTHERS", "3");


        HIV_TEST_RESULT_MAPPING.put("HIV_TEST_RESULT_NEGATIVE", "Negative");
        HIV_TEST_RESULT_MAPPING.put("HIV_TEST_RESULT_POSITIVE", "Positive");
        HIV_TEST_RESULT_MAPPING.put("HIV_TEST_RESULT_EARLY_DETECT", "SuspectedAcuteHIVInfection");


        PREP_TYPE_MAPPING.put("PREP_TYPE_ORAL", "1");
        PREP_TYPE_MAPPING.put("PREP_TYPE_INJECTIBLES", "2");
        PREP_TYPE_MAPPING.put("PREP_TYPE_RING", "3");
        PREP_TYPE_MAPPING.put("PREP_TYPE_OTHERS", "4");


        PREP_REGIMEN_MAPPING.put("PREP_REGIMEN_TDF_FTC", "1");
        PREP_REGIMEN_MAPPING.put("PREP_REGIMEN_TDF_3TC", "2");
        PREP_REGIMEN_MAPPING.put("PREP_REGIMEN_CABOTEGRAVIR", "3");
        PREP_REGIMEN_MAPPING.put("PREP_REGIMEN_LENACAPAVIR", "4");
        PREP_REGIMEN_MAPPING.put("PREP_REGIMEN_OTHERS", "5");


        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_RIFAMPIN", "1");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_RIFAPENTINE", "2");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_CARBAMAZEPINE", "3");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_OXCARBAZEPINE", "4");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_PHENOBARBITAL", "5");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_PHENYTOIN", "6");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_METHADONE", "7");
        PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.put("PREP_HISTORY_OF_DRUG_INTERACTIONS_EXOGENOUS_HORMONES", "8");


        PREP_URINALYSIS_RESULT_MAPPING.put("PREP_TYPE_ORAL", "1");
        PREP_URINALYSIS_RESULT_MAPPING.put("PREP_TYPE_INJECTIBLES", "2");
        PREP_URINALYSIS_RESULT_MAPPING.put("PREP_TYPE_RING", "3");
        PREP_URINALYSIS_RESULT_MAPPING.put("PREP_TYPE_OTHERS", "4");


        PREP_SERVICE_PROVIDED_MAPPING.put("PREP_TYPE_ORAL", "1");
        PREP_SERVICE_PROVIDED_MAPPING.put("SERVICE_PROVIDED_STI_SERVICE", "2");
        PREP_SERVICE_PROVIDED_MAPPING.put("SERVICE_PROVIDED_FAMILY_PLANNING", "3");
        PREP_SERVICE_PROVIDED_MAPPING.put("SERVICE_PROVIDED_ANTIRETROVIRAL_THERAPY", "4");
    }


    public boolean getPrEPType(
            final IndividualReportType individualReportType,
            final ObjectFactory objectFactory,
            final List<PrepDTO> projections,
            final List<NDRErrorDTO> errors) {


        if (projections == null || projections.isEmpty()) {
            log.debug("No PrEP/PEP projections to map");
            return false;
        }


        final PrEPType prepPepType = objectFactory.createPrEPType();

        // Thread-safe collections to collect mapped items and errors across parallel streams
        final List<PrepScreeningAndEligibilityType> threadSafeScreenings = Collections.synchronizedList(new ArrayList<>());
        final List<PrepPepCardEnrollmentType> threadSafeEnrollments = Collections.synchronizedList(new ArrayList<>());


        projections.parallelStream().filter(Objects::nonNull).forEach(projection -> {
            try {
                if (hasScreeningData(projection)) {
                    PrepScreeningAndEligibilityType screening = mapScreeningAndEligibility(projection, objectFactory);
                    if (screening != null) {
                        threadSafeScreenings.add(screening);
                    }
                }
                if (hasEnrollmentData(projection)) {
                    PrepPepCardEnrollmentType enrollment = mapCardEnrollment(projection, objectFactory);
                    if (enrollment != null) {
                        threadSafeEnrollments.add(enrollment);
                    }
                }
            } catch (Exception e) {
                log.error("Error mapping PrEP projection for client: {}", projection.getUniqueClientId(), e);
                if (errors != null) {
                    synchronized (errors) {
                        errors.add(new NDRErrorDTO(
                                projection.getUniqueClientId(),
                                null,
                                "PrEP Mapping Error: " + e.getMessage()
                        ));
                    }
                }
            }
        });


        boolean mappedAny = false;


        if (!threadSafeScreenings.isEmpty()) {
            prepPepType.getScreeningAndEligibility().addAll(threadSafeScreenings);
            mappedAny = true;
        }


        // Note: If multiple card enrollments exist, ensure your schema supports a list or handle appropriately.
        // Here we assign the first available enrollment if present.
        if (!threadSafeEnrollments.isEmpty()) {
            prepPepType.setCardEnrollment(threadSafeEnrollments.get(0));
            mappedAny = true;
        }


        if (mappedAny) {
            individualReportType.setPrEPPEP(prepPepType);
            log.info("PrEP/PEP successfully mapped and attached to IndividualReportType using parallel processing");
            return true;
        }


        return false;
    }


    private PrepScreeningAndEligibilityType mapScreeningAndEligibility(
            final PrepDTO p,
            final ObjectFactory factory) {


        if (p.getUniqueClientId() == null || p.getUniqueClientId().trim().isEmpty()) {
            log.warn("Skipping screening and eligibility mapping: UniqueClientId is missing for client hospital number: {}",
                    p.getHospitalNumber());
            return null;
        }


        if (!hasScreeningData(p)) {
            return null;
        }


        final PrepScreeningAndEligibilityType s = factory.createPrepScreeningAndEligibilityType();
        validateAndSet(p.getUniqueClientId(), s::setUniqueClientId, "UniqueClientId");
        validateAndSetDate(p.getDateOfVisit(), s::setDateOfVisit, "DateOfVisit");
        setIfPresent(p.getReferredFrom(), s::setReferredFrom, this::mapPrepSource);
        if (p.getReferredFromOtherSpecify() != null) s.setReferredFromOtherSpecify(p.getReferredFromOtherSpecify());
        setIfPresent(p.getSetting(), s::setSetting, this::mapPrepSetting);
        if (p.getSettingOtherSpecify() != null) s.setSettingOtherSpecify(p.getSettingOtherSpecify());
        if (p.getAge() != null) s.setAge(p.getAge());
        if (p.getSex() != null) s.setSex(p.getSex());
        setIfPresent(p.getVisitType(), s::setVisitType, this::mapVisitType);
        setIfPresent(p.getPopulationType(), s::setPopulationType, this::mapPopulationType);
        if (p.getOccupation() != null) s.setOccupation(p.getOccupation());
        if (p.getEducationLevel() != null) s.setEducationLevel(p.getEducationLevel());
        if (p.getMaritalStatus() != null) s.setMaritalStatus(p.getMaritalStatus());
        if (p.getNumberOfOwnChildrenUnder5() != null) s.setNumberOfOwnChildrenUnder5(p.getNumberOfOwnChildrenUnder5());
        if (p.getWeight() != null) s.setWeight(p.getWeight());
        setIfPresent(p.getTypeOfSession(), s::setTypeOfSession, this::mapPrepSession);
        setIfPresent(p.getSexPartners(), s::setSexPartners, this::mapSexPartner);
        setIfPresent(p.getPregnancyStatus(), s::setPregnancyStatus, this::mapPregnancyStatus);
        if (p.getPartnerIsHivPositive() != null) s.setPartnerIsHivPositive(p.getPartnerIsHivPositive());
        if (p.getPartnerInjectsDrugs() != null) s.setPartnerInjectsDrugs(p.getPartnerInjectsDrugs());
        if (p.getPartnerHasSexWithMen() != null) s.setPartnerHasSexWithMen(p.getPartnerHasSexWithMen());
        if (p.getPartnerIsTransgender() != null) s.setPartnerIsTransgender(p.getPartnerIsTransgender());
        if (p.getPartnerHasMultiplePartnersWithoutCondoms() != null)
            s.setPartnerHasMultiplePartnersWithoutCondoms(p.getPartnerHasMultiplePartnersWithoutCondoms());
        if (p.getUnprotectedVaginalSexWithCasualPartner() != null)
            s.setUnprotectedVaginalSexWithCasualPartner(p.getUnprotectedVaginalSexWithCasualPartner());
        if (p.getUnprotectedAnalSex() != null) s.setUnprotectedAnalSex(p.getUnprotectedAnalSex());
        if (p.getSharedNeedlesOrInjectingMaterials() != null)
            s.setSharedNeedlesOrInjectingMaterials(p.getSharedNeedlesOrInjectingMaterials());
        if (p.getMoreThanOneSexPartner() != null) s.setMoreThanOneSexPartner(p.getMoreThanOneSexPartner());
        if (p.getPaidOrBeenPaidForSex() != null) s.setPaidOrBeenPaidForSex(p.getPaidOrBeenPaidForSex());
        if (p.getUsesCocaine() != null) s.setUsesCocaine(p.getUsesCocaine());
        if (p.getUsesHeroin() != null) s.setUsesHeroin(p.getUsesHeroin());
        if (p.getUsesMarijuana() != null) s.setUsesMarijuana(p.getUsesMarijuana());
        if (p.getUsesAmphetamine() != null) s.setUsesAmphetamine(p.getUsesAmphetamine());
        if (p.getUsesCodeineSyrup() != null) s.setUsesCodeineSyrup(p.getUsesCodeineSyrup());
        if (p.getUsesOtherDrugs() != null) s.setUsesOtherDrugs(p.getUsesOtherDrugs());
        if (p.getOtherDrugsSpecify() != null) s.setOtherDrugsSpecify(p.getOtherDrugsSpecify());
        if (p.getRouteInject() != null) s.setRouteInject(p.getRouteInject());
        if (p.getRouteSniff() != null) s.setRouteSniff(p.getRouteSniff());
        if (p.getRouteSnort() != null) s.setRouteSnort(p.getRouteSnort());
        if (p.getRouteSmoke() != null) s.setRouteSmoke(p.getRouteSmoke());
        if (p.getUsesDrugsForSexualEnhancement() != null) s.setUsesDrugsForSexualEnhancement(p.getUsesDrugsForSexualEnhancement());
        if (p.getHadSexWithoutCondomUnkHivStatLast72Hours() != null)
            s.setHadSexWithoutCondomUnkHivStatLast72Hours(p.getHadSexWithoutCondomUnkHivStatLast72Hours());
        if (p.getSharedInjectionEquipmentUnkHivStatLast72Hours() != null)
            s.setSharedInjectionEquipmentUnkHivStatLast72Hours(p.getSharedInjectionEquipmentUnkHivStatLast72Hours());
        if (p.getHadColdFluSymptomsLast2Weeks() != null) s.setHadColdFluSymptomsLast2Weeks(p.getHadColdFluSymptomsLast2Weeks());
        if (p.getHadUnprotectedSexOrSharedMaterialsLast28Days() != null)
            s.setHadUnprotectedSexOrSharedMaterialsLast28Days(p.getHadUnprotectedSexOrSharedMaterialsLast28Days());
        if (p.getFemaleVaginalDischargeOrBurning() != null) s.setFemaleVaginalDischargeOrBurning(p.getFemaleVaginalDischargeOrBurning());
        if (p.getFemaleLowerAbdominalPain() != null) s.setFemaleLowerAbdominalPain(p.getFemaleLowerAbdominalPain());
        if (p.getMaleUrethralDischargeOrBurning() != null) s.setMaleUrethralDischargeOrBurning(p.getMaleUrethralDischargeOrBurning());
        if (p.getMaleScrotumSwellingAndPain() != null) s.setMaleScrotumSwellingAndPain(p.getMaleScrotumSwellingAndPain());
        if (p.getGenitalSores() != null) s.setGenitalSores(p.getGenitalSores());
        if (p.getSwollenInguinalLymphNodes() != null) s.setSwollenInguinalLymphNodes(p.getSwollenInguinalLymphNodes());
        if (p.getAnalPainOnStooling() != null) s.setAnalPainOnStooling(p.getAnalPainOnStooling());
        if (p.getAnalItching() != null) s.setAnalItching(p.getAnalItching());
        if (p.getAnalDischarge() != null) s.setAnalDischarge(p.getAnalDischarge());
        setIfPresent(p.getLastHivTestTimeframe(), s::setLastHivTestTimeframe, this::mapPrepLastTestTime);
        if (p.getRecommendedForHivRetest() != null) s.setRecommendedForHivRetest(p.getRecommendedForHivRetest());
        if (p.getTestedInOtherClinicalSettings() != null) s.setTestedInOtherClinicalSettings(p.getTestedInOtherClinicalSettings());
        if (p.getHivTestResult() != null) s.setHivTestResult(p.getHivTestResult());
        if (p.getReportsOngoingHivRiskBehaviors() != null) s.setReportsOngoingHivRiskBehaviors(p.getReportsOngoingHivRiskBehaviors());
        if (p.getReportsSpecificHivExposureLast3Months() != null)
            s.setReportsSpecificHivExposureLast3Months(p.getReportsSpecificHivExposureLast3Months());
        if (p.getAcuteHivInfectionRetestRecommended() != null)
            s.setAcuteHivInfectionRetestRecommended(p.getAcuteHivInfectionRetestRecommended());
        if (p.getHivNegative() != null) s.setHivNegative(p.getHivNegative());
        if (p.getHivRiskScoreAtLeastOne() != null) s.setHivRiskScoreAtLeastOne(p.getHivRiskScoreAtLeastOne());
        if (p.getNoSignsOfAcuteHivInfection() != null) s.setNoSignsOfAcuteHivInfection(p.getNoSignsOfAcuteHivInfection());
        if (p.getNoIndicationForPep() != null) s.setNoIndicationForPep(p.getNoIndicationForPep());
        if (p.getHasNoProteinuria() != null) s.setHasNoProteinuria(p.getHasNoProteinuria());
        if (p.getNoHistoryOfLiverAbnormalities() != null) s.setNoHistoryOfLiverAbnormalities(p.getNoHistoryOfLiverAbnormalities());
        if (p.getNoHistoryOfDrugDrugInteractionsForInjectable() != null)
            s.setNoHistoryOfDrugDrugInteractionsForInjectable(p.getNoHistoryOfDrugDrugInteractionsForInjectable());
        if (p.getNoHistoryOfDrugHypersensitivity() != null) s.setNoHistoryOfDrugHypersensitivity(p.getNoHistoryOfDrugHypersensitivity());
        if (p.getPrepOffered() != null) s.setPrepOffered(p.getPrepOffered());
        if (p.getWillingToCommencePrEP() != null) s.setWillingToCommencePrEP(p.getWillingToCommencePrEP());
        if (p.getReceivedPrepForFirstTimeThisYear() != null) s.setReceivedPrepForFirstTimeThisYear(p.getReceivedPrepForFirstTimeThisYear());
        if (p.getClientReferredToOtherServices() != null) s.setClientReferredToOtherServices(p.getClientReferredToOtherServices());
        if (p.getReferralServiceSpecify() != null) s.setReferralServiceSpecify(p.getReferralServiceSpecify());
        if (p.getDeclineNoNeedForPrep() != null) s.setDeclineNoNeedForPrep(p.getDeclineNoNeedForPrep());
        if (p.getDeclineDoesNotWishDailyMedication() != null) s.setDeclineDoesNotWishDailyMedication(p.getDeclineDoesNotWishDailyMedication());
        if (p.getDeclineConcernAboutSideEffects() != null) s.setDeclineConcernAboutSideEffects(p.getDeclineConcernAboutSideEffects());
        if (p.getDeclineConcernAboutStigmatization() != null) s.setDeclineConcernAboutStigmatization(p.getDeclineConcernAboutStigmatization());
        if (p.getDeclineConcernAboutClinicFollowupTime() != null) s.setDeclineConcernAboutClinicFollowupTime(p.getDeclineConcernAboutClinicFollowupTime());
        if (p.getDeclineConcernAboutSafetyOfMedication() != null) s.setDeclineConcernAboutSafetyOfMedication(p.getDeclineConcernAboutSafetyOfMedication());
        if (p.getDeclineConcernAboutEffectiveness() != null) s.setDeclineConcernAboutEffectiveness(p.getDeclineConcernAboutEffectiveness());
        if (p.getDeclineOtherReasonSpecify() != null) s.setDeclineOtherReasonSpecify(p.getDeclineOtherReasonSpecify());


        return s;
    }


    private PrepPepCardEnrollmentType mapCardEnrollment(
            final PrepDTO p,
            final ObjectFactory factory) {


        if (p.getUniqueId() == null || p.getUniqueId().trim().isEmpty()) {
            log.warn("Skipping card enrollment mapping: UniqueId is missing for client hospital number: {}",
                    p.getHospitalNumber());
            return null;
        }


        if (!hasEnrollmentData(p)) {
            return null;
        }


        final PrepPepCardEnrollmentType e = factory.createPrepPepCardEnrollmentType();


        validateAndSet(p.getHospitalNumber(), e::setHospitalNumber, "HospitalNumber");
        if (p.getEnrollmentType() != null) e.setEnrollmentType(p.getEnrollmentType());
        setIfPresent(p.getEnrollmentType(), e::setEnrollmentType, this::mapEnrollmentType);
        validateAndSet(p.getUniqueId(), e::setUniqueId, "UniqueId");
        validateAndSetDate(p.getDateEnrolled(), e::setDateEnrolled, "DateEnrolled");
        if (p.getPartnerAncOrUniqueArtNumber() != null) e.setPartnerAncOrUniqueArtNumber(p.getPartnerAncOrUniqueArtNumber());
        if (p.getSex() != null) e.setSex(p.getSex());
        if (p.getAge() != null) e.setAge(p.getAge());
        setIfPresent(p.getMaritalStatus(), e::setMaritalStatus, this::mapMaritalStatus);
        if (p.getOccupation() != null) e.setOccupation(p.getOccupation());
        setIfPresent(p.getEducationLevel(), e::setEducationLevel, this::mapEducationLevel);
        setIfPresent(p.getHivTestingPoint(), e::setHivTestingPoint, this::mapHtsEntryPoint);
        if (p.getDateOfLastHivTest() != null) e.setDateOfLastHivTest(toXMLGregorianCalendar(p.getDateOfLastHivTest()));
        setIfPresent(p.getHivTestResult(), e::setHivTestResult, this::mapHtsTestResult);
        if (p.getDateReferred() != null) e.setDateReferred(toXMLGregorianCalendar(p.getDateReferred()));
        setIfPresent(p.getPopulationType(), e::setPopulationType, this::mapPopulationType);
        if (p.getOtherPopulationSpecify() != null) e.setOtherPopulationSpecify(p.getOtherPopulationSpecify());
        if (p.getDateInitialAdherenceCounseling() != null) e.setDateInitialAdherenceCounseling(toXMLGregorianCalendar(p.getDateInitialAdherenceCounseling()));
        if (p.getDateStarted() != null) e.setDateStarted(toXMLGregorianCalendar(p.getDateStarted()));
        setIfPresent(p.getPrepTypeAtStart(), e::setPrepTypeAtStart, this::mapPrepType);
        setIfPresent(p.getPrepRegimen(), e::setPrepRegimen, this::mapPrepRegimen);
        if (p.getWeight() != null) e.setWeight(p.getWeight());
        if (p.getHeight() != null) e.setHeight(p.getHeight());
        if (p.getBMI() != null) e.setBMI(p.getBMI());
        if (p.getIsPregnant() != null) e.setIsPregnant(p.getIsPregnant());
        if (p.getIsBreastfeeding() != null) e.setIsBreastfeeding(p.getIsBreastfeeding());
        if (p.getHistoryOfDrugAllergies() != null) e.setHistoryOfDrugAllergies(p.getHistoryOfDrugAllergies());
        setIfPresent(p.getHistoryOfDrugDrugInteractions(), e::setHistoryOfDrugDrugInteractions, this::mapPrepDrugInteraction);
        setIfPresent(p.getUrinalysisResult(), e::setUrinalysisResult, this::mapUrinalysis);
        if (p.getLiverFunctionTestResult() != null) e.setLiverFunctionTestResult(p.getLiverFunctionTestResult());
        setIfPresent(p.getReferredAtInitiation(), e::setReferredAtInitiation, this::mapYesNoToCode);
        if (p.getDateReferredAtInitiation() != null) e.setDateReferredAtInitiation(toXMLGregorianCalendar(p.getDateReferredAtInitiation()));
        setIfPresent(p.getServiceReferredFor(), e::setServiceReferredFor, this::mapServicesProvided);
        setIfPresent(p.getPepCompletion(), e::setPepCompletion, this::mapYesNoToCode);


        return e;
    }


    private boolean hasScreeningData(final PrepDTO p) {
        if (p == null) return false;
        boolean hasValidClientId = (p.getUniqueClientId() != null && !p.getUniqueClientId().trim().isEmpty());
        boolean hasVisitDate = p.getDateOfVisit() != null;
        return hasValidClientId && hasVisitDate;
    }


    private boolean hasEnrollmentData(final PrepDTO p) {
        if (p == null) return false;
        boolean hasValidHospitalNo = (p.getHospitalNumber() != null && !p.getHospitalNumber().trim().isEmpty());
        boolean hasValidUniqueId = (p.getUniqueId() != null && !p.getUniqueId().trim().isEmpty());
        boolean hasEnrollmentDate = p.getDateEnrolled() != null;
        return (hasValidUniqueId || hasValidHospitalNo) && hasEnrollmentDate;
    }


    private boolean hasDiscontinuationData(final PrepDTO p) {
        if (p == null) return false;
        return p.getDiscontinuedPrepPepReason() != null
                || p.getDiscontinuedPrepPepDate() != null
                || p.getDateClientReferredOut() != null
                || p.getDateClientDied() != null
                || (p.getInterruptions() != null && !p.getInterruptions().isEmpty());
    }


    private String mapVisitType(String method) {
        if (method == null || method.isEmpty()) return "1";
        return VISIT_TYPE_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapPopulationType(String method) {
        if (method == null || method.isEmpty()) return "1";
        return POPULATION_TYPE_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapPregnancyStatus(String method) {
        if (method == null || method.isEmpty()) return "NP";
        return PREGNANCY_STATUS_MAPPING.getOrDefault(method.toUpperCase().trim(), "NP");
    }


    private String mapSexPartner(String method) {
        if (method == null || method.isEmpty()) return "Both";
        String upperType = method.contains("SEX_PARTNERS_") ? method.toUpperCase().trim() : method.trim();
        return SEX_PARTNER_MAPPING.getOrDefault(upperType, "Both");
    }


    private String mapPrepSource(String method) {
        if (method == null || method.isEmpty()) return "Self";
        return REFERREDFROM_MAPPING.getOrDefault(method.toUpperCase().trim(), "Self");
    }


    private String mapPrepSetting(String method) {
        if (method == null || method.isEmpty()) return "1";
        return SETTING_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapPrepSession(String method) {
        if (method == null || method.isEmpty()) return "NP";
        return TYPE_SESSION_MAPPING.getOrDefault(method.toUpperCase().trim(), "NP");
    }


    private String mapEnrollmentType(String method) {
        if (method == null || method.isEmpty()) return "PrEP";
        return ENROLLMENT_TYPE_MAPPING.getOrDefault(method.toUpperCase().trim(), "PrEP");
    }


    private String mapMaritalStatus(String method) {
        if (method == null || method.isEmpty()) return "1";
        return MARITAL_STATUS_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapEducationLevel(String method) {
        if (method == null || method.isEmpty()) return "1";
        return EDUCATION_LEVEL_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapHtsEntryPoint(String method) {
        if (method == null || method.isEmpty()) return "1";
        return HTS_ENTRY_POINT_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapHtsTestResult(String method) {
        if (method == null || method.isEmpty()) return "Negative";
        return HIV_TEST_RESULT_MAPPING.getOrDefault(method.toUpperCase().trim(), "Negative");
    }


    private String mapPrepType(String method) {
        if (method == null || method.isEmpty()) return "1";
        return PREP_TYPE_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapPrepRegimen(String method) {
        if (method == null || method.isEmpty()) return "1";
        return PREP_REGIMEN_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapUrinalysis(String method) {
        if (method == null || method.isEmpty()) return "1";
        return PREP_URINALYSIS_RESULT_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapServicesProvided(String method) {
        if (method == null || method.isEmpty()) return "1";
        return PREP_SERVICE_PROVIDED_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapPrepDrugInteraction(String method) {
        if (method == null || method.isEmpty()) return "1";
        return PREP_HISTORY_OF_DRUG_INTERACTIONS_MAPPING.getOrDefault(method.toUpperCase().trim(), "1");
    }


    private String mapPrepLastTestTime(String method) {
        if (method == null || method.isEmpty()) return "LessThan1Month";
        return LAST_HIV_TEST_TIME_MAPPING.getOrDefault(method.trim(), "LessThan1Month");
    }


    private String mapYesNoToCode(String value) {
        if (value == null || value.isEmpty()) {
            return "No";
        }
        String upperValue;
        if (value.contains("false") || value.contains("true")) {
            upperValue = value.toUpperCase().trim();
            if (upperValue.equals("TRUE")) return "Yes";
            if (upperValue.equals("FALSE")) return "No";
        } else {
            upperValue = value.toUpperCase().replace("YES_NO_", "").trim();
            if (upperValue.equals("YES")) return "Yes";
            if (upperValue.equals("NO")) return "No";
        }
        return "No";
    }


    private <T> void validateAndSet(T value, Consumer<T> setter, String fieldName) {
        if (value != null) {
            setter.accept(value);
        } else {
            log.info("Validation failed for: {}", fieldName);
        }
    }


    private void validateAndSetDate(LocalDate date, Consumer<javax.xml.datatype.XMLGregorianCalendar> setter, String fieldName) {
        if (date != null) {
            try {
                setter.accept(DateUtil.getXmlDate(Date.valueOf(date)));
            } catch (DatatypeConfigurationException e) {
                log.info("Validation failed for: {} {}", fieldName, date);
            }
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
    private XMLGregorianCalendar toXMLGregorianCalendar(final LocalDate date) {
        if (date == null) {
            return null;
        }
        final GregorianCalendar gcal = GregorianCalendar.from(date.atStartOfDay(DEFAULT_ZONE_ID));
        return DATATYPE_FACTORY.newXMLGregorianCalendar(gcal);
    }
}
