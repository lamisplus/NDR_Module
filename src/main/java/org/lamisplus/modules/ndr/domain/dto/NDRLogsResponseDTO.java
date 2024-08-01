package org.lamisplus.modules.ndr.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class NDRLogsResponseDTO {
    Integer code;
    String message;
    List<NDRMessageLogDTO> messageLogs;
}
