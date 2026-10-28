package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterSyphilisType", propOrder = {
    "testResult",
    "treatmentReferral"
})
public class RegisterSyphilisType {

    @XmlElement(name = "TestResult")
    protected String testResult;
    @XmlElement(name = "TreatmentReferral")
    protected String treatmentReferral;

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
