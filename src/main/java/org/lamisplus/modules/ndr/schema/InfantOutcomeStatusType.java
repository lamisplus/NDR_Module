package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "InfantOutcomeStatusType")
@XmlEnum
public enum InfantOutcomeStatusType {

    @XmlEnumValue("HIVPositiveLinkedtoART")
    HIV_POSITIVE_LINKEDTO_ART("HIVPositiveLinkedtoART"),
    @XmlEnumValue("HIVPositiveNotLinkedToART")
    HIV_POSITIVE_NOT_LINKED_TO_ART("HIVPositiveNotLinkedToART"),
    @XmlEnumValue("HIVNeg")
    HIV_NEG("HIVNeg"),
    @XmlEnumValue("StillBreastfeeding")
    STILL_BREASTFEEDING("StillBreastfeeding"),
    @XmlEnumValue("TransferredOut")
    TRANSFERRED_OUT("TransferredOut"),
    @XmlEnumValue("LostToFollowUp")
    LOST_TO_FOLLOW_UP("LostToFollowUp"),
    @XmlEnumValue("Dead")
    DEAD("Dead");
    private final String value;

    InfantOutcomeStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InfantOutcomeStatusType fromValue(String v) {
        for (InfantOutcomeStatusType c: InfantOutcomeStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
