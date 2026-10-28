package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfirmatoryPCRType", propOrder = {
    "dateSampleCollected",
    "result"
})
public class ConfirmatoryPCRType {

    @XmlElement(name = "DateSampleCollected")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateSampleCollected;
    @XmlElement(name = "Result")
    @XmlSchemaType(name = "string")
    protected PCRRapidTestResultType result;

    public XMLGregorianCalendar getDateSampleCollected() {
        return dateSampleCollected;
    }

    public void setDateSampleCollected(XMLGregorianCalendar value) {
        this.dateSampleCollected = value;
    }

    public PCRRapidTestResultType getResult() {
        return result;
    }

    public void setResult(PCRRapidTestResultType value) {
        this.result = value;
    }

}
