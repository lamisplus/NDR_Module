package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDateTime;

public interface LabDTO {
	String getVisitId();
	String getLabTestName();
	String getResultReported();
	LocalDateTime getDateSampleCollected();
	LocalDateTime getDateAssayed();
	LocalDateTime getResultDate();


}
