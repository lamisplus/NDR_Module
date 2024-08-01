package org.lamisplus.modules.ndr.domain.dto;

import lombok.Data;

@Data
public class NDRAuthResponseDTO {
    String token;
    Boolean isAuthenticated;
    Integer code;
}
