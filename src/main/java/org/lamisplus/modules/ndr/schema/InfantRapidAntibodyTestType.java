package org.lamisplus.modules.ndr.schema;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantRapidAntibodyTestType", propOrder = {
    "ageAtTestMonths",
    "dateOfTest",
    "result"
})
public class InfantRapidAntibodyTestType {

    @XmlElement(name = "AgeAtTest_Months")
    protected BigInteger ageAtTestMonths;
    @XmlElement(name = "DateOfTest")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfTest;
    @XmlElement(name = "Result")
    @XmlSchemaType(name = "string")
    protected PCRRapidTestResultType result;

    public BigInteger getAgeAtTestMonths() {
        return ageAtTestMonths;
    }

    public void setAgeAtTestMonths(BigInteger value) {
        this.ageAtTestMonths = value;
    }

    public XMLGregorianCalendar getDateOfTest() {
        return dateOfTest;
    }

    public void setDateOfTest(XMLGregorianCalendar value) {
        this.dateOfTest = value;
    }

    public PCRRapidTestResultType getResult() {
        return result;
    }

    public void setResult(PCRRapidTestResultType value) {
        this.result = value;
    }

}
