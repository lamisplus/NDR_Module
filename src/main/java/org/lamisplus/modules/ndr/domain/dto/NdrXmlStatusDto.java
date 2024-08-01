package org.lamisplus.modules.ndr.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NdrXmlStatusDto implements Serializable {
    private  Integer id;
    private  Integer files;
    private  String fileName;
    private  String facility;
    private  LocalDateTime lastModified;
    private String pushIdentifier;
    private Long percentagePushed;
    private Boolean completelyPushed;
    private boolean error;
}
