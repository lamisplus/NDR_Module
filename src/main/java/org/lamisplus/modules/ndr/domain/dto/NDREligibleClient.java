package org.lamisplus.modules.ndr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NDREligibleClient {
	String  personUuid;
	String  hospitalNumber;
	String  Name;
}
