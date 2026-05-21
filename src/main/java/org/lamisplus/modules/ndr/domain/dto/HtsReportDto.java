package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface HtsReportDto extends
        SyndromicSTIScreeningTypeDto,
        KnowledgeAssessmentTypeDto,
        HIVRiskAssessmentTypeDTO,
        ClinicalTBScreeningTypeDto,
        SexPartnerRiskAssessmentTypeDTO,
        TestResultTypeDTO,
        RecencyTestingTypeDTO,
        IndexContactTestingTypeDTO,
        IndexContactTypeDTO,
        PostTestCounsellingTypeDto{
    String getClientCode();
    String getVisitId();
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
    String getClientIsPregnant();
    String getBreastfeeding();
    String getDurationOfBreastfeeding();

//    String getFirstTimeVisit();

//    String getReferredFrom();

//    String getIsIndexClient();

    String getSyphilisTestResult();
    String getRecencyTestingResult();
    String getComments();
    String getCompletedBy();
    String getDateCompleted();

    String getOfferedPns();
    String getAcceptedPns();

}
