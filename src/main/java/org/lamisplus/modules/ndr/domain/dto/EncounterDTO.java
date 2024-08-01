package org.lamisplus.modules.ndr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncounterDTO implements Serializable {
	Integer weight;
	String  visitID;
	String tbStatus;
	String visitDate;
	Integer childHeight;
	String bloodPressure;
	String nextAppointmentDate;
}
