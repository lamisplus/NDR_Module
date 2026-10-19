package org.lamisplus.modules.ndr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.ndr.domain.dto.pmtct.DeliveryChildrenDetailsProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.InfantCohortRegistrationProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.MotherInfantPairVisitProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTRegisterProjection;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PmtctDTO {
    private List<PMTCTRegisterProjection> pmtctRegisters = Collections.emptyList();
    private List<MotherInfantPairVisitProjection> motherInfantPairVisits = Collections.emptyList();
    private List<DeliveryChildrenDetailsProjection> deliveryChildrenDetails = Collections.emptyList();
    private List<InfantCohortRegistrationProjection> infantCohortRegistrations = Collections.emptyList();

    public boolean isEmpty() {
        return (pmtctRegisters == null || pmtctRegisters.isEmpty())
                && (motherInfantPairVisits == null || motherInfantPairVisits.isEmpty())
                && (deliveryChildrenDetails == null || deliveryChildrenDetails.isEmpty())
                && (infantCohortRegistrations == null || infantCohortRegistrations.isEmpty());
    }
}
