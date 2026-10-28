package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterHBVType", propOrder = {
    "knownPositive",
    "testResult",
    "treatmentReferral"
})
public class RegisterHBVType {

    @XmlElement(name = "KnownPositive")
    protected Boolean knownPositive;
    @XmlElement(name = "TestResult")
    protected String testResult;
    @XmlElement(name = "TreatmentReferral")
    protected String treatmentReferral;

    public Boolean isKnownPositive() {
        return knownPositive;
    }

    public void setKnownPositive(Boolean value) {
        this.knownPositive = value;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String value) {
        this.testResult = value;
    }

    public String getTreatmentReferral() {
        return treatmentReferral;
    }

    public void setTreatmentReferral(String value) {
        this.treatmentReferral = value;
    }

}
