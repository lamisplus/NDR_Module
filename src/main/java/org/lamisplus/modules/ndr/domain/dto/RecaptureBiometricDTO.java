package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface RecaptureBiometricDTO {
	 String getTemplateType();
	 String getTemplateTypeHash();
	 Integer getQuality();
	 Integer getCount();
	 byte[] getTemplate();
	 LocalDate getEnrollmentDate();
}
