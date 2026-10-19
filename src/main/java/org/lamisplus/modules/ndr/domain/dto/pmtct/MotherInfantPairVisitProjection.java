package org.lamisplus.modules.ndr.domain.dto.pmtct;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MotherInfantPairVisitProjection {
    String getVisitId();
    LocalDate getVisitDate();
    Boolean getScheduled();
    Integer getGestationalAgeWeeks();
    BigDecimal getMotherWeightKg();
    BigDecimal getSfhLengthCm();
    String getMotherCurrentStatus();
    String getMotherCurrentArtStatus();
    String getRegimenCode();
    String getRegimenDescription();
    String getRegimenNdrCode();
    String getMotherCurrentHbvStatus();
    String getMotherCurrentHbvDrugName();
    String getMotherCurrentSyphilisStatus();
    String getSyphilisDrugAdministered();
    LocalDate getVlDateSampleCollected();
    LocalDate getVlDateResultReceived();
    BigDecimal getVlResultCopiesPerMl();
    String getInfantFeedingPractice();
    String getInfantOnCtx();
    String getReferredToTreatment();
    LocalDate getNextAppointmentDate();
}
