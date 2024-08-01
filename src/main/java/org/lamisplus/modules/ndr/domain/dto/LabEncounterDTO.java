package org.lamisplus.modules.ndr.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.ndr.service.LocalDateTimeDeserializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabEncounterDTO  implements Serializable {
	private String visitId;
	private String labTestName;
	private String resultReported;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime dateSampleCollected;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime dateAssayed;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime resultDate;
}
