package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantHIVProphylaxisType", propOrder = {
    "dateOfInitiation",
    "ageAtInitiationWks",
    "typeOfProphylaxis",
    "otherSpecify",
    "dateOfCompletion"
})
public class InfantHIVProphylaxisType {

    @XmlElement(name = "DateOfInitiation")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfInitiation;
    @XmlElement(name = "AgeAtInitiation_wks")
    protected Integer ageAtInitiationWks;
    @XmlElement(name = "TypeOfProphylaxis")
    protected String typeOfProphylaxis;
    @XmlElement(name = "OtherSpecify")
    protected String otherSpecify;
    @XmlElement(name = "DateOfCompletion")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfCompletion;

    public XMLGregorianCalendar getDateOfInitiation() {
        return dateOfInitiation;
    }

    public void setDateOfInitiation(XMLGregorianCalendar value) {
        this.dateOfInitiation = value;
    }

    public Integer getAgeAtInitiationWks() {
        return ageAtInitiationWks;
    }

    public void setAgeAtInitiationWks(Integer value) {
        this.ageAtInitiationWks = value;
    }

    public String getTypeOfProphylaxis() {
        return typeOfProphylaxis;
    }

    public void setTypeOfProphylaxis(String value) {
        this.typeOfProphylaxis = value;
    }

    public String getOtherSpecify() {
        return otherSpecify;
    }

    public void setOtherSpecify(String value) {
        this.otherSpecify = value;
    }

    public XMLGregorianCalendar getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(XMLGregorianCalendar value) {
        this.dateOfCompletion = value;
    }

}
