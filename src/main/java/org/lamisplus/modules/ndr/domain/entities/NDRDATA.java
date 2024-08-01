package org.lamisplus.modules.ndr.domain.entities;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NDRDATA {
	@Id
	@NotNull
	private String patientId;
	
	@Type(type = "jsonb")
	@Column(name = "demorgraphic", columnDefinition = "jsonb")
	private JsonNode demorgraphic;
	
	@Type(type = "jsonb")
	@Column(name = "commencement", columnDefinition = "jsonb")
	private JsonNode commencement;
	
	@Type(type = "jsonb")
	@Column(name = "careCards", columnDefinition = "jsonb")
	private JsonNode careCards;
	
	@Type(type = "jsonb")
	@Column(name = "labs", columnDefinition = "jsonb")
	private JsonNode labs;
	
	@Type(type = "jsonb")
	@Column(name = "pharmacies", columnDefinition = "jsonb")
	private JsonNode pharmacies;
	
	private LocalDate dateModified;
}


	
	
	
	

