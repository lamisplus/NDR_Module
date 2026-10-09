package org.lamisplus.modules.ndr.domain.dto.prep;

import java.time.LocalDate;
import java.util.List;

public interface PrepDiscontinuationProjection {
    List<PrepInterruptionProjection> getInterruption();
    LocalDate getDateClientReferredOut();
    String getFacilityReferredTo();
    LocalDate getDateClientDied();
    String getSourceOfDeathInformation();
    String getCauseOfDeath();
    String getDiscontinuedPrepPepReason();
    LocalDate getDiscontinuedPrepPepDate();
    LocalDate getArtLinkDate();

}
