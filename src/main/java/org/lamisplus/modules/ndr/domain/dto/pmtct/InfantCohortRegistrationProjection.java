package org.lamisplus.modules.ndr.domain.dto.pmtct;

import java.time.LocalDate;

public interface InfantCohortRegistrationProjection {
    String getInfantUuid();
    LocalDate getDateOfBirth();
    LocalDate getDateOfFirstVisit();
    Integer getAgeAtFirstVisit();
    String getChildHospitalRegNo();
    String getInfantSex();
    String getChildEntryPoint();
    String getMotherHospitalNumber();
    String getMotherAncNumber();
    String getMotherStatus();
    String getMotherArtInitiationTiming();
    String getMotherRegimenCode();
    String getMotherRegimenDescription();
    String getMotherRegimenNdrCode();
    String getMotherSyphilisTreatmentStartDate();
    String getMotherHepatitisBTreatmentStartDate();
    Boolean getHivExposedInfantGivenHbWithin24hrs();
    Boolean getNonHbvExposedInfantGivenHbWithin24hrs();
    String getHbvVaccinationsJson();
    String getVisitHbvVaccinationDataJson();
    String getInfantArvDataJson();
    String getRelArvType();
    String getRelArvTime();
    String getRelArvDeliveryPoint();
    String getRelTimingWithin72();
    String getRelTimingAfter72();
    String getRelAgeAtCtx();
    String getInfantPcrDataJson();
    String getRelPcrListJson();
    String getRapidTestDataJson();
    String getRelRapidTestJson();
    String getOutcomeAt18Months();
    LocalDate getDateLinkedToArtClinic();
    String getArtEnrollmentNumber();
}
