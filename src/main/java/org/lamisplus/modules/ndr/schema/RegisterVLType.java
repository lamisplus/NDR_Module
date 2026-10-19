package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterVLType", propOrder = {
    "vlLessThan32Weeks",
    "vl32To36Weeks",
    "vlBreastfeedingPeriod"
})
public class RegisterVLType {

    @XmlElement(name = "VLLessThan32Weeks")
    protected VLMeasurementType vlLessThan32Weeks;
    @XmlElement(name = "VL32To36Weeks")
    protected VLMeasurementType vl32To36Weeks;
    @XmlElement(name = "VLBreastfeedingPeriod")
    protected VLMeasurementType vlBreastfeedingPeriod;

    public VLMeasurementType getVLLessThan32Weeks() {
        return vlLessThan32Weeks;
    }

    public void setVLLessThan32Weeks(VLMeasurementType value) {
        this.vlLessThan32Weeks = value;
    }

    public VLMeasurementType getVL32To36Weeks() {
        return vl32To36Weeks;
    }

    public void setVL32To36Weeks(VLMeasurementType value) {
        this.vl32To36Weeks = value;
    }

    public VLMeasurementType getVLBreastfeedingPeriod() {
        return vlBreastfeedingPeriod;
    }

    public void setVLBreastfeedingPeriod(VLMeasurementType value) {
        this.vlBreastfeedingPeriod = value;
    }

}
