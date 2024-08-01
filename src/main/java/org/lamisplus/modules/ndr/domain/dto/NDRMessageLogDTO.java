package org.lamisplus.modules.ndr.domain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NDRMessageLogDTO {
    Integer id;
    String message;
    String patientIdentifier;
    String FileName;
    LocalDate dateCreated;
}
