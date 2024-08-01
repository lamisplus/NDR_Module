package org.lamisplus.modules.ndr.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.ndr.service.LocalDateDeserializer;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommencementDTO  implements Serializable {
	private String whoStage;
	private String functionStatus;
	private String regimen;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate artStartDate;
	private Double height;
	private Double bodyWeight;
	private Long  Cd4;
	private Long Cd4Percentage;
}
