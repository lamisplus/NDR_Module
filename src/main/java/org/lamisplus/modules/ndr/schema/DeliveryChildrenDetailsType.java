package org.lamisplus.modules.ndr.schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeliveryChildrenDetailsType", propOrder = {
    "dateOfDelivery",
    "modeOfDelivery",
    "birthWeightKg",
    "birthLengthCm",
    "birthOutcome",
    "childHospitalNumber",
    "childEntryPoint",
    "sex",
    "hivProphylaxis",
    "syphilisProphylaxis",
    "ctx",
    "hbvVaccination",
    "dnapcr",
    "rapidTestAt18Months",
    "outcomeAt18Months"
})
public class DeliveryChildrenDetailsType {

    @XmlElement(name = "DateOfDelivery")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfDelivery;
    @XmlElement(name = "ModeOfDelivery")
    protected String modeOfDelivery;
    @XmlElement(name = "BirthWeight_kg")
    protected BigDecimal birthWeightKg;
    @XmlElement(name = "BirthLength_cm")
    protected BigDecimal birthLengthCm;
    @XmlElement(name = "BirthOutcome")
    protected String birthOutcome;
    @XmlElement(name = "ChildHospitalNumber")
    protected String childHospitalNumber;
    @XmlElement(name = "ChildEntryPoint")
    protected String childEntryPoint;
    @XmlElement(name = "Sex")
    protected String sex;
    @XmlElement(name = "HIVProphylaxis")
    protected InfantHIVProphylaxisType hivProphylaxis;
    @XmlElement(name = "SyphilisProphylaxis")
    protected InfantSyphilisProphylaxisType syphilisProphylaxis;
    @XmlElement(name = "CTX")
    protected InfantCTXType ctx;
    @XmlElement(name = "HBVVaccination")
    protected InfantHBVVaccinationType hbvVaccination;
    @XmlElement(name = "DNAPCR")
    protected List<DPCRTestType> dnapcr;
    @XmlElement(name = "RapidTestAt18Months")
    protected ConfirmatoryPCRType rapidTestAt18Months;
    @XmlElement(name = "OutcomeAt18Months")
    @XmlSchemaType(name = "string")
    protected InfantOutcomeStatusType outcomeAt18Months;

    public XMLGregorianCalendar getDateOfDelivery() {
        return dateOfDelivery;
    }

    public void setDateOfDelivery(XMLGregorianCalendar value) {
        this.dateOfDelivery = value;
    }

    public String getModeOfDelivery() {
        return modeOfDelivery;
    }

    public void setModeOfDelivery(String value) {
        this.modeOfDelivery = value;
    }

    public BigDecimal getBirthWeightKg() {
        return birthWeightKg;
    }

    public void setBirthWeightKg(BigDecimal value) {
        this.birthWeightKg = value;
    }

    public BigDecimal getBirthLengthCm() {
        return birthLengthCm;
    }

    public void setBirthLengthCm(BigDecimal value) {
        this.birthLengthCm = value;
    }

    public String getBirthOutcome() {
        return birthOutcome;
    }

    public void setBirthOutcome(String value) {
        this.birthOutcome = value;
    }

    public String getChildHospitalNumber() {
        return childHospitalNumber;
    }

    public void setChildHospitalNumber(String value) {
        this.childHospitalNumber = value;
    }

    public String getChildEntryPoint() {
        return childEntryPoint;
    }

    public void setChildEntryPoint(String value) {
        this.childEntryPoint = value;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String value) {
        this.sex = value;
    }

    public InfantHIVProphylaxisType getHIVProphylaxis() {
        return hivProphylaxis;
    }

    public void setHIVProphylaxis(InfantHIVProphylaxisType value) {
        this.hivProphylaxis = value;
    }

    public InfantSyphilisProphylaxisType getSyphilisProphylaxis() {
        return syphilisProphylaxis;
    }

    public void setSyphilisProphylaxis(InfantSyphilisProphylaxisType value) {
        this.syphilisProphylaxis = value;
    }

    public InfantCTXType getCTX() {
        return ctx;
    }

    public void setCTX(InfantCTXType value) {
        this.ctx = value;
    }

    public InfantHBVVaccinationType getHBVVaccination() {
        return hbvVaccination;
    }

    public void setHBVVaccination(InfantHBVVaccinationType value) {
        this.hbvVaccination = value;
    }

    public List<DPCRTestType> getDNAPCR() {
        if (dnapcr == null) {
            dnapcr = new ArrayList<DPCRTestType>();
        }
        return this.dnapcr;
    }

    public ConfirmatoryPCRType getRapidTestAt18Months() {
        return rapidTestAt18Months;
    }

    public void setRapidTestAt18Months(ConfirmatoryPCRType value) {
        this.rapidTestAt18Months = value;
    }

    public InfantOutcomeStatusType getOutcomeAt18Months() {
        return outcomeAt18Months;
    }

    public void setOutcomeAt18Months(InfantOutcomeStatusType value) {
        this.outcomeAt18Months = value;
    }

}
