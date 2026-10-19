package org.lamisplus.modules.ndr.domain.dto.prep;

import org.lamisplus.modules.ndr.schema.PrepInterruptionType;

import java.time.LocalDate;
import java.util.List;
public interface PrepDiscontinuationProjection {
    List<PrepInterruptionType> getInterruptions();
    LocalDate getDateClientReferredOut();
    String    getFacilityReferredTo();
    LocalDate getDateClientDied();
    String    getSourceOfDeathInformation();
    String    getCauseOfDeath();
    String    getDiscontinuedPrepPepReason();   // "1".."7"
    LocalDate getDiscontinuedPrepPepDate();
    LocalDate getArtLinkDate();
}


