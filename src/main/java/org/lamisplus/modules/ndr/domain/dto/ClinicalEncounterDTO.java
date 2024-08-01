package org.lamisplus.modules.ndr.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.ndr.schema.CodedSimpleType;
import org.lamisplus.modules.ndr.service.LocalDateDeserializer;
import org.lamisplus.modules.ndr.service.LocalDateTimeDeserializer;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalEncounterDTO  implements Serializable {
	private Long clinicId;
	private Long facilityId;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate visitDate;
	private Long cd4Percentage;
	private Long cd4;
	private Long functionalStatusId;
	private Long clinicalStageId;
	private String clinicalUuid;
	private long regimenId;
	private long regimenTypeId;
	private Long artStatusId;
	private Long whoStagingId;
	private String adherenceLevel;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate nextAppointment;
	private String familyPlaning;
	private String onFamilyPlaning;
	private String levelOfAdherence;
	private String tbStatus;
	private String tbPrevention;
	private String cd4Count;
	private String pregnancyStatus;
	private Double bodyWeight;
	private Double diastolic;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime captureDate;
	private Double height;
	private Double temperature;
	private Double pulse;
	private Double respiratoryRate;
	private String visitId;
	private Double systolic;
}
