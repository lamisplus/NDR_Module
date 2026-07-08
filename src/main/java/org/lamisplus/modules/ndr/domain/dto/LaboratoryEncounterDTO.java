package org.lamisplus.modules.ndr.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class LaboratoryEncounterDTO implements Serializable {
		private String visitId;
		private String visitDate;
		private String collectionDate;
		private String orderedTestDate;
		private String resultedTestDate;
		private String laboratoryTestTypeCode;
		private String laboratoryTestIdentifier;
		private String laboratoryResultedTestCode;
		private String laboratoryResultAnswerNumeric;
		private String laboratoryResultedTestCodeDescTxt;
//		new added
	    private String laboratoryOrderedTest;
	    private String laboratoryResult;
		private String otherLaboratoryInformation;
	    private String specimenCollectionDate;
		private String specimenTypeCode;
		private String sampleReceivedAtLabDate;
		private String sampleLoggedRemotely;
	    private String labRegistrationNumber;
	    private String pcrpocLabName;
	    private String pcrpocLabSampleNumber;
	    private String viralLoadIndicationCode;
	    private String viralLoadResult;
	    private String viralLoadResultDate;
	    private String eidIndicationCode;
	    private String eidEntryPointCode;
		private String eidResultCode;
		private String eidAgeCode;
		private String cd4CellCount;
		private String cd4Percentage;
		private String cd4LFAResultCode;
		private String randomGlucose;
		private String hBsAGResultCode;
		private String hcvAntibodyResultCode;
		private String hbvViralLoad;
		private String hcvViralLoad;
		private String vdrlSyphilisResultCode;
		private String serologyForCrAgResultCode;
		private String csfForCrAgResultCode;
		private String tblflamResultCode;
		private String hpvResultCode;
		private String cytologyVIAPapSmearResult;
		private String urinalysis;
		private String artStartDate;
		private String drugRegimenLineCode;
		private String arvProphylaxisReceivedCode;
}
