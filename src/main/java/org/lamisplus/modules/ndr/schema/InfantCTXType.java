package org.lamisplus.modules.ndr.schema;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantCTXType", propOrder = {
    "dateOfInitiation",
    "ageAtInitiationWks"
})
public class InfantCTXType {

    @XmlElement(name = "DateOfInitiation")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfInitiation;
    @XmlElement(name = "AgeAtInitiation_Wks")
    protected BigInteger ageAtInitiationWks;

    public XMLGregorianCalendar getDateOfInitiation() {
        return dateOfInitiation;
    }

    public void setDateOfInitiation(XMLGregorianCalendar value) {
        this.dateOfInitiation = value;
    }

    public BigInteger getAgeAtInitiationWks() {
        return ageAtInitiationWks;
    }

    public void setAgeAtInitiationWks(BigInteger value) {
        this.ageAtInitiationWks = value;
    }

}
