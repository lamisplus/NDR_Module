package org.lamisplus.modules.ndr.domain.dto;

public interface HIVRiskAssessmentTypeDTO {
	Boolean getEverHadSexualIntercourse();
	Boolean getBloodTransfussionInLast3Months();
	Boolean getUnprotectedSexWithCasualPartnerinLast3Months();
	Boolean getUnprotectedSexWithRegularPartnerInLast3Months();
	Boolean getMoreThan1SexPartnerDuringLast3Months();
	Boolean getStiInLast3Months();
}
