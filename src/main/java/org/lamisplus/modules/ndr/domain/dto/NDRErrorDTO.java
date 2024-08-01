package org.lamisplus.modules.ndr.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NDRErrorDTO {
	private  String patientUuid ;
	private  String hospitalNumber ;
	private  String  errorMessage ;
}
