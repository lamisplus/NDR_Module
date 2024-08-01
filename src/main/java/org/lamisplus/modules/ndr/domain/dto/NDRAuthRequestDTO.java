package org.lamisplus.modules.ndr.domain.dto;

import lombok.Data;

@Data
public class NDRAuthRequestDTO {
    String email;
    String password;
    String baseUrl;
}
