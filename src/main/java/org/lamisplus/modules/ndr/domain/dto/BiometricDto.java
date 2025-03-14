package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;


public interface BiometricDto {
    String getTemplateType();

    byte[] getTemplate();

    LocalDate getEnrollmentDate();
    
    Integer getQuality();
}
