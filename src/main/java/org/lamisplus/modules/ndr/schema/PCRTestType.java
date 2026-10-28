package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PCRTestType", propOrder = {
    "ageAtTest",
    "dateSampleCollected",
    "result",
    "dateResultReceived"
})
public class PCRTestType {

    @XmlElement(name = "AgeAtTest")
    @XmlSchemaType(name = "string")
    protected PCRAgeAtTestType ageAtTest;
    @XmlElement(name = "DateSampleCollected")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateSampleCollected;
    @XmlElement(name = "Result")
    @XmlSchemaType(name = "string")
    protected PCRRapidTestResultType result;
    @XmlElement(name = "DateResultReceived")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateResultReceived;

    public PCRAgeAtTestType getAgeAtTest() {
        return ageAtTest;
    }

    public void setAgeAtTest(PCRAgeAtTestType value) {
        this.ageAtTest = value;
    }

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

    public XMLGregorianCalendar getDateResultReceived() {
        return dateResultReceived;
    }

    public void setDateResultReceived(XMLGregorianCalendar value) {
        this.dateResultReceived = value;
    }

}
