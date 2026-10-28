package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantARTEnrollmentType", propOrder = {
    "dateLinkedToARTClinic",
    "artEnrollmentNumber"
})
public class InfantARTEnrollmentType {

    @XmlElement(name = "DateLinkedToARTClinic")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateLinkedToARTClinic;
    @XmlElement(name = "ARTEnrollmentNumber")
    protected String artEnrollmentNumber;

    public XMLGregorianCalendar getDateLinkedToARTClinic() {
        return dateLinkedToARTClinic;
    }

    public void setDateLinkedToARTClinic(XMLGregorianCalendar value) {
        this.dateLinkedToARTClinic = value;
    }

    public String getARTEnrollmentNumber() {
        return artEnrollmentNumber;
    }

    public void setARTEnrollmentNumber(String value) {
        this.artEnrollmentNumber = value;
    }

}
