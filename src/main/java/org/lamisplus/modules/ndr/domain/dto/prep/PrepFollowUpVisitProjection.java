package org.lamisplus.modules.ndr.domain.dto.prep;

import org.lamisplus.modules.ndr.schema.PrepFollowUpVisitType;

import java.util.List;

public interface PrepFollowUpVisitProjection {
    List<PrepFollowUpVisitType> getPrepFollowUpVisits();
}


