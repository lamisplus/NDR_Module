package org.lamisplus.modules.ndr.domain.dto.pmtct;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PMTCTRegisterProjection {
    String getVisitId();
    LocalDate getVisitDate();
    String getHospitalNo();
    String getAncNumber();
    String getPregnancyBreastfeedingStatus();
    Integer getGestationalAgeAtBooking();
    String getPreviouslyKnownHivStatus();
    String getCurrentlyOnArt();
    String getHivEarlyAcute();
    String getHivEarlyViralLoad();
    String getHivTrAnc();
    String getHivTrLd();
    String getHivTrBf();
    String getHivRtAnc();
    String getHivRtLd();
    String getHivRtBf();
    LocalDate getDateOfInitiation();
    String getTimingOfArtInitiation();
    String getSyphilisTestResult();
    String getSyphilisTreated();
    String getSyphilisReferred();
    String getHbvKnownPositive();
    String getHbvTestResult();
    String getHbvTreated();
    String getHbvReferred();
    String getInitiatedOnProphylaxis();
    String getTbScreeningStatus();
    String getTbTreatmentReferral();
    LocalDate getVlLessThan32WeeksDate();
    BigDecimal getVlLessThan32WeeksResult();
    LocalDate getVl32To36WeeksDate();
    BigDecimal getVl32To36WeeksResult();
    LocalDate getVlBreastfeedingDate();
    BigDecimal getVlBreastfeedingResult();
    String getPartnerAgreedToNotification();
    String getPartnerTestedHiv();
    String getPartnerTestedSyphilis();
    String getPartnerTestedHepatitisB();
    String getPartnerReferral();
}
