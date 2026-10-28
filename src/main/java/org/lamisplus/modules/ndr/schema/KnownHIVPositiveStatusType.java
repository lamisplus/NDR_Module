package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "KnownHIVPositiveStatusType")
@XmlEnum
public enum KnownHIVPositiveStatusType {

    @XmlEnumValue("OnART")
    ON_ART("OnART"),
    @XmlEnumValue("NotOnART")
    NOT_ON_ART("NotOnART");
    private final String value;

    KnownHIVPositiveStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KnownHIVPositiveStatusType fromValue(String v) {
        for (KnownHIVPositiveStatusType c: KnownHIVPositiveStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
