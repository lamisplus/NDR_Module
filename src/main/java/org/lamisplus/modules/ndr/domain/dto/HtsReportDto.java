package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface HtsReportDto extends
        SyndromicSTIScreeningTypeDto,
        KnowledgeAssessmentTypeDto,
        HIVRiskAssessmentTypeDTO,
        ClinicalTBScreeningTypeDto,
        TestResultTypeDTO,
        RecencyTestingTypeDTO,
        PostTestCounsellingTypeDto{
    String getClientCode();
    String getVisitId();
    String getSetting();
    String getFirstTimeVisit();
    LocalDate getVisitDate();

//    String getSessionType();
    String getReferredFrom();

    String getMaritalStatus();

    Integer getNoOfOwnChildrenLessThan5Years();

    Integer getNoOfAllWives();

    String getIsIndexClient();

    String getIndexClientId();

//    String getReTestingForResultVerification();
    String getSyphilisTestResult();

    String getHbvTestResult();

    String getHcvTestResult();
    String getPartnerNotification();

}
