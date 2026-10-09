package org.lamisplus.modules.ndr.domain.dto;

import org.lamisplus.modules.ndr.domain.dto.prep.*;

public interface PrepDTO extends
        PrepScreeningAndEligibilityTypeProjection,
        PrepPepCardEnrollmentTypeProjection,
        PrepFollowUpVisitProjection,
        PepFollowUpVisitProjection,
        PrepDiscontinuationProjection
{
    String getClientCode();
}
