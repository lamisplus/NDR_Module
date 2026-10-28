package org.lamisplus.modules.ndr.schema;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PMTCTRegisterType", propOrder = {
    "visitID",
    "visitDate",
    "hospitalNo",
    "ancNumber",
    "pregnancyBreastfeedingStatus",
    "gestationalAgeAtBooking",
    "knownHIVPositive",
    "hivEarlyAcute",
    "hivEarlyViralLoad",
    "hivtranc",
    "hivtrld",
    "hivtrbf",
    "hivrtanc",
    "hivrtld",
    "hivrtbf",
    "dateOfInitiation",
    "timingOfArtInitiation",
    "syphilis",
    "hepatitisB",
    "initiatedOnProphylaxis",
    "tbScreening",
    "viralLoad",
    "partnerNotification"
})
public class PMTCTRegisterType {

    @XmlElement(name = "VisitID", required = true)
    protected String visitID;
    @XmlElement(name = "VisitDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar visitDate;
    @XmlElement(name = "HospitalNo")
    protected String hospitalNo;
    @XmlElement(name = "ANCNumber")
    protected String ancNumber;
    @XmlElement(name = "PregnancyBreastfeedingStatus")
    @XmlSchemaType(name = "string")
    protected PregnancyBreastfeedingStatusType pregnancyBreastfeedingStatus;
    @XmlElement(name = "GestationalAgeAtBooking")
    protected BigInteger gestationalAgeAtBooking;
    @XmlElement(name = "KnownHIVPositive")
    @XmlSchemaType(name = "string")
    protected KnownHIVPositiveStatusType knownHIVPositive;
    @XmlElement(name = "HIVEarlyAcute")
    protected String hivEarlyAcute;
    @XmlElement(name = "HIVEarlyViralLoad")
    protected String hivEarlyViralLoad;
    @XmlElement(name = "HIVTRANC")
    protected String hivtranc;
    @XmlElement(name = "HIVTRLD")
    protected String hivtrld;
    @XmlElement(name = "HIVTRBF")
    protected String hivtrbf;
    @XmlElement(name = "HIVRTANC")
    protected String hivrtanc;
    @XmlElement(name = "HIVRTLD")
    protected String hivrtld;
    @XmlElement(name = "HIVRTBF")
    protected String hivrtbf;
    @XmlElement(name = "DateOfInitiation")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfInitiation;
    @XmlElement(name = "TimingOfArtInitiation")
    protected String timingOfArtInitiation;
    @XmlElement(name = "Syphilis")
    protected RegisterSyphilisType syphilis;
    @XmlElement(name = "HepatitisB")
    protected RegisterHBVType hepatitisB;
    @XmlElement(name = "InitiatedOnProphylaxis")
    protected String initiatedOnProphylaxis;
    @XmlElement(name = "TBScreening")
    protected RegisterTBScreeningType tbScreening;
    @XmlElement(name = "ViralLoad")
    protected RegisterVLType viralLoad;
    @XmlElement(name = "PartnerNotification")
    protected PartnerNotificationType partnerNotification;

    public String getVisitID() {
        return visitID;
    }

    public void setVisitID(String value) {
        this.visitID = value;
    }

    public XMLGregorianCalendar getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(XMLGregorianCalendar value) {
        this.visitDate = value;
    }

    public String getHospitalNo() {
        return hospitalNo;
    }

    public void setHospitalNo(String value) {
        this.hospitalNo = value;
    }

    public String getANCNumber() {
        return ancNumber;
    }

    public void setANCNumber(String value) {
        this.ancNumber = value;
    }

    public PregnancyBreastfeedingStatusType getPregnancyBreastfeedingStatus() {
        return pregnancyBreastfeedingStatus;
    }

    public void setPregnancyBreastfeedingStatus(PregnancyBreastfeedingStatusType value) {
        this.pregnancyBreastfeedingStatus = value;
    }

    public BigInteger getGestationalAgeAtBooking() {
        return gestationalAgeAtBooking;
    }

    public void setGestationalAgeAtBooking(BigInteger value) {
        this.gestationalAgeAtBooking = value;
    }

    public KnownHIVPositiveStatusType getKnownHIVPositive() {
        return knownHIVPositive;
    }

    public void setKnownHIVPositive(KnownHIVPositiveStatusType value) {
        this.knownHIVPositive = value;
    }

    public String getHIVEarlyAcute() {
        return hivEarlyAcute;
    }

    public void setHIVEarlyAcute(String value) {
        this.hivEarlyAcute = value;
    }

    public String getHIVEarlyViralLoad() {
        return hivEarlyViralLoad;
    }

    public void setHIVEarlyViralLoad(String value) {
        this.hivEarlyViralLoad = value;
    }

    public String getHIVTRANC() {
        return hivtranc;
    }

    public void setHIVTRANC(String value) {
        this.hivtranc = value;
    }

    public String getHIVTRLD() {
        return hivtrld;
    }

    public void setHIVTRLD(String value) {
        this.hivtrld = value;
    }

    public String getHIVTRBF() {
        return hivtrbf;
    }

    public void setHIVTRBF(String value) {
        this.hivtrbf = value;
    }

    public String getHIVRTANC() {
        return hivrtanc;
    }

    public void setHIVRTANC(String value) {
        this.hivrtanc = value;
    }

    public String getHIVRTLD() {
        return hivrtld;
    }

    public void setHIVRTLD(String value) {
        this.hivrtld = value;
    }

    public String getHIVRTBF() {
        return hivrtbf;
    }

    public void setHIVRTBF(String value) {
        this.hivrtbf = value;
    }

    public XMLGregorianCalendar getDateOfInitiation() {
        return dateOfInitiation;
    }

    public void setDateOfInitiation(XMLGregorianCalendar value) {
        this.dateOfInitiation = value;
    }

    public String getTimingOfArtInitiation() {
        return timingOfArtInitiation;
    }

    public void setTimingOfArtInitiation(String value) {
        this.timingOfArtInitiation = value;
    }

    public RegisterSyphilisType getSyphilis() {
        return syphilis;
    }

    public void setSyphilis(RegisterSyphilisType value) {
        this.syphilis = value;
    }

    public RegisterHBVType getHepatitisB() {
        return hepatitisB;
    }

    public void setHepatitisB(RegisterHBVType value) {
        this.hepatitisB = value;
    }

    public String getInitiatedOnProphylaxis() {
        return initiatedOnProphylaxis;
    }

    public void setInitiatedOnProphylaxis(String value) {
        this.initiatedOnProphylaxis = value;
    }

    public RegisterTBScreeningType getTBScreening() {
        return tbScreening;
    }

    public void setTBScreening(RegisterTBScreeningType value) {
        this.tbScreening = value;
    }

    public RegisterVLType getViralLoad() {
        return viralLoad;
    }

    public void setViralLoad(RegisterVLType value) {
        this.viralLoad = value;
    }

    public PartnerNotificationType getPartnerNotification() {
        return partnerNotification;
    }

    public void setPartnerNotification(PartnerNotificationType value) {
        this.partnerNotification = value;
    }

}
