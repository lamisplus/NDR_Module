package org.lamisplus.modules.ndr.schema;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PairCardVisitVLType", propOrder = {
    "dateSampleCollected",
    "dateResultReceived",
    "resultCopiesPerML"
})
public class PairCardVisitVLType {

    @XmlElement(name = "DateSampleCollected")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateSampleCollected;
    @XmlElement(name = "DateResultReceived")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateResultReceived;
    @XmlElement(name = "ResultCopiesPerML")
    protected BigDecimal resultCopiesPerML;

    public XMLGregorianCalendar getDateSampleCollected() {
        return dateSampleCollected;
    }

    public void setDateSampleCollected(XMLGregorianCalendar value) {
        this.dateSampleCollected = value;
    }

    public XMLGregorianCalendar getDateResultReceived() {
        return dateResultReceived;
    }

    public void setDateResultReceived(XMLGregorianCalendar value) {
        this.dateResultReceived = value;
    }

    public BigDecimal getResultCopiesPerML() {
        return resultCopiesPerML;
    }

    public void setResultCopiesPerML(BigDecimal value) {
        this.resultCopiesPerML = value;
    }

}
