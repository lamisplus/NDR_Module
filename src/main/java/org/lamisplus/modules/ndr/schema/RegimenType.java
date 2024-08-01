
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RegimenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegimenType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VisitID" type="{}StringType"/>
 *         &lt;element name="VisitDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="PrescribedRegimen" type="{}CodedSimpleType"/>
 *         &lt;element name="PrescribedRegimenTypeCode" type="{}CodeType"/>
 *         &lt;element name="PrescribedRegimenLineCode" type="{}CodeType" minOccurs="0"/>
 *         &lt;element name="PrescribedRegimenDuration" type="{}CodeType"/>
 *         &lt;element name="PrescribedRegimenDispensedDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="DateRegimenStarted" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="DateRegimenStartedDD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DateRegimenStartedMM" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DateRegimenStartedYYYY" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DateRegimenEnded" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="DateRegimenEndedDD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DateRegimenEndedMM" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DateRegimenEndedYYYY" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="0"/>
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegimenType", propOrder = {
    "visitID",
    "visitDate",
    "prescribedRegimen",
    "prescribedRegimenTypeCode",
    "prescribedRegimenLineCode",
    "prescribedRegimenDuration",
    "prescribedRegimenDispensedDate",
    "dateRegimenStarted",
    "dateRegimenStartedDD",
    "dateRegimenStartedMM",
    "dateRegimenStartedYYYY",
    "dateRegimenEnded",
    "dateRegimenEndedDD",
    "dateRegimenEndedMM",
    "dateRegimenEndedYYYY",
    "pillBalance",
    "differentiatedServiceDelivery",
    "dispensing",
    "multiMonthDispensing"
})
public class RegimenType {
    @Override
    public String toString() {
        return "RegimenType{" +
                "visitID='" + visitID + '\'' +
                ", visitDate=" + visitDate +
                ", prescribedRegimen=" + prescribedRegimen +
                ", prescribedRegimenTypeCode='" + prescribedRegimenTypeCode + '\'' +
                ", prescribedRegimenLineCode='" + prescribedRegimenLineCode + '\'' +
                ", prescribedRegimenDuration='" + prescribedRegimenDuration + '\'' +
                ", prescribedRegimenDispensedDate=" + prescribedRegimenDispensedDate +
                ", dateRegimenStarted=" + dateRegimenStarted +
                ", dateRegimenStartedDD='" + dateRegimenStartedDD + '\'' +
                ", dateRegimenStartedMM='" + dateRegimenStartedMM + '\'' +
                ", dateRegimenStartedYYYY='" + dateRegimenStartedYYYY + '\'' +
                ", dateRegimenEnded=" + dateRegimenEnded +
                ", dateRegimenEndedDD='" + dateRegimenEndedDD + '\'' +
                ", dateRegimenEndedMM='" + dateRegimenEndedMM + '\'' +
                ", dateRegimenEndedYYYY='" + dateRegimenEndedYYYY + '\'' +
                ", pillBalance='" + pillBalance + '\'' +
                ", differentiatedServiceDelivery='" + differentiatedServiceDelivery + '\'' +
                ", dispensing='" + dispensing + '\'' +
                ", multiMonthDispensing='" + multiMonthDispensing + '\'' +
                '}';
    }
    
