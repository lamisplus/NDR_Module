package org.lamisplus.modules.ndr.domain.dto.prep;

import java.time.LocalDate;

public interface PrepInterruptionProjection {
    String getInterruptionReason();
    LocalDate getInterruptionDate();
    String getWhyCode();
    LocalDate getDateOfRestart();
}
