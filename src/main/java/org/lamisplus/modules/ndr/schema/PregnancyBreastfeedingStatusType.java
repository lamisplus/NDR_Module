package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PregnancyBreastfeedingStatusType")
@XmlEnum
public enum PregnancyBreastfeedingStatusType {

    P,
    BF;

    public String value() {
        return name();
    }

    public static PregnancyBreastfeedingStatusType fromValue(String v) {
        return valueOf(v);
    }

}
