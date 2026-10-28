package org.lamisplus.modules.ndr.domain.dto.prep;

import java.time.LocalDate;

public interface PepFollowUpEntryProjection {
    LocalDate getFollowUpVisitDate();
    String getHivResult();
    String getEarlyHivDetectionViralLoadResult();

}
