package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PCRRapidTestResultType")
@XmlEnum
public enum PCRRapidTestResultType {

    @XmlEnumValue("Positive")
    POSITIVE("Positive"),
    @XmlEnumValue("Negative")
    NEGATIVE("Negative");
    private final String value;

    PCRRapidTestResultType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PCRRapidTestResultType fromValue(String v) {
        for (PCRRapidTestResultType c: PCRRapidTestResultType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
