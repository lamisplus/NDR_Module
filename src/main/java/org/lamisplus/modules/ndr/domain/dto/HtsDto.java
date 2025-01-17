package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface HtsDto {
	String getClientCode();
	String getVisitId();
	String getSetting();
	LocalDate getVisitDate();
	String getTestResult();
	String getRecency();
	
}
