package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PairCardSyphilisType", propOrder = {
    "testResult",
    "treatmentGiven",
    "drugName"
})
public class PairCardSyphilisType {

    @XmlElement(name = "TestResult")
    protected String testResult;
    @XmlElement(name = "TreatmentGiven")
    protected String treatmentGiven;
    @XmlElement(name = "DrugName")
    protected String drugName;

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String value) {
        this.testResult = value;
    }

    public String getTreatmentGiven() {
        return treatmentGiven;
    }

    public void setTreatmentGiven(String value) {
        this.treatmentGiven = value;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String value) {
        this.drugName = value;
    }

}
