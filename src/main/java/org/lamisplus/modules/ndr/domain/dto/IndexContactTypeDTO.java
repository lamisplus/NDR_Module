package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface IndexContactTypeDTO {
    String getSerialNo();

    String getRelationshipToIndex();
    String getSex();
    String getAgeGroup();
    String getNotificationMethod();
    String getFollowUpAppointmentLocation();
    String getContactAttempts();
    String getKnownHIVPositive();
    String getHivTestResult();
    LocalDate getDateTested();
    LocalDate getDateEnrolledOnART();
    LocalDate getDateEnrolledInOVC();
    String getOvcid();

}
