package org.lamisplus.modules.ndr.domain.dto.prep;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PrepPepCardEnrollmentTypeProjection {
    String getHospitalNumber();

    String getEnrollmentType();

    String getUniqueId();

    LocalDate getDateEnrolled();

    String getPartnerAncOrUniqueArtNumber();

    String getSex();

    Integer getAge();

    String getMaritalStatus();

    String getOccupation();

    String getEducationLevel();

    String getHivTestingPoint();

    LocalDate getDateOfLastHivTest();

    String getHivTestResult();

    LocalDate getDateReferred();

    String getPopulationType();

    String getOtherPopulationSpecify();

    LocalDate getDateInitialAdherenceCounseling();

    LocalDate getDateStarted();

    String getPrepTypeAtStart();

    String getPrepRegimen();

    BigDecimal getWeight();

    BigDecimal getHeight();

    BigDecimal getBMI();

    Boolean getIsPregnant();

    Boolean getIsBreastfeeding();

    String getHistoryOfDrugAllergies();

    String getHistoryOfDrugDrugInteractions();

    String getUrinalysisResult();

    String getLiverFunctionTestResult();

    String getReferredAtInitiation();

    LocalDate getDateReferredAtInitiation();

    String getServiceReferredFor();

    String getPepCompletion();

    List<PepFollowUpEntryProjection> getPepFollowUpEntry();

}