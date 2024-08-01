package org.lamisplus.modules.ndr.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public  class NDRStatus {
    public Long id;
    public String identifier;
    public String file;
}
