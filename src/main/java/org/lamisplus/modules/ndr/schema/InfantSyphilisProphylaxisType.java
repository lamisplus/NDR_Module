package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantSyphilisProphylaxisType", propOrder = {
    "dateOfInitiation",
    "ageAtInitiationWks",
    "typeOfProphylaxis"
})
public class InfantSyphilisProphylaxisType {

    @XmlElement(name = "DateOfInitiation")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfInitiation;
    @XmlElement(name = "AgeAtInitiation_wks")
    protected Integer ageAtInitiationWks;
    @XmlElement(name = "TypeOfProphylaxis")
    protected String typeOfProphylaxis;

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

}
