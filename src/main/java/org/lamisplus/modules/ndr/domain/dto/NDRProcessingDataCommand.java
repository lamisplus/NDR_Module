package org.lamisplus.modules.ndr.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class NDRProcessingDataCommand {
    private Long facilityId;
    private List<String> patientIds;
    private boolean initial;
    private List<NDRErrorDTO> ndrErrors;
    private String pushIdentifier;
    private AtomicInteger generatedCount;
    private AtomicInteger errorCount;
    private boolean isLastRecord;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}


