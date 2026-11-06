package org.lamisplus.modules.ndr.domain.dto.prep;

import org.lamisplus.modules.ndr.schema.PrepInterruptionType;

import java.time.LocalDate;
import java.util.List;
public interface PrepDiscontinuationProjection {
//    List<PrepInterruptionType> getInterruptions();
    LocalDate getDateClientReferredOut();

    String getFacilityReferredTo();

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


