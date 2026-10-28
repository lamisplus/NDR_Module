package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CTXAgeCategoryType")
@XmlEnum
public enum CTXAgeCategoryType {

    @XmlEnumValue("LT2Months")
    LT_2_MONTHS("LT2Months"),
    @XmlEnumValue("GTe2Months")
    G_TE_2_MONTHS("GTe2Months");
    private final String value;

    CTXAgeCategoryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CTXAgeCategoryType fromValue(String v) {
        for (CTXAgeCategoryType c: CTXAgeCategoryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
