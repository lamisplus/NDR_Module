package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterTBScreeningType", propOrder = {
    "status",
    "treatmentReferral"
})
public class RegisterTBScreeningType {

    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "TreatmentReferral")
    protected String treatmentReferral;

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getTreatmentReferral() {
        return treatmentReferral;
    }

    public void setTreatmentReferral(String value) {
        this.treatmentReferral = value;
    }

}
