package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "InfantARVProphylaxisType")
@XmlEnum
public enum InfantARVProphylaxisType {

    NVP("NVP"),
    ZDV("ZDV"),
    @XmlEnumValue("NVP_and_ZDV")
    NVP_AND_ZDV("NVP_and_ZDV"),
    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    InfantARVProphylaxisType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InfantARVProphylaxisType fromValue(String v) {
        for (InfantARVProphylaxisType c: InfantARVProphylaxisType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
