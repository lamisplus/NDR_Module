package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface MortalityDTO {

    String getVisitID();

    LocalDate getVisitDate();

    String getReasonForTracking();

    String getOtherTrackingReason();

    String getPartnerFullName();

    String getAddressofTreatmentSupporter();

    String getContactPhoneNumber();

    LocalDate getDateofLastActualContact();

    LocalDate getDateofMissedScheduledAppointment();

    LocalDate getDatePatientContacted();

    String getNameofPersonWhoAttemptedContact();

    String getModeofCommunication();

    String getPersonContacted();

    String getReasonforDefaulting();

    String getOtherReasonforDefaulting();

    String getReasonforLosttoFollowup();

    String getPreviousARVExposure();

    LocalDate getDateofTermination();

    String getReasonforTermination();
    String getTransferredOutTo();

    String getDeath();

    String getVaCauseofDeath();

    String getOtherCauseofDeath();

    String getCauseOfDeath();

    String getDiscontinuedCare();

    String getDiscontinueCareOtherSpecify();

    LocalDate getDateReturnedtoCare();

    String getReffferedFor();

    String getReffferedForOther();

    String getNameofContactTracer();
}
