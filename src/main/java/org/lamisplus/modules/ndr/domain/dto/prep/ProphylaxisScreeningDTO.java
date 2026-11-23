package org.lamisplus.modules.ndr.domain.dto.prep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProphylaxisScreeningDTO {
    private Long id;
    private String uuid;
    private String uniqueClientId;
    private String hospitalNumber;
}

