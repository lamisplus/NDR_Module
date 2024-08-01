package org.lamisplus.modules.ndr.mapper;

import org.lamisplus.modules.ndr.schema.*;

public class NDRObjectFactory {

	
	public static HIVTestingReportType createHIVTestingReportType(){
		 return new HIVTestingReportType();
    }

	public static PreTestInformationType createPreTestInformationType(){
		return new  PreTestInformationType();
	}

	public static KnowledgeAssessmentType createKnowledgeAssessmentType(){
		return new  KnowledgeAssessmentType();
	}

	public static HIVRiskAssessmentType createHIVRiskAssessmentType(){
		return new  HIVRiskAssessmentType();
	}
	public static ClinicalTBScreeningType createClinicalTBScreeningType(){
		return new ClinicalTBScreeningType();
	}
	public static SyndromicSTIScreeningType createSyndromicSTIScreeningType(){
		return new  SyndromicSTIScreeningType();
	}


}
