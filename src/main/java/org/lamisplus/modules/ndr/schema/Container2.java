package org.lamisplus.modules.ndr.schema;


import lombok.Data;

import java.util.List;
@Data
public class Container2 {
    protected MessageHeaderType messageHeader;
    protected List<IndividualReportType> individualReport;
    protected String validation;
}
