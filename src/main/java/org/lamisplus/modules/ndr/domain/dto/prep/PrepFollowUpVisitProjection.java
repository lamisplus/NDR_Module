package org.lamisplus.modules.ndr.domain.dto.prep;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PrepFollowUpVisitProjection {
    String getVisitID();
    LocalDate getVisitDate();
    String getVisitType();
    Integer getDurationOnPrepMonths();
    String getPregnancyStatus();
    BigDecimal getWeight();
    String getBloodPressure();
    String getHtsResult();
    String getNotedSideEffects();
    String getSyndromicSTIScreening();
    String getRiskReductionServices();
    String getAdherence();
    String getReasonForPoorFairAdherence();
    String getPrepType();
    String getPrepRegimen();
    Integer getMonthsOfRefill();
    String getOtherDrugsPrescribed();
    LocalDate getDateOfUrinalysis();
    String getUrinalysisResult();
    LocalDate getDateOfHepatitisTest();
    String getHepatitisTestResult();
    LocalDate getDateOfSyphilisTest();
    String getSyphilisTestResult();
    LocalDate getDateOfLiverFunctionTest();
    String getLiverFunctionTestResult();
    LocalDate getDateOfOtherTests();
    String getOtherTestsResult();
    String getSuspectedAcuteInfection();
    String getEarlyHivDetectionViralLoadResult();
    LocalDate getNextAppointmentDate();

}
