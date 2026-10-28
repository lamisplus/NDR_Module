package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantHBVProphylaxisType", propOrder = {
    "birthDoseWithin24Hrs",
    "birthDoseAfter24Hrs",
    "secondDoseGiven",
    "thirdDoseGiven"
})
public class InfantHBVProphylaxisType {

    @XmlElement(name = "BirthDoseWithin24Hrs")
    protected Boolean birthDoseWithin24Hrs;
    @XmlElement(name = "BirthDoseAfter24Hrs")
    protected Boolean birthDoseAfter24Hrs;
    @XmlElement(name = "SecondDoseGiven")
    protected Boolean secondDoseGiven;
    @XmlElement(name = "ThirdDoseGiven")
    protected Boolean thirdDoseGiven;

    public Boolean isBirthDoseWithin24Hrs() {
        return birthDoseWithin24Hrs;
    }

    public void setBirthDoseWithin24Hrs(Boolean value) {
        this.birthDoseWithin24Hrs = value;
    }

    public Boolean isBirthDoseAfter24Hrs() {
        return birthDoseAfter24Hrs;
    }

    public void setBirthDoseAfter24Hrs(Boolean value) {
        this.birthDoseAfter24Hrs = value;
    }

    public Boolean isSecondDoseGiven() {
        return secondDoseGiven;
    }

    public void setSecondDoseGiven(Boolean value) {
        this.secondDoseGiven = value;
    }

    public Boolean isThirdDoseGiven() {
        return thirdDoseGiven;
    }

    public void setThirdDoseGiven(Boolean value) {
        this.thirdDoseGiven = value;
    }

}
