package org.lamisplus.modules.ndr.domain.dto;

import org.lamisplus.modules.ndr.schema.HIVTestResultType;
import org.lamisplus.modules.ndr.schema.IndexNotificationServicesType;
import org.lamisplus.modules.ndr.schema.PostTestCounsellingType;
import org.lamisplus.modules.ndr.schema.PreTestInformationType;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDate;

public interface HtsDto {
	String getClientCode();
	String getVisitId();
	String getSetting();
	String getFirstTimeVisit();
	LocalDate getVisitDate();
	String getHivRiskAssessment();
	String getKnowledgeAssessment();
	String getPostTestCounselling();
	String getTestResult();
	String getRecency();
	String getSyndromicStiScreening();
	
}