    @XmlElement(name = "VisitID", required = true)
    protected String visitID;
    @XmlElement(name = "VisitDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar visitDate;
    @XmlElement(name = "PrescribedRegimen", required = true)
    protected CodedSimpleType prescribedRegimen;
    @XmlElement(name = "PrescribedRegimenTypeCode", required = true)
    protected String prescribedRegimenTypeCode;
    @XmlElement(name = "PrescribedRegimenLineCode")
    protected String prescribedRegimenLineCode;
    @XmlElement(name = "PrescribedRegimenDuration", required = true)
    protected String prescribedRegimenDuration;
    @XmlElement(name = "PrescribedRegimenDispensedDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar prescribedRegimenDispensedDate;
    @XmlElement(name = "DateRegimenStarted")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateRegimenStarted;
    @XmlElement(name = "DateRegimenStartedDD")
    protected String dateRegimenStartedDD;
    @XmlElement(name = "DateRegimenStartedMM")
    protected String dateRegimenStartedMM;
    @XmlElement(name = "DateRegimenStartedYYYY")
    protected String dateRegimenStartedYYYY;
    @XmlElement(name = "DateRegimenEnded")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateRegimenEnded;
    @XmlElement(name = "DateRegimenEndedDD")
    protected String dateRegimenEndedDD;
    @XmlElement(name = "DateRegimenEndedMM")
    protected String dateRegimenEndedMM;
    @XmlElement(name = "DateRegimenEndedYYYY")
    protected String dateRegimenEndedYYYY;

    @XmlElement(name = "PillBalance")
    protected Integer pillBalance;
    @XmlElement(name = "DifferentiatedServiceDelivery")
    protected String differentiatedServiceDelivery;
    @XmlElement(name = "Dispensing")
    protected String dispensing;
    @XmlElement(name = "MultiMonthDispensing")
    protected String multiMonthDispensing;

    /**
     * Gets the value of the visitID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisitID() {
        return visitID;
    }

    /**
     * Sets the value of the visitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisitID(String value) {
        this.visitID = value;
    }

    /**
     * Gets the value of the visitDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getVisitDate() {
        return visitDate;
    }

    /**
     * Sets the value of the visitDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setVisitDate(XMLGregorianCalendar value) {
        this.visitDate = value;
    }

    /**
     * Gets the value of the prescribedRegimen property.
     * 
     * @return
     *     possible object is
     *     {@link CodedSimpleType }
     *     
     */
    public CodedSimpleType getPrescribedRegimen() {
        return prescribedRegimen;
    }

    /**
     * Sets the value of the prescribedRegimen property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodedSimpleType }
     *     
     */
    public void setPrescribedRegimen(CodedSimpleType value) {
        this.prescribedRegimen = value;
    }

    /**
     * Gets the value of the prescribedRegimenTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescribedRegimenTypeCode() {
        return prescribedRegimenTypeCode;
    }

    /**
     * Sets the value of the prescribedRegimenTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescribedRegimenTypeCode(String value) {
        this.prescribedRegimenTypeCode = value;
    }

    /**
     * Gets the value of the prescribedRegimenLineCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescribedRegimenLineCode() {
        return prescribedRegimenLineCode;
    }

    /**
     * Sets the value of the prescribedRegimenLineCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescribedRegimenLineCode(String value) {
        this.prescribedRegimenLineCode = value;
    }

    /**
     * Gets the value of the prescribedRegimenDuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescribedRegimenDuration() {
        return prescribedRegimenDuration;
    }

    /**
     * Sets the value of the prescribedRegimenDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescribedRegimenDuration(String value) {
        this.prescribedRegimenDuration = value;
    }

    /**
     * Gets the value of the prescribedRegimenDispensedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPrescribedRegimenDispensedDate() {
        return prescribedRegimenDispensedDate;
    }

    /**
     * Sets the value of the prescribedRegimenDispensedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPrescribedRegimenDispensedDate(XMLGregorianCalendar value) {
        this.prescribedRegimenDispensedDate = value;
    }

    /**
     * Gets the value of the dateRegimenStarted property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateRegimenStarted() {
        return dateRegimenStarted;
    }

    /**
     * Sets the value of the dateRegimenStarted property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateRegimenStarted(XMLGregorianCalendar value) {
        this.dateRegimenStarted = value;
    }

    /**
     * Gets the value of the dateRegimenStartedDD property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegimenStartedDD() {
        return dateRegimenStartedDD;
    }

    /**
     * Sets the value of the dateRegimenStartedDD property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegimenStartedDD(String value) {
        this.dateRegimenStartedDD = value;
    }

    /**
     * Gets the value of the dateRegimenStartedMM property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegimenStartedMM() {
        return dateRegimenStartedMM;
    }

    /**
     * Sets the value of the dateRegimenStartedMM property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegimenStartedMM(String value) {
        this.dateRegimenStartedMM = value;
    }

    /**
     * Gets the value of the dateRegimenStartedYYYY property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegimenStartedYYYY() {
        return dateRegimenStartedYYYY;
    }

    /**
     * Sets the value of the dateRegimenStartedYYYY property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegimenStartedYYYY(String value) {
        this.dateRegimenStartedYYYY = value;
    }

    /**
     * Gets the value of the dateRegimenEnded property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateRegimenEnded() {
        return dateRegimenEnded;
    }

    /**
     * Sets the value of the dateRegimenEnded property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateRegimenEnded(XMLGregorianCalendar value) {
        this.dateRegimenEnded = value;
    }

    /**
     * Gets the value of the dateRegimenEndedDD property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegimenEndedDD() {
        return dateRegimenEndedDD;
    }

    /**
     * Sets the value of the dateRegimenEndedDD property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegimenEndedDD(String value) {
        this.dateRegimenEndedDD = value;
    }

    /**
     * Gets the value of the dateRegimenEndedMM property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegimenEndedMM() {
        return dateRegimenEndedMM;
    }

    /**
     * Sets the value of the dateRegimenEndedMM property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegimenEndedMM(String value) {
        this.dateRegimenEndedMM = value;
    }

    /**
     * Gets the value of the dateRegimenEndedYYYY property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegimenEndedYYYY() {
        return dateRegimenEndedYYYY;
    }

    /**
     * Sets the value of the dateRegimenEndedYYYY property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegimenEndedYYYY(String value) {
        this.dateRegimenEndedYYYY = value;
    }

    /**
     * Gets the value of the pillBalance property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getPillBalance() {
        return pillBalance;
    }

    /**
     * Sets the value of the pillBalance property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setPillBalance(Integer value) {
        this.pillBalance = value;
    }

    /**
     * Gets the value of the differentiatedServiceDelivery property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDifferentiatedServiceDelivery() {
        return differentiatedServiceDelivery;
    }

    /**
     * Sets the value of the differentiatedServiceDelivery property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDifferentiatedServiceDelivery(String value) {
        this.differentiatedServiceDelivery = value;
    }
    /**
     * Gets the value of the dispensing property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDispensing() {
        return dispensing;
    }

    /**
     * Sets the value of the dispensing property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDispensing(String value) {
        this.dispensing = value;
    }

    /**
     * Gets the value of the multiMonthDispensing property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMultiMonthDispensing() {
        return multiMonthDispensing;
    }

    /**
     * Sets the value of the multiMonthDispensing property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMultiMonthDispensing(String value) {
        this.multiMonthDispensing = value;
    }
}
