package org.lamisplus.modules.ndr.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.HtsReportDto;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PartnerNotificationDTO;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.processor.SpringErrorClassTagProcessor;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class HtsTypeMapper {


    public boolean getHivTestingReportType(
            IndividualReportType individualReportType,
            ObjectFactory objectFactory,
            List<HtsReportDto> htsReportDtos,
            List<NDRErrorDTO> errors) {
        if (!htsReportDtos.isEmpty()) {
            List<HIVTestingReportType> hivTestingReport = individualReportType.getHIVTestingReport();
            htsReportDtos.parallelStream().forEach(h -> {
                try {
                    HIVTestingReportType hivTestingReportType = NDRObjectFactory.createHIVTestingReportType();
                    validateAndSetClientCode(h.getClientCode(), hivTestingReportType);
                    validateAndSetVisitId(h.getVisitId(), hivTestingReportType);
                    validateAndSetVisitDate(h.getVisitDate(), hivTestingReportType);
                    validateAndSetSetting(h.getSetting(), hivTestingReportType);
                    validateAndSetFirstTime(h.getFirstTimeVisit(), hivTestingReportType);
                    // add referred_from and session type here
                    validateAndSetReferedFrom(h.getReferredFrom(), hivTestingReportType);
                    validateAndSetMaritalStatus(h.getMaritalStatus(), hivTestingReportType);
                    validateAndSetNumOfChildren(h.getNoOfOwnChildrenLessThan5Years(), hivTestingReportType);
                    validateAndSetNumWives(h.getNoOfAllWives(), hivTestingReportType);
                    validateAndSetIsIndex(h.getIsIndexClient(), hivTestingReportType);
                    validateAndSetIsIndexClientId(h.getIndexClientId(), hivTestingReportType);
                    // add resTestForResult here
                    PreTestInformationType preTestInformationType = setPreTest(objectFactory, h);
                    hivTestingReportType.setPreTestInformation(preTestInformationType);
                    PostTestCounsellingType postTestCounsellingType = setPostTest(objectFactory, h);
                    validateAndSetSyphilisResult(h.getSyphilisTestResult(), hivTestingReportType);
                    validAndSetHbV(h.getHbvTestResult(), hivTestingReportType);
                    validAndSetHcV(h.getHcvTestResult(), hivTestingReportType);



                    IndexNotificationServicesType indexNotificationServicesType = new IndexNotificationServicesType();
                    //List<PartnerNotificationType> partnerNotifications = getAllPartnerNotification(query);

                    List<PartnerNotificationType> partnerNotifications = getAllPartnerNotification(h, errors);
                    log.info("List of partner notification size {} ", partnerNotifications.size());
                    indexNotificationServicesType.getPartner().addAll(partnerNotifications);
                    hivTestingReportType.setIndexNotificationServices(indexNotificationServicesType);

                    hivTestingReportType.setPostTestCounselling(postTestCounsellingType);
                    HIVTestResultType hivTestResultType = setResult(objectFactory, h);
                    hivTestingReportType.setHIVTestResult(hivTestResultType);
                    hivTestingReport.add(hivTestingReportType);
                } catch (Exception e) {
                    errors.add(new NDRErrorDTO(h.getClientCode(), h.getVisitId(), Arrays.toString(e.getStackTrace())));
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    private List<PartnerNotificationDTO> getPartnerNotifications (HtsReportDto h, List<NDRErrorDTO> ndrErrors) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(h.getPartnerNotification(), typeFactory.constructCollectionType(List.class, PartnerNotificationDTO.class));
        } catch (Exception e) {
            log.error("Error reading partner notification of patient with uuid {}  errorMsg {}",
                    h.getClientCode(), e.getMessage());
            ndrErrors.add(new NDRErrorDTO(h.getClientCode(),  "", e.getMessage()));
        }
        return new ArrayList<>();
    }

    private List<PartnerNotificationType> getAllPartnerNotification (HtsReportDto h, List<NDRErrorDTO> ndrErrors ) {
        List<PartnerNotificationType> partnerNotifications = new ArrayList<>();
        List<PartnerNotificationDTO> partnerNotificationsDto = getPartnerNotifications(h, ndrErrors);
        for (PartnerNotificationDTO p : partnerNotificationsDto) {
            log.info("Hts in a loop {}", h.getPartnerNotification());
            PartnerNotificationType partner = new PartnerNotificationType();
            partner.setPartnername(p.getPartnername());
            partner.setDescriptiveAddress(p.getDescriptiveAddress());
            partner.setPhoneNumber(p.getPhoneNumber());
            String gender = p.getPartnerGender();
            if (gender != null) {
                if (gender.contains("Male")) {
                    gender = "M";
                } else {
                    gender = "F";
                }
                partner.setPartnerGender(gender);
            }

            String indexRelation = p.getIndexRelation();
            if (indexRelation != null) {
                if (indexRelation.contains("Biological")) {
                    indexRelation = "1";
                } else if (indexRelation.contains("Sexual")) {
                    indexRelation = "2";
                } else if (indexRelation.contains("Social")) {
                    indexRelation = "3";
                }
                partner.setIndexRelation(indexRelation);
            }

            partnerNotifications.add(partner);
        }

        return partnerNotifications;
    }

//    private HIVTestResultType setResult(ObjectFactory objectFactory, HtsReportDto h) {
//        HIVTestResultType hivTestResultType = objectFactory.createHIVTestResultType();
//        try {
//
//            TestResultType testResult = objectFactory.createTestResultType();
//            if (h.getScreeningTestResult() != null) {
//                if (h.getScreeningTestResult().equalsIgnoreCase("positive")) {
//                    testResult.setScreeningTestResult("R");
//                } else {
//                    testResult.setScreeningTestResult("NR");
//                }
//            } else {
//                log.error("ScreeningTestResult can not be null kindly correct");
//                throw new IllegalArgumentException("ScreeningTestResult can not be null kindly correct");
//            }
//            validateAndSetTestResultDate(h.getScreeningTestResultDate(), testResult);
//            if (h.getConfirmatoryTestResult().equalsIgnoreCase("positive")) {
//                testResult.setConfirmatoryTestResult("R");
//                testResult.setFinalTestResult("Pos");
//                testResult.setTieBreakerTestResult("R");
//            } else {
//                testResult.setConfirmatoryTestResult("NR");
//                testResult.setFinalTestResult("Neg");
//                testResult.setTieBreakerTestResult("NR");
//            }
//
//            validateAndSetConfirmatoryTestResultDate(h.getConfirmatoryTestResultDate(), testResult);
//            validateAndSetTieBreakerTestResultDate(h.getScreeningTestResultDate(), testResult);
//            hivTestResultType.setTestResult(testResult);
//
////            if(h.getRecencyNumber() == null && h.getConfirmatoryTestResult().equalsIgnoreCase("positive")) {
////                log.error("Recency number is null for client {}", h.getClientCode());
////                throw new IllegalArgumentException("Recency Number can not be null kindly correct");
////            }else {
//////                if (testResult.getFinalTestResult().equals("Pos") && !StringUtils.isBlank(h.getRecencyNumber())) {
////                if (!StringUtils.isBlank(h.getRecencyNumber()) && h.getConfirmatoryTestResult().equalsIgnoreCase("positive")) {
////                    processAndSetRecencyResult(hivTestResultType, objectFactory, h);
////                }
////            }
//
//            if (!StringUtils.isBlank(h.getRecencyNumber()) && h.getConfirmatoryTestResult().equalsIgnoreCase("positive")) {
//                processAndSetRecencyResult(hivTestResultType, objectFactory, h);
//                return hivTestResultType;
//            }
//            if (h.getConfirmatoryTestResult().equalsIgnoreCase("negative")){
//                return hivTestResultType;
//            }
//            if (h.getConfirmatoryTestResult().equalsIgnoreCase("positive") && StringUtils.isBlank(h.getRecencyNumber()) ){
//                return hivTestResultType;
//            }
//
//        }catch (Exception e) {
//            log.error("An error occur while fetching recency records for patient with information {}", e.getMessage());
//        }
//        return null;
//    }



    private HIVTestResultType setResult(ObjectFactory objectFactory, HtsReportDto h) {
        HIVTestResultType hivTestResultType = objectFactory.createHIVTestResultType();
        try {
            TestResultType testResult = objectFactory.createTestResultType();

            // Validate and set Screening Test Result
            if (h.getScreeningTestResult() != null) {
                testResult.setScreeningTestResult(
                        h.getScreeningTestResult().equalsIgnoreCase("positive") ? "R" : "NR"
                );
            } else {
                log.error("ScreeningTestResult cannot be null. Kindly correct this.");
                throw new IllegalArgumentException("ScreeningTestResult cannot be null.");
            }

            validateAndSetTestResultDate(h.getScreeningTestResultDate(), testResult);

            // Validate and set Confirmatory Test Result
            if (h.getConfirmatoryTestResult() == null || h.getConfirmatoryTestResult().isEmpty()) {
                log.error("Test Result is null for client {}", h.getClientCode());
                throw new IllegalArgumentException("Test Result cannot be null. Kindly correct this.");
            } else if ("positive".equalsIgnoreCase(h.getConfirmatoryTestResult())) {
                testResult.setConfirmatoryTestResult("R");
                testResult.setFinalTestResult("Pos");
                testResult.setTieBreakerTestResult("R");
            } else if ("negative".equalsIgnoreCase(h.getConfirmatoryTestResult())) {
                testResult.setConfirmatoryTestResult("NR");
                testResult.setFinalTestResult("Neg");
                testResult.setTieBreakerTestResult("NR");
            } else {
                log.error("Invalid ConfirmatoryTestResult for client {}", h.getClientCode());
                throw new IllegalArgumentException("Invalid ConfirmatoryTestResult. Kindly correct this.");
            }

            validateAndSetConfirmatoryTestResultDate(h.getConfirmatoryTestResultDate(), testResult);
            validateAndSetTieBreakerTestResultDate(h.getScreeningTestResultDate(), testResult);

            hivTestResultType.setTestResult(testResult);

            // Process Recency Results based on conditions
            if (("positive".equalsIgnoreCase(h.getConfirmatoryTestResult()) //&& (StringUtils.isBlank(h.getRecencyNumber()) || h.getRecencyNumber().isEmpty() || h.getRecencyNumber() == null)
                    && (StringUtils.isBlank(h.getFinalRecencyTestResult()) || h.getFinalRecencyTestResult().isEmpty() || h.getFinalRecencyTestResult() == null ))) {
                log.info("Processing recency result: {} {}", h.getRecencyNumber(), h.getFinalRecencyTestResult());
                return hivTestResultType;
            }

            if ((!StringUtils.isBlank(h.getRecencyNumber()) && "positive".equalsIgnoreCase(h.getConfirmatoryTestResult()) && !StringUtils.isBlank(h.getFinalRecencyTestResult()))) {
                log.info("Processing recency result: {} {}", h.getRecencyNumber(), h.getFinalRecencyTestResult());
                processAndSetRecencyResult(hivTestResultType, objectFactory, h);
                return hivTestResultType;
            }

            if ("negative".equalsIgnoreCase(h.getConfirmatoryTestResult())) {
                return hivTestResultType;
            }


        } catch (Exception e) {
            log.error("An error occurred while fetching recency records for client: {}", e.getMessage());
        }
        return null;
    }


    private void processAndSetRecencyResult(HIVTestResultType testResult, ObjectFactory objectFactory, HtsReportDto h) {
        RecencyTestingType recency = objectFactory.createRecencyTestingType();
        validateAndSetRecencyYNCode(h, recency);
        validateAndSetStringValues(h, recency);
        validateDateValuesForRecencyData(h, recency);

        if (h.getFinalRecencyTestResult() != null) {
            if (h.getFinalRecencyTestResult().equalsIgnoreCase("RITA Long term")) {
                recency.setFinalRecencyTestResult(RecencyStatus.RITA_LONG_TERM.getValue());
            } else if (h.getFinalRecencyTestResult().equalsIgnoreCase("Rita Recent")) {
                recency.setFinalRecencyTestResult(RecencyStatus.RITA_RECENT.getValue());
            } else {
                recency.setFinalRecencyTestResult(RecencyStatus.RITA_INCONCLUSIVE.getValue());
            }
        } else {
            recency.setFinalRecencyTestResult(RecencyStatus.RITA_INCONCLUSIVE.getValue());
        }
        log.info("recency {} recency number {}",  recency, h.getRecencyNumber());
        testResult.setRecencyTesting(recency);
    }

    private void validateDateValuesForRecencyData(HtsReportDto h, RecencyTestingType recencyTesting) {
        if (h.getTestDate() != null) {
            try {
                recencyTesting.setTestDate(DateUtil.getXmlDate(Date.valueOf(h.getTestDate())));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("TestDate can not be null kindly correct this");
        }

        if (h.getDateSampleSent() != null) {
            try {
                recencyTesting.setDateSampleSent(DateUtil.getXmlDate(Date.valueOf(h.getDateSampleSent())));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("DateSampleSent can not be null kindly correct this");
            //throw new IllegalArgumentException("DateSampleSent can not be null kindly correct this");
        }

        if (h.getDateSampleCollected() != null) {
            try {
                recencyTesting.setDateSampleCollected(DateUtil.getXmlDate(Date.valueOf(h.getDateSampleCollected())));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("DateSampleCollected can not be null kindly correct this");
            //throw new IllegalArgumentException("DateSampleCollected can not be null kindly correct this");
        }

        if (h.getViralLoadConfirmationTestDate() != null) {
            try {
                recencyTesting.setViralLoadConfirmationTestDate(DateUtil.getXmlDate(Date.valueOf(h.getViralLoadConfirmationTestDate())));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
//		else {
//			throw new IllegalArgumentException("ViralLoadConfirmationTestDate can not be null kindly correct this");
//		}


    }

    private static void validateAndSetStringValues(HtsReportDto h, RecencyTestingType recencyTesting) {
        validateNotNull(h.getTestName(), "TestName");
        if (h.getTestName().contains("Asante")) {
           log.info("test name {}", RecencyTestName.ASANTE.getValue());
            recencyTesting.setTestName(RecencyTestName.ASANTE.getValue());
        } else {
            log.info("test name {}", RecencyTestName.OTHERS.getValue());
            recencyTesting.setTestName(RecencyTestName.OTHERS.name());
        }
        recencyTesting.setViralLoadConfirmationResult(h.getViralLoadConfirmationResult());
        if (h.getSampleType() != null) {
            if(h.getSampleType().equalsIgnoreCase("Plasma")){
                recencyTesting.setSampleType("P");
            }else {
                recencyTesting.setSampleType("D");
            }
        } else {
            recencyTesting.setSampleType("");
        }

        if (h.getPcrLab() != null) {
            recencyTesting.setPCRLab(h.getPcrLab());
        } else {
            recencyTesting.setPCRLab("");
        }

        validateNotNull(h.getRecencyNumber(), "RecencyNumber");
        recencyTesting.setRecencyNumber(h.getRecencyNumber());

        //interpretation
        validateNotNull(h.getRecencyInterpretation(), "RecencyInterpretation");
        if (h.getRecencyInterpretation().contains("Recent")) {
            recencyTesting.setRecencyInterpretation(RecencyInterpretation.RECENT.getValue());
            recencyTesting.setRapidRecencyAssay(RecencyInterpretation.RECENT.getValue());
        } else if (h.getRecencyInterpretation().contains("Long Term")) {
            recencyTesting.setRecencyInterpretation(RecencyInterpretation.LONG_TERM.getValue());
            recencyTesting.setRapidRecencyAssay(RecencyInterpretation.LONG_TERM.getValue());
        } else if (h.getRecencyInterpretation().contains("Negative")) {
            recencyTesting.setRecencyInterpretation(RecencyInterpretation.NEGATIVE.getValue());
            recencyTesting.setRapidRecencyAssay(RecencyInterpretation.LONG_TERM.getValue());
        } else {
            recencyTesting.setRecencyInterpretation(RecencyInterpretation.INVALID.getValue());
            recencyTesting.setRapidRecencyAssay(RecencyInterpretation.LONG_TERM.getValue());
        }
        if (h.getSampleReferenceNumber() != null) {
            recencyTesting.setSampleReferenceNumber(h.getSampleReferenceNumber());
        } else {
            recencyTesting.setSampleReferenceNumber("");
        }
        if (h.getViralLoadClassification() != null) {
            recencyTesting.setViralLoadClassification(h.getViralLoadClassification());
        } else {
            recencyTesting.setViralLoadClassification("");
        }
    }

    private static void validateAndSetRecencyYNCode(HtsReportDto h, RecencyTestingType recencyTesting) {
        if (h.getConsent() != null && (h.getConsent().equalsIgnoreCase("false") || h.getConsent().equalsIgnoreCase("no"))) {
            recencyTesting.setConsent(YNCodeType.YES);
        } else {
            recencyTesting.setConsent(YNCodeType.NO);
        }

        if (h.getControlLine() != null && (h.getControlLine().equalsIgnoreCase("yes") || h.getControlLine().equalsIgnoreCase("true"))) {
            recencyTesting.setControlLine(YNCodeType.YES);
        } else {
            recencyTesting.setControlLine(YNCodeType.NO);
        }

        if (h.getVerificationLine() != null
                && (h.getVerificationLine().equalsIgnoreCase("yes")
                || h.getVerificationLine().equalsIgnoreCase("true"))) {
            recencyTesting.setVerificationLine(YNCodeType.YES);
        } else {
            recencyTesting.setVerificationLine(YNCodeType.NO);
        }

        if (h.getLongTermLine() != null && (h.getLongTermLine().equalsIgnoreCase("yes") || h.getLongTermLine().equalsIgnoreCase("true"))) {
            recencyTesting.setLongTermLine(YNCodeType.YES);
        } else {
            recencyTesting.setLongTermLine(YNCodeType.NO);
        }

        if (h.getViralLoadRequest() != null &&
                (h.getViralLoadRequest().equalsIgnoreCase("yes") || h.getViralLoadRequest().equalsIgnoreCase("true"))) {
            recencyTesting.setViralLoadRequest(YNCodeType.YES);
        } else {
            recencyTesting.setViralLoadRequest(YNCodeType.NO);
        }
    }

    private static PreTestInformationType setPreTest(ObjectFactory objectFactory, HtsReportDto h) {
        //uncomment KnowledgeAssessment
		KnowledgeAssessmentType knowledgeAssessment = objectFactory.createKnowledgeAssessmentType();
		knowledgeAssessment.setPreviouslyTestedHIVNegative(h.getPreviouslyTestedHIVNegative());
		knowledgeAssessment.setClientInformedAboutHIVTransmissionRoutes(h.getClientInformedAboutHIVTransmissionRoutes());
		knowledgeAssessment.setClientPregnant(h.getClientPregnant());
		knowledgeAssessment.setClientInformedOfHIVTransmissionRiskFactors(h.getClientInformedOfHIVTransmissionRiskFactors());
		knowledgeAssessment.setClientInformedAboutPreventingHIV(h.getClientInformedAboutPreventingHIV());
		knowledgeAssessment.setClientInformedAboutPossibleTestResults(h.getClientInformedAboutPossibleTestResults());
		knowledgeAssessment.setInformedConsentForHIVTestingGiven(h.getInformedConsentForHIVTestingGiven());

        HIVRiskAssessmentType assessment = objectFactory.createHIVRiskAssessmentType();
        if (isNotNull(h.getEverHadSexualIntercourse())) {
            assessment.setEverHadSexualIntercourse(h.getEverHadSexualIntercourse());
        }
        if (isNotNull(h.getBloodTransfussionInLast3Months())) {
            assessment.setBloodTransfussionInLast3Months(h.getBloodTransfussionInLast3Months());
        }
        if (isNotNull(h.getStiInLast3Months())) {
            assessment.setUnprotectedSexWithCasualPartnerinLast3Months(h.getUnprotectedSexWithCasualPartnerinLast3Months());
        }
        if (isNotNull(h.getUnprotectedSexWithRegularPartnerInLast3Months())) {
            assessment.setUnprotectedSexWithRegularPartnerInLast3Months(h.getUnprotectedSexWithRegularPartnerInLast3Months());
        }
        if (isNotNull(h.getMoreThan1SexPartnerDuringLast3Months())) {
            assessment.setMoreThan1SexPartnerDuringLast3Months(h.getMoreThan1SexPartnerDuringLast3Months());
        }
        if (isNotNull(h.getStiInLast3Months())) {
            assessment.setSTIInLast3Months(h.getStiInLast3Months());
        }
        ClinicalTBScreeningType screening = objectFactory.createClinicalTBScreeningType();
        if (isNotNull(h.getCurrentlyCough())) {
            screening.setCurrentlyCough(h.getCurrentlyCough());
        }
        if (isNotNull(h.getWeightLoss())) {
            screening.setWeightLoss(h.getWeightLoss());
        }
        if (isNotNull(h.getFever())) {
            screening.setFever(h.getFever());
        }
        if (isNotNull(h.getNightSweats())) {
            screening.setNightSweats(h.getNightSweats());
        }
        SyndromicSTIScreeningType synStiScreening = objectFactory.createSyndromicSTIScreeningType();
        if (isNotNull(h.getVaginalDischargeOrBurningWhenUrinating())) {
            synStiScreening.setVaginalDischargeOrBurningWhenUrinating(h.getVaginalDischargeOrBurningWhenUrinating());
        }
        if (isNotNull(h.getLowerAbdominalPainsWithOrWithoutVaginalDischarge())) {
            synStiScreening.setLowerAbdominalPainsWithOrWithoutVaginalDischarge(h.getLowerAbdominalPainsWithOrWithoutVaginalDischarge());
        }
        if (isNotNull(h.getUrethralDischargeOrBurningWhenUrinating())) {
            synStiScreening.setUrethralDischargeOrBurningWhenUrinating(h.getUrethralDischargeOrBurningWhenUrinating());
        }
        if (isNotNull(h.getScrotalSwellingAndPain())) {
            synStiScreening.setScrotalSwellingAndPain(h.getScrotalSwellingAndPain());
        }
        if (isNotNull(h.getScrotalSwellingAndPain())) {
            synStiScreening.setGenitalSoreOrSwollenInguinalLymphNodes(h.getGenitalSoreOrSwollenInguinalLymphNodes());
        }
        PreTestInformationType preTestInformationType = objectFactory.createPreTestInformationType();

        if (isNotNull(screening)) {
            preTestInformationType.setClinicalTBScreening(screening);
        }

        if (isNotNull(assessment)) {
            preTestInformationType.setHIVRiskAssessment(assessment);
        }
        if (isNotNull(synStiScreening)) {
            preTestInformationType.setSyndromicSTIScreening(synStiScreening);
        }

        preTestInformationType.setKnowledgeAssessment(knowledgeAssessment);
        return preTestInformationType;

    }

    private static PostTestCounsellingType setPostTest(ObjectFactory objectFactory, HtsReportDto h) {
        PostTestCounsellingType postTest = objectFactory.createPostTestCounsellingType();
        String testedForHIVBeforeWithinThisYear = h.getTestedForHIVBeforeWithinThisYear();
        if (testedForHIVBeforeWithinThisYear != null) {
            if (testedForHIVBeforeWithinThisYear.equalsIgnoreCase("true")) {
                testedForHIVBeforeWithinThisYear = "1";
            } else {
                testedForHIVBeforeWithinThisYear = "2";
            }
            postTest.setTestedForHIVBeforeWithinThisYear(testedForHIVBeforeWithinThisYear);
        } else {
            throw new IllegalArgumentException("TestedForHIVBeforeWithinThisYear can not be null");
        }
        postTest.setPostTestDisclosurePlanDeveloped(h.getPostTestDisclosurePlanDeveloped());
        postTest.setPostTestCounsellingDone(h.getPostTestCounsellingDone());
        postTest.setProvidedWithInformationOnFPandDualContraception(h.getProvidedWithInformationOnFPandDualContraception());
        postTest.setHIVRequestAndResultFormSignedByTester(h.getHivRequestAndResultFormSignedByTester());
        postTest.setHIVRequestAndResultFormFilledWithCTIForm(h.getHivRequestAndResultFormFilledWithCTIForm());
        postTest.setClientRecievedHIVTestResult(h.getClientRecievedHIVTestResult());
        postTest.setRiskReductionPlanDeveloped(h.getRiskReductionPlanDeveloped());
        postTest.setWillBringPartnerForHIVTesting(h.getWillBringPartnerForHIVTesting());
        postTest.setWillBringOwnChildrenForHIVTesting(h.getWillBringOwnChildrenForHIVTesting());
        postTest.setClientOrPartnerUseFPMethodsOtherThanCondoms(h.getClientOrPartnerUseFPMethodsOtherThanCondoms());
        postTest.setClientOrPartnerUseCondomsAsOneFPMethods(h.getClientOrPartnerUseCondomsAsOneFPMethods());
        postTest.setCorrectCondomUseDemonstrated(h.getCorrectCondomUseDemonstrated());
        postTest.setCondomsProvidedToClient(h.getCondomsProvidedToClient());
        postTest.setClientReferredToOtherServices(h.getClientReferredToOtherServices());
        return postTest;


    }

    private static void validateAndSetFirstTime(String firstTime, HIVTestingReportType hivTestingReportType) {
        if (firstTime != null) {
            if (firstTime.equalsIgnoreCase("yes")) {
                firstTime = "Y";
            } else {
                firstTime = "N";
            }
            hivTestingReportType.setFirstTimeVisit(firstTime);
        } else {
            throw new IllegalArgumentException("FirstTimeVisit can not be null kindly correct this");
        }
    }

    private static void validateAndSetSetting(String setting, HIVTestingReportType hivTestingReportType) {
        if (setting != null) {
            if (setting.contains("CT")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_CT.getValue());
            } else if (setting.contains("TB")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_TB.getValue());
            } else if (setting.contains("STI")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_STI.getValue());
            } else if (setting.contains("FP")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_FP.getValue());
            } else if (setting.contains("WARD")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_WARD.getValue());
            } else if (setting.contains("OUTREACH")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_OUTREACH.getValue());
            } else if (setting.contains("STANDALONE")) {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_STANDALONE_HTS.getValue());
            } else {
                hivTestingReportType.setSetting(HstSetting.TEST_SETTING_OTHERS.getValue());
            }
        } else {
            hivTestingReportType.setSetting(HstSetting.TEST_SETTING_OTHERS.getValue());
        }
    }

    private static void validateAndSetVisitId(String visitId, HIVTestingReportType hivTestingReportType) {
        if (visitId != null) {
            hivTestingReportType.setVisitID(visitId);
        } else {
            throw new IllegalArgumentException("VisitId can not be null kindly correct this");
        }
    }

    private static void validateAndSetClientCode(String clientCode, HIVTestingReportType hivTestingReportType) {
        if (clientCode != null) {
            hivTestingReportType.setClientCode(clientCode);
        } else {
            throw new IllegalArgumentException("ClientCode can not be null kindly correct this");
        }
    }

    private static void validateAndSetVisitDate(LocalDate visitDate, HIVTestingReportType hivTestingReportType) {
        if (visitDate != null) {
            try {
                hivTestingReportType.setVisitDate(DateUtil.getXmlDate(Date.valueOf(visitDate)));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("VisitDate can not be null kindly correct this");
        }
    }

    private static void validateAndSetTestResultDate(LocalDate date, TestResultType testResult) {
        if (date != null) {
            try {
                testResult.setScreeningTestResultDate(DateUtil.getXmlDate(Date.valueOf(date)));
                testResult.setConfirmatoryTestResultDate(DateUtil.getXmlDate(Date.valueOf(date)));
                testResult.setTieBreakerTestResultDate(DateUtil.getXmlDate(Date.valueOf(date)));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(Arrays.toString(e.getStackTrace()));
            }
        } else {
            throw new IllegalArgumentException("TestResultDate can not be null kindly correct this");
        }

    }

    private static void validateAndSetConfirmatoryTestResultDate(LocalDate date, TestResultType testResult) {
        if (date != null) {
            try {
                testResult.setConfirmatoryTestResultDate(DateUtil.getXmlDate(Date.valueOf(date)));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private static void validateAndSetTieBreakerTestResultDate(LocalDate date, TestResultType testResult) {
        if (date != null) {
            try {
                testResult.setTieBreakerTestResultDate(DateUtil.getXmlDate(Date.valueOf(date)));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException(Arrays.toString(e.getStackTrace()));
            }
        }

    }

    public static void validateAndSetSyphilisResult(String syphilisTest, HIVTestingReportType hivTestingReportType) {
        if (syphilisTest != null) {
            if (syphilisTest.equalsIgnoreCase("yes")) {
                syphilisTest = "R";
            } else {
                syphilisTest = "NR";
            }
            hivTestingReportType.setSyphilisTestResult(syphilisTest);
        }
    }

    public static void validAndSetHbV(String hbvTest, HIVTestingReportType hivTestingReportType) {
        if (hbvTest != null) {
            if (hbvTest.equalsIgnoreCase("Yes")){
                hbvTest = "Pos";
            } else {
                hbvTest = "Neg";
            }
            hivTestingReportType.setHBVTestResult(hbvTest);
        }
    }

    public static void validAndSetHcV(String hcvTest, HIVTestingReportType hivTestingReportType) {
        if (hcvTest != null) {
            if (hcvTest.equalsIgnoreCase("Yes")){
                hcvTest = "Pos";
            } else {
                hcvTest = "Neg";
            }
            hivTestingReportType.setHCVTestResult(hcvTest);
        }
    }

    public static void validateAndSetNumOfChildren(Integer numChild, HIVTestingReportType hivTestingReportType) {
        if (numChild != null) {
            hivTestingReportType.setNoOfOwnChildrenLessThan5Years(numChild);
        }
    }

    public static void validateAndSetNumWives (Integer numWives, HIVTestingReportType hivTestingReportType) {
        if (numWives != null) {
            hivTestingReportType.setNoOfAllWives(numWives);
        }
    }

    public static void validateAndSetIsIndex (String isIndex, HIVTestingReportType hivTestingReportType) {
        if(isIndex != null) {
            if(isIndex.equalsIgnoreCase("true")){
                isIndex = "Y";
            } else {
             isIndex = "N";
            }
            hivTestingReportType.setIsIndexClient(isIndex);
        }
}


    public static void validateAndSetMaritalStatus (String maritalStatus, HIVTestingReportType hivTestingReportType) {
        if (maritalStatus != null){
            if (maritalStatus.contains("Married")) {
                maritalStatus = "M";
            } else if (maritalStatus.contains("Single")) {
                maritalStatus = "S";
            } else if (maritalStatus.contains("Widowed")) {
                maritalStatus = "W";
            } else if (maritalStatus.contains("Separated")) {
                maritalStatus = "A";
            } else if (maritalStatus.contains("Divorced")) {
                maritalStatus = "D";
            } else if (maritalStatus.contains("Living together")) {
                maritalStatus = "G";
            }
            hivTestingReportType.setMaritalStatus(maritalStatus);
        }
}

    public static void validateAndSetReferedFrom (String referredFrom, HIVTestingReportType hivTestingReportType) {
        if (referredFrom != null){
            if (referredFrom.contains("Self")) {
                referredFrom = "1";
            } else if (referredFrom.contains("TB")) {
                referredFrom = "2";
            } else if (referredFrom.contains("STI")) {
                referredFrom = "3";
            } else if (referredFrom.contains("FP")) {
                referredFrom = "4";
            } else if (referredFrom.contains("OPD")) {
                referredFrom = "5";
            } else if (referredFrom.contains("Ward")) {
                referredFrom = "6";
            }   else if (referredFrom.contains("Blood Bank")) {
                referredFrom = "7";
            } else if (referredFrom.contains("Others")) {
                referredFrom = "8";
            }
        }
    }

    public static void validateAndSetIsIndexClientId (String indexClientId, HIVTestingReportType hivTestingReportType){
        if(indexClientId != null && !indexClientId.isEmpty()) {
            hivTestingReportType.setIsIndexClient(indexClientId);
        }
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static void validateNotNull(Object obj, String fieldName) {
        if (obj == null) throw new IllegalArgumentException(fieldName + " cannot be null");
    }


}
