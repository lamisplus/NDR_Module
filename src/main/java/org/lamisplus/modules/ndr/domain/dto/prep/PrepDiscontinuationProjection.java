package org.lamisplus.modules.ndr.domain.dto.prep;

import org.lamisplus.modules.ndr.schema.PrepInterruptionType;

import java.time.LocalDate;
import java.util.List;
public interface PrepDiscontinuationProjection {
//    List<PrepInterruptionType> getInterruptions();
    // ----- PrEP Interruptions (repeatable — card has up to 3 rows) -----

    /**
     * PrEP interruptions. Repeatable; expected up to 3 rows per the card
     * layout, but the schema allows an unbounded list.
     */
//    List<? extends PrepInterruptionProjection> getInterruptions();

    // ----- Referral Out -----

    LocalDate getDateClientReferredOut();

    String getFacilityReferredTo();

    // ----- Mortality -----

    LocalDate getDateClientDied();

    String getSourceOfDeathInformation();

    String getCauseOfDeath();

    // ----- From Register -----

    /**
     * Discontinuation reason code per tool specification.
     * 1=Dead, 2=Stopped, 3=Seroconverted, 4=Transfer out,
     * 5=Loss to follow up, 6=Adverse drug reaction,
     * 7=No longer at substantial risk
     */
    String getDiscontinuedPrepPepReason();

    LocalDate getDiscontinuedPrepPepDate();

    /** Date client was linked to ART following positive HIV test. */
    LocalDate getArtLinkDate();

}


