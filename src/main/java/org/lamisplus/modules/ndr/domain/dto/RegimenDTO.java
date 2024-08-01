package org.lamisplus.modules.ndr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegimenDTO implements Serializable {
	String   visitID;
	String   visitDate;
	String   dateRegimenStarted;
	String   prescribedRegimenCode;
	String   prescribedRegimenDuration;
	String   prescribedRegimenTypeCode;
	String   prescribedRegimenCodeDescTxt;
	String   differentiatedServiceDelivery;
	String   dispensing;
	String   multiMonthDispensing;
}
