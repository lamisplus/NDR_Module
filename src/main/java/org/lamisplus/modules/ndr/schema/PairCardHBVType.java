package org.lamisplus.modules.ndr.schema;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PairCardHBVType", propOrder = {
    "testResult",
    "vlResultDate",
    "vlResultCopiesPerML",
    "treatmentProphylaxis",
    "drugName"
})
public class PairCardHBVType {

    @XmlElement(name = "TestResult")
    protected String testResult;
    @XmlElement(name = "VLResultDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar vlResultDate;
    @XmlElement(name = "VLResult_CopiesPerML")
    protected BigDecimal vlResultCopiesPerML;
    @XmlElement(name = "TreatmentProphylaxis")
    protected String treatmentProphylaxis;
    @XmlElement(name = "DrugName")
    protected String drugName;

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String value) {
        this.testResult = value;
    }

    public XMLGregorianCalendar getVLResultDate() {
        return vlResultDate;
    }

    public void setVLResultDate(XMLGregorianCalendar value) {
        this.vlResultDate = value;
    }

    public BigDecimal getVLResultCopiesPerML() {
        return vlResultCopiesPerML;
    }

    public void setVLResultCopiesPerML(BigDecimal value) {
        this.vlResultCopiesPerML = value;
    }

    public String getTreatmentProphylaxis() {
        return treatmentProphylaxis;
    }

    public void setTreatmentProphylaxis(String value) {
        this.treatmentProphylaxis = value;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String value) {
        this.drugName = value;
    }

}
