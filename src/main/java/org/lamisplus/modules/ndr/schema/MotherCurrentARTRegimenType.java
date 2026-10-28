package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MotherCurrentARTRegimenType", propOrder = {
    "regimenCode",
    "regimenDescription",
    "regimenNDRCode"
})
public class MotherCurrentARTRegimenType {

    @XmlElement(name = "RegimenCode")
    protected String regimenCode;
    @XmlElement(name = "RegimenDescription")
    protected String regimenDescription;
    @XmlElement(name = "RegimenNDRCode")
    protected String regimenNDRCode;

    public String getRegimenCode() {
        return regimenCode;
    }

    public void setRegimenCode(String value) {
        this.regimenCode = value;
    }

    public String getRegimenDescription() {
        return regimenDescription;
    }

    public void setRegimenDescription(String value) {
        this.regimenDescription = value;
    }

    public String getRegimenNDRCode() {
        return regimenNDRCode;
    }

    public void setRegimenNDRCode(String value) {
        this.regimenNDRCode = value;
    }

}
