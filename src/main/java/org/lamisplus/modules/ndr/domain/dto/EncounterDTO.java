package org.lamisplus.modules.ndr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncounterDTO implements Serializable {
	Integer  weight;
	String  visitID;
	String tbStatus;
	String visitDate;
	Integer childHeight;
	String bloodPressure;
	String nextAppointmentDate;
	Integer durationOnArt;
	Integer height;
	BigDecimal bmimuac;
	String pregnancyBFStatus;
	String edDandPMTCTLink;
	String patientFamilyPlanningCode;
	String patientFamilyPlanningMethodCode;
	String functionalStatus;
	String disclosureStatus;
	String whoClinicalStage;
	String cryptococcalStatus;
	String cervicalCancerScreeningStatus;
	String cervicalTreatmentProvided;
	String hepatitisStatus;
	String otherOIOtherProblems;
	String notedSideEffects;
	String dsdStatusCode;
	String dateDevolved;
	String arvDrugAdherence;
	String whyPoorFairARVDrugAdherence;
	String cotrimoxazoleCode;
	String cotrimoxazoleAdherence;
	String whyPoorFairCotrimoxazoleDrugAdherence;
	String inhDose;
	String inhAdherence;
	String whyPoorFairINHDrugAdherence;
    Integer cd4;
	String cd4TestDate;
	String reasonForRegimenSwitchSubs;
	Boolean prescribedRegimenInitialIndicator;
	Boolean prescribedRegimenCurrentIndicator;
	String typeOfPreviousExposureCode;
	Boolean poorAdherenceIndicator;
	String reasonForPoorAdherence;
	String reasonRegimenEndedCode;
	Boolean substitutionIndicator;
	Boolean switchIndicator;
	Boolean stoppedRegimen;
	String dateStoppedRegimen;
	String reasonForStoppedRegimen;
	String methodofTBDiagnosis;
	String tptMedication;
	String tptDose;
	String tptAdherenceCode;
	String otherDrugsPrescribed;
	Integer vlResult;
	String vlIndication;
	String eacCode;
	BigDecimal randomBloodSugar;
	String otherTestsDone;
	String consultHospitaliseRefer;
	String healthInsuranceCode;
}
