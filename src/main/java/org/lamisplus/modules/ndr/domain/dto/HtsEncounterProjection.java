package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface HtsEncounterProjection {
    String getClientCode();
    String getVisitID();
    LocalDate getVisitDate();
    String getSetting();
    String getModality();
    String getOtherModality();
    String getClientAge();
    String getSex();
    String getMaritalStatus();
    Integer getNoOfAllWives();
    Integer getNoOfOwnChildrenLessThan15Years();
    String getStateOfResidence();
    String getLgaOfResidence();
    String getSessionType();
    String getHivstResult();
    String getIndexClientId();
    String getRelationshipToIndex();
    Boolean getClientIsPregnant();
    Boolean getBreastFeeding();
    String getDurationOfBreastfeeding();
    Boolean getPreviouslyTestedNegative();
    String getTimeOfLastNegativeTest();
    Boolean getClientInformedTransmissionRoutes();
    Boolean getClientInformedRiskFactors();
    Boolean getClientInformedPreventionMethods();
    Boolean getClientInformedPossibleResults();
    Boolean getInformedConsentGiven();
    Boolean getEverHadSexualIntercourse();
    Boolean getMoreThanOneSexPartner();
    Boolean getUnprotectedSexWithCasualPartner();
    Boolean getUnprotectedVaginalSex();
    Boolean getBloodTransfusionLast3Months();
    Boolean getSexUnderInfluence();
    Boolean getHistoryOfSTI();
    String getHadSexWithHivPositivePartnerInRiskGroup();
    Boolean getCurrentCough();
    Boolean getWeightLoss();
    Boolean getFever();
    Boolean getNightSweats();
    Boolean getComplaintsVaginalDischarge();
    Boolean getComplaintsLowerAbdominalPain();
    Boolean getComplaintsUrethralDischarge();
    Boolean getComplaintsScroralSwelling();
    Boolean getComplaintsGenitalSores();
    Boolean getComplaintsSwollenLymphNodes();
    Boolean getPartnerNewlyDiagnosed();
    Boolean getAdolescentHivPositive();
    Boolean getPartnerNotRegularlyOnDrugs();
    Boolean getPartnerRecentlyReturnedToTreatment();
    Boolean getClientReceivedTestResult();
    String getInitialHivTest();
    String getSuspectedAcuteInfection();
    String getConfirmatoryHivTest();
    String getSyphilisTestResult();
    String getRecencyTest();
    String getPreviouslyTestedThisYear();
    Boolean getAcceptedIndexTesting();
    Boolean getProvidedFpInfo();
    Boolean getClientPartnerUseFpMethods();
    Boolean getClientPartnerUseCondoms();
    Boolean getCorrectCondomUseDemonstrated();
    String getHivTestKitsProvided();
    Boolean getCondomsProvided();
    String getCategoryOfClients();
    Boolean getClientReferredToOtherServices();

    // From hts_icten table
    String getClientCategory();
    String getOfferedPns();
    String getAcceptedPns();

    // From hts_ictcon table
    String getNotificationMethod();
    String getFollowUpLocation();
    String getAttempts();
    String getKnownHivPositive();
    String getHivTestResult();
    LocalDate getDateTestedHiv();
    LocalDate getDateEnrolledArt();
    LocalDate getDateEnrolledOvc();
    String getOvcId();

}
