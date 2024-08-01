package org.lamisplus.modules.ndr.domain.dto;

import java.time.LocalDate;

public interface ArtCommencementDTO {
	String getWhoStage();
	String getFunctionStatus();
	String getRegimen();
	LocalDate getArtStartDate();
	Double getHeight();
	Double getBodyWeight();
	Long getCd4();
	Long getCd4Percentage();
}
