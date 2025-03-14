package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ARTClinicalInfo {
	Long getClinicId();
	
	Long getFacilityId();
	
	LocalDate getVisitDate();
	
	Long getCd4();
	
	Long getFunctionalStatusId();
	
	Long getClinicalStageId();
	
	String getclinicalUuid();
	
	Long getWhoStagingId();
	
	LocalDate getNextAppointment();
	
	Double getBodyWeight();
		
	Double getDiastolic();
		
	Double getHeight();
		
	Double getTemperature();
		
	Double getPulse();
	
	String getVisitId();
		
	Double getSystolic();
	
}