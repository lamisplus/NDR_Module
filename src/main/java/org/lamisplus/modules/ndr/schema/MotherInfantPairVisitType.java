package org.lamisplus.modules.ndr.schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MotherInfantPairVisitType", propOrder = {
    "visitId",
    "visitDate",
    "scheduled",
    "gestationalAgeWeeks",
    "motherWeightKg",
    "sfhLenghtCm",
    "motherCurrentStatus",
    "motherCurrentARTStatus",
    "motherCurrentARTRegimen",
    "motherCurrentHBVStatus",
    "motherCurrentHBVDrugName",
    "motherCurrentSyphilisStatus",
    "syphilisDrugAdministered",
    "viralLoad",
    "infantFeedingPractice",
    "infantOnCTX",
    "referredToTreatment",
    "nextAppointmentDate"
})
public class MotherInfantPairVisitType {

    @XmlElement(name = "VisitId", required = true)
    protected String visitId;
    @XmlElement(name = "VisitDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar visitDate;
    @XmlElement(name = "Scheduled")
    protected Boolean scheduled;
    @XmlElement(name = "GestationalAge_Weeks")
    protected BigInteger gestationalAgeWeeks;
    @XmlElement(name = "MotherWeight_kg")
    protected BigDecimal motherWeightKg;
    @XmlElement(name = "SFHLenght_cm")
    protected BigDecimal sfhLenghtCm;
    @XmlElement(name = "MotherCurrentStatus")
    @XmlSchemaType(name = "string")
    protected PregnancyBreastfeedingStatusType motherCurrentStatus;
    @XmlElement(name = "MotherCurrentARTStatus")
    protected String motherCurrentARTStatus;
    @XmlElement(name = "MotherCurrentARTRegimen")
    protected MotherCurrentARTRegimenType motherCurrentARTRegimen;
    @XmlElement(name = "MotherCurrentHBVStatus")
    protected String motherCurrentHBVStatus;
    @XmlElement(name = "MotherCurrentHBVDrugName")
    protected String motherCurrentHBVDrugName;
    @XmlElement(name = "MotherCurrentSyphilisStatus")
    protected String motherCurrentSyphilisStatus;
    @XmlElement(name = "SyphilisDrugAdministered")
    protected String syphilisDrugAdministered;
    @XmlElement(name = "ViralLoad")
    protected PairCardVisitVLType viralLoad;
    @XmlElement(name = "InfantFeedingPractice")
    protected String infantFeedingPractice;
    @XmlElement(name = "InfantOnCTX")
    protected String infantOnCTX;
    @XmlElement(name = "ReferredToTreatment")
    protected String referredToTreatment;
    @XmlElement(name = "NextAppointmentDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar nextAppointmentDate;

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String value) {
        this.visitId = value;
    }

    public XMLGregorianCalendar getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(XMLGregorianCalendar value) {
        this.visitDate = value;
    }

    public Boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(Boolean value) {
        this.scheduled = value;
    }

    public BigInteger getGestationalAgeWeeks() {
        return gestationalAgeWeeks;
    }

    public void setGestationalAgeWeeks(BigInteger value) {
        this.gestationalAgeWeeks = value;
    }

    public BigDecimal getMotherWeightKg() {
        return motherWeightKg;
    }

    public void setMotherWeightKg(BigDecimal value) {
        this.motherWeightKg = value;
    }

    public BigDecimal getSFHLenghtCm() {
        return sfhLenghtCm;
    }

    public void setSFHLenghtCm(BigDecimal value) {
        this.sfhLenghtCm = value;
    }

    public PregnancyBreastfeedingStatusType getMotherCurrentStatus() {
        return motherCurrentStatus;
    }

    public void setMotherCurrentStatus(PregnancyBreastfeedingStatusType value) {
        this.motherCurrentStatus = value;
    }

    public String getMotherCurrentARTStatus() {
        return motherCurrentARTStatus;
    }

    public void setMotherCurrentARTStatus(String value) {
        this.motherCurrentARTStatus = value;
    }

    public MotherCurrentARTRegimenType getMotherCurrentARTRegimen() {
        return motherCurrentARTRegimen;
    }

    public void setMotherCurrentARTRegimen(MotherCurrentARTRegimenType value) {
        this.motherCurrentARTRegimen = value;
    }

    public String getMotherCurrentHBVStatus() {
        return motherCurrentHBVStatus;
    }

    public void setMotherCurrentHBVStatus(String value) {
        this.motherCurrentHBVStatus = value;
    }

    public String getMotherCurrentHBVDrugName() {
        return motherCurrentHBVDrugName;
    }

    public void setMotherCurrentHBVDrugName(String value) {
        this.motherCurrentHBVDrugName = value;
    }

    public String getMotherCurrentSyphilisStatus() {
        return motherCurrentSyphilisStatus;
    }

    public void setMotherCurrentSyphilisStatus(String value) {
        this.motherCurrentSyphilisStatus = value;
    }

    public String getSyphilisDrugAdministered() {
        return syphilisDrugAdministered;
    }

    public void setSyphilisDrugAdministered(String value) {
        this.syphilisDrugAdministered = value;
    }

    public PairCardVisitVLType getViralLoad() {
        return viralLoad;
    }

    public void setViralLoad(PairCardVisitVLType value) {
        this.viralLoad = value;
    }

    public String getInfantFeedingPractice() {
        return infantFeedingPractice;
    }

    public void setInfantFeedingPractice(String value) {
        this.infantFeedingPractice = value;
    }

    public String getInfantOnCTX() {
        return infantOnCTX;
    }

    public void setInfantOnCTX(String value) {
        this.infantOnCTX = value;
    }

    public String getReferredToTreatment() {
        return referredToTreatment;
    }

    public void setReferredToTreatment(String value) {
        this.referredToTreatment = value;
    }

    public XMLGregorianCalendar getNextAppointmentDate() {
        return nextAppointmentDate;
    }

    public void setNextAppointmentDate(XMLGregorianCalendar value) {
        this.nextAppointmentDate = value;
    }

}
