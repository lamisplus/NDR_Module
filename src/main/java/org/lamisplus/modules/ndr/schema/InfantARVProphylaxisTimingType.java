package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "InfantARVProphylaxisTimingType")
@XmlEnum
public enum InfantARVProphylaxisTimingType {

    @XmlEnumValue("InFacilityWithin72Hrs")
    IN_FACILITY_WITHIN_72_HRS("InFacilityWithin72Hrs"),
    @XmlEnumValue("InFacilityAfter72Hrs")
    IN_FACILITY_AFTER_72_HRS("InFacilityAfter72Hrs"),
    @XmlEnumValue("DeliveredOutsideFacilityWithin72Hrs")
    DELIVERED_OUTSIDE_FACILITY_WITHIN_72_HRS("DeliveredOutsideFacilityWithin72Hrs"),
    @XmlEnumValue("DeliveredFacilityAfter72Hrs")
    DELIVERED_FACILITY_AFTER_72_HRS("DeliveredFacilityAfter72Hrs");
    private final String value;

    InfantARVProphylaxisTimingType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InfantARVProphylaxisTimingType fromValue(String v) {
        for (InfantARVProphylaxisTimingType c: InfantARVProphylaxisTimingType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
