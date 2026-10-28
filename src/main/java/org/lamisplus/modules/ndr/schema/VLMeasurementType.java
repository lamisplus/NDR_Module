package org.lamisplus.modules.ndr.schema;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VLMeasurementType", propOrder = {
    "resultDate",
    "resultCopiesPerML"
})
public class VLMeasurementType {

    @XmlElement(name = "ResultDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar resultDate;
    @XmlElement(name = "ResultCopiesPerML", required = true)
    protected BigDecimal resultCopiesPerML;

    public XMLGregorianCalendar getResultDate() {
        return resultDate;
    }

    public void setResultDate(XMLGregorianCalendar value) {
        this.resultDate = value;
    }

    public BigDecimal getResultCopiesPerML() {
        return resultCopiesPerML;
    }

    public void setResultCopiesPerML(BigDecimal value) {
        this.resultCopiesPerML = value;
    }

}
