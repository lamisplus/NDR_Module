
package org.lamisplus.modules.ndr.utility;

import lombok.Data;

import java.util.Date;


@Data
public class RegimenHistory {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Date dateVisit;

    private String regimenType;

    private String regimen;

    private String reasonSwitchedSubs;
}
