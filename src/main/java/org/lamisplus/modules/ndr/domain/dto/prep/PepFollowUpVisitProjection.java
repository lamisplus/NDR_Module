package org.lamisplus.modules.ndr.domain.dto.prep;

import org.lamisplus.modules.ndr.schema.PepFollowUpVisitType;

import java.util.List;

public interface PepFollowUpVisitProjection {
    List<PepFollowUpVisitType> getPepFollowUpVisits();
}


