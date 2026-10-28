package org.lamisplus.modules.ndr.domain.dto;

public interface SexPartnerRiskAssessmentTypeDTO {
    Boolean getPartnerNewlyDiagnosedOnARTLessThan3To6Months();
    Boolean getPartnerPregnantReceivingARVForPMTCT();
    Boolean getPartnerAdolescent10To19KnownHIVInfected();
    Boolean getPartnerKnownPositiveNotRegularlyOnDrugs();
    Boolean getPartnerKnownPositiveRecentlyReturnedAfterLTFU();
}
