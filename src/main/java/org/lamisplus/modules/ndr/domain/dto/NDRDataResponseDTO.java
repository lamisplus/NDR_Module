package org.lamisplus.modules.ndr.domain.dto;

import lombok.Data;

@Data
public class NDRDataResponseDTO {
    Integer code;
    String batchNumber;
    String message;
    Boolean isAuthenticated;
}
