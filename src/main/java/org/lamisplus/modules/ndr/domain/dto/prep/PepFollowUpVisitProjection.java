package org.lamisplus.modules.ndr.domain.dto.prep;

import java.time.LocalDate;

public interface PepFollowUpVisitProjection {
    String getVisitID();
    LocalDate getVisitDate();
    String getModeOfExposure();
    String getDurationBeforePepProvided();
    String getBloodPressure();
    String getHivStatusAtExposure();
    String getNotedSideEffects();
    String getSyndromicSTIScreening();
    String getRiskReductionServices();
    String getAdherence();
    String getPepRegimen();
    LocalDate getDatePepGivenStart();
    LocalDate getDatePepGivenStop();
    String getFollowUpHivTestResult1St6Weeks();
    String getFollowUpHivTestResult2Nd3Months();
    String getFollowUpHivTestResult3Rd6Months();
    String getReferIfPositive();
    LocalDate getNextAppointmentDate();

}
