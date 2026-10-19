package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfantCohortRegistrationType", propOrder = {
    "dateOfBirth",
    "dateOfFirstVisit",
    "ageAtFirstVisit",
    "childHospitalRegNo",
    "infantSex",
    "childEntryPoint",
    "motherHospitalNumber",
    "motherANCNumber",
    "motherStatus",
    "motherARTInitiationTiming",
    "motherHIVARTRegimen",
    "motherSyphilisTreatmentStartDate",
    "motherHepatitisBTreatmentStartDate",
    "infantHBVProphylaxis",
    "infantARVProphylaxisType",
    "infantARVProphylaxisTiming",
    "ctxAgeCategory",
    "firstPCR",
    "secondPCR",
    "thirdPCR",
    "confirmatoryPCR",
    "rapidAntibodyTest",
    "outcomeAt18Months",
    "artEnrollment"
})
public class InfantCohortRegistrationType {

    @XmlElement(name = "DateOfBirth")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlElement(name = "DateOfFirstVisit")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfFirstVisit;
    @XmlElement(name = "AgeAtFirstVisit")
    protected Integer ageAtFirstVisit;
    @XmlElement(name = "ChildHospitalRegNo")
    protected String childHospitalRegNo;
    @XmlElement(name = "InfantSex")
    protected String infantSex;
    @XmlElement(name = "ChildEntryPoint")
    protected String childEntryPoint;
    @XmlElement(name = "MotherHospitalNumber")
    protected String motherHospitalNumber;
    @XmlElement(name = "MotherANCNumber")
    protected String motherANCNumber;
    @XmlElement(name = "MotherStatus")
    protected String motherStatus;
    @XmlElement(name = "MotherARTInitiationTiming")
    protected String motherARTInitiationTiming;
    @XmlElement(name = "MotherHIVARTRegimen")
    protected MotherCurrentARTRegimenType motherHIVARTRegimen;
    @XmlElement(name = "MotherSyphilisTreatmentStartDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar motherSyphilisTreatmentStartDate;
    @XmlElement(name = "MotherHepatitisBTreatmentStartDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar motherHepatitisBTreatmentStartDate;
    @XmlElement(name = "InfantHBVProphylaxis")
    protected InfantHBVProphylaxisType infantHBVProphylaxis;
    @XmlElement(name = "InfantARVProphylaxisType")
    @XmlSchemaType(name = "string")
    protected InfantARVProphylaxisType infantARVProphylaxisType;
    @XmlElement(name = "InfantARVProphylaxisTiming")
    @XmlSchemaType(name = "string")
    protected InfantARVProphylaxisTimingType infantARVProphylaxisTiming;
    @XmlElement(name = "CTXAgeCategory")
    @XmlSchemaType(name = "string")
    protected CTXAgeCategoryType ctxAgeCategory;
    @XmlElement(name = "FirstPCR")
    protected PCRTestType firstPCR;
    @XmlElement(name = "SecondPCR")
    protected ConfirmatoryPCRType secondPCR;
    @XmlElement(name = "ThirdPCR")
    protected ConfirmatoryPCRType thirdPCR;
    @XmlElement(name = "ConfirmatoryPCR")
    protected ConfirmatoryPCRType confirmatoryPCR;
    @XmlElement(name = "RapidAntibodyTest")
    protected InfantRapidAntibodyTestType rapidAntibodyTest;
    @XmlElement(name = "OutcomeAt18Months")
    @XmlSchemaType(name = "string")
    protected InfantOutcomeStatusType outcomeAt18Months;
    @XmlElement(name = "ARTEnrollment")
    protected InfantARTEnrollmentType artEnrollment;

    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    public XMLGregorianCalendar getDateOfFirstVisit() {
        return dateOfFirstVisit;
    }

    public void setDateOfFirstVisit(XMLGregorianCalendar value) {
        this.dateOfFirstVisit = value;
    }

    public Integer getAgeAtFirstVisit() {
        return ageAtFirstVisit;
    }

    public void setAgeAtFirstVisit(Integer value) {
        this.ageAtFirstVisit = value;
    }

    public String getChildHospitalRegNo() {
        return childHospitalRegNo;
    }

    public void setChildHospitalRegNo(String value) {
        this.childHospitalRegNo = value;
    }

    public String getInfantSex() {
        return infantSex;
    }

    public void setInfantSex(String value) {
        this.infantSex = value;
    }

    public String getChildEntryPoint() {
        return childEntryPoint;
    }

    public void setChildEntryPoint(String value) {
        this.childEntryPoint = value;
    }

    public String getMotherHospitalNumber() {
        return motherHospitalNumber;
    }

    public void setMotherHospitalNumber(String value) {
        this.motherHospitalNumber = value;
    }

    public String getMotherANCNumber() {
        return motherANCNumber;
    }

    public void setMotherANCNumber(String value) {
        this.motherANCNumber = value;
    }

    public String getMotherStatus() {
        return motherStatus;
    }

    public void setMotherStatus(String value) {
        this.motherStatus = value;
    }

    public String getMotherARTInitiationTiming() {
        return motherARTInitiationTiming;
    }

    public void setMotherARTInitiationTiming(String value) {
        this.motherARTInitiationTiming = value;
    }

    public MotherCurrentARTRegimenType getMotherHIVARTRegimen() {
        return motherHIVARTRegimen;
    }

    public void setMotherHIVARTRegimen(MotherCurrentARTRegimenType value) {
        this.motherHIVARTRegimen = value;
    }

    public XMLGregorianCalendar getMotherSyphilisTreatmentStartDate() {
        return motherSyphilisTreatmentStartDate;
    }

    public void setMotherSyphilisTreatmentStartDate(XMLGregorianCalendar value) {
        this.motherSyphilisTreatmentStartDate = value;
    }

    public XMLGregorianCalendar getMotherHepatitisBTreatmentStartDate() {
        return motherHepatitisBTreatmentStartDate;
    }

    public void setMotherHepatitisBTreatmentStartDate(XMLGregorianCalendar value) {
        this.motherHepatitisBTreatmentStartDate = value;
    }

    public InfantHBVProphylaxisType getInfantHBVProphylaxis() {
        return infantHBVProphylaxis;
    }

    public void setInfantHBVProphylaxis(InfantHBVProphylaxisType value) {
        this.infantHBVProphylaxis = value;
    }

    public InfantARVProphylaxisType getInfantARVProphylaxisType() {
        return infantARVProphylaxisType;
    }

    public void setInfantARVProphylaxisType(InfantARVProphylaxisType value) {
        this.infantARVProphylaxisType = value;
    }

    public InfantARVProphylaxisTimingType getInfantARVProphylaxisTiming() {
        return infantARVProphylaxisTiming;
    }

    public void setInfantARVProphylaxisTiming(InfantARVProphylaxisTimingType value) {
        this.infantARVProphylaxisTiming = value;
    }

    public CTXAgeCategoryType getCTXAgeCategory() {
        return ctxAgeCategory;
    }

    public void setCTXAgeCategory(CTXAgeCategoryType value) {
        this.ctxAgeCategory = value;
    }

    public PCRTestType getFirstPCR() {
        return firstPCR;
    }

    public void setFirstPCR(PCRTestType value) {
        this.firstPCR = value;
    }

    public ConfirmatoryPCRType getSecondPCR() {
        return secondPCR;
    }

    public void setSecondPCR(ConfirmatoryPCRType value) {
        this.secondPCR = value;
    }

    public ConfirmatoryPCRType getThirdPCR() {
        return thirdPCR;
    }

    public void setThirdPCR(ConfirmatoryPCRType value) {
        this.thirdPCR = value;
    }

    public ConfirmatoryPCRType getConfirmatoryPCR() {
        return confirmatoryPCR;
    }

    public void setConfirmatoryPCR(ConfirmatoryPCRType value) {
        this.confirmatoryPCR = value;
    }

    public InfantRapidAntibodyTestType getRapidAntibodyTest() {
        return rapidAntibodyTest;
    }

    public void setRapidAntibodyTest(InfantRapidAntibodyTestType value) {
        this.rapidAntibodyTest = value;
    }

    public InfantOutcomeStatusType getOutcomeAt18Months() {
        return outcomeAt18Months;
    }

    public void setOutcomeAt18Months(InfantOutcomeStatusType value) {
        this.outcomeAt18Months = value;
    }

    public InfantARTEnrollmentType getARTEnrollment() {
        return artEnrollment;
    }

    public void setARTEnrollment(InfantARTEnrollmentType value) {
        this.artEnrollment = value;
    }

}
