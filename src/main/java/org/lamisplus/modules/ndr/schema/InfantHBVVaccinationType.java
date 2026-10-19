package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantHBVVaccinationType", propOrder = {
    "firstDoseDate",
    "firstDoseTiming",
    "secondDoseDate",
    "thirdDoseDate"
})
public class InfantHBVVaccinationType {

    @XmlElement(name = "FirstDoseDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar firstDoseDate;
    @XmlElement(name = "FirstDoseTiming")
    protected String firstDoseTiming;
    @XmlElement(name = "SecondDoseDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar secondDoseDate;
    @XmlElement(name = "ThirdDoseDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar thirdDoseDate;

    public XMLGregorianCalendar getFirstDoseDate() {
        return firstDoseDate;
    }

    public void setFirstDoseDate(XMLGregorianCalendar value) {
        this.firstDoseDate = value;
    }

    public String getFirstDoseTiming() {
        return firstDoseTiming;
    }

    public void setFirstDoseTiming(String value) {
        this.firstDoseTiming = value;
    }

    public XMLGregorianCalendar getSecondDoseDate() {
        return secondDoseDate;
    }

    public void setSecondDoseDate(XMLGregorianCalendar value) {
        this.secondDoseDate = value;
    }

    public XMLGregorianCalendar getThirdDoseDate() {
        return thirdDoseDate;
    }

    public void setThirdDoseDate(XMLGregorianCalendar value) {
        this.thirdDoseDate = value;
    }

}
