package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PeriodOfHIVDiagnosisType")
@XmlEnum
public enum PeriodOfHIVDiagnosisType {

    @XmlEnumValue("PreviouslyInCareOrART")
    PREVIOUSLY_IN_CARE_OR_ART("PreviouslyInCareOrART"),
    ANC("ANC"),
    @XmlEnumValue("Labour")
    LABOUR("Labour"),
    @XmlEnumValue("LessThan72HrsPostpartum")
    LESS_THAN_72_HRS_POSTPARTUM("LessThan72HrsPostpartum"),
    @XmlEnumValue("BreastfeedingPeriod")
    BREASTFEEDING_PERIOD("BreastfeedingPeriod"),
    @XmlEnumValue("HIVNegative")
    HIV_NEGATIVE("HIVNegative");
    private final String value;

    PeriodOfHIVDiagnosisType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PeriodOfHIVDiagnosisType fromValue(String v) {
        for (PeriodOfHIVDiagnosisType c: PeriodOfHIVDiagnosisType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
