
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MaternalCohortType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MaternalCohortType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VisitID" type="{}StringType"/>
 *         &lt;element name="VisitDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="ViralLoadPeriod" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SampleCollectionDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="ViralLoadResult" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="PMTCTEntryPoint" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="4"/>
 *               &lt;enumeration value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="GestationalAgeAtSampleCollection" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="GestationalAge" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="TimingOfArtInitiation" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="AIT1"/>
 *               &lt;enumeration value="AIT2"/>
 *               &lt;enumeration value="AIT3"/>
 *               &lt;enumeration value="AIT4"/>
 *               &lt;enumeration value="AIT5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TBStatus" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *               &lt;enumeration value="4"/>
 *               &lt;enumeration value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Gravida" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ARTStartDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="DateOfDelivery" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="FamilyPlanningCounselling" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="N"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FamilyPlanningMethod" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="FP1"/>
 *               &lt;enumeration value="FP2"/>
 *               &lt;enumeration value="FP3"/>
 *               &lt;enumeration value="FP4"/>
 *               &lt;enumeration value="FP5"/>
 *               &lt;enumeration value="FP6"/>
 *               &lt;enumeration value="FP7"/>
 *               &lt;enumeration value="FP8"/>
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
@XmlType(name = "MaternalCohortType", propOrder = {
    "visitID",
    "visitDate",
    "viralLoadPeriod",
    "sampleCollectionDate",
    "viralLoadResult",
    "pmtctEntryPoint",
    "gestationalAgeAtSampleCollection",
    "gestationalAge",
    "timingOfArtInitiation",
    "tbStatus",
    "gravida",
    "artStartDate",
    "dateOfDelivery",
    "familyPlanningCounselling",
    "familyPlanningMethod"
})
public class MaternalCohortType {

    @XmlElement(name = "VisitID", required = true)
    protected String visitID;
    @XmlElement(name = "VisitDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar visitDate;
    @XmlElement(name = "ViralLoadPeriod")
    protected String viralLoadPeriod;
    @XmlElement(name = "SampleCollectionDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar sampleCollectionDate;
    @XmlElement(name = "ViralLoadResult")
    protected Double viralLoadResult;
    @XmlElement(name = "PMTCTEntryPoint")
    protected String pmtctEntryPoint;
    @XmlElement(name = "GestationalAgeAtSampleCollection")
    protected Integer gestationalAgeAtSampleCollection;
    @XmlElement(name = "GestationalAge")
    protected Integer gestationalAge;
    @XmlElement(name = "TimingOfArtInitiation")
    protected String timingOfArtInitiation;
    @XmlElement(name = "TBStatus")
    protected String tbStatus;
    @XmlElement(name = "Gravida")
    protected Integer gravida;
    @XmlElement(name = "ARTStartDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar artStartDate;
    @XmlElement(name = "DateOfDelivery")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfDelivery;
    @XmlElement(name = "FamilyPlanningCounselling")
    protected String familyPlanningCounselling;
    @XmlElement(name = "FamilyPlanningMethod")
    protected String familyPlanningMethod;

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
     * Gets the value of the viralLoadPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViralLoadPeriod() {
        return viralLoadPeriod;
    }

    /**
     * Sets the value of the viralLoadPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViralLoadPeriod(String value) {
        this.viralLoadPeriod = value;
    }

    /**
     * Gets the value of the sampleCollectionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSampleCollectionDate() {
        return sampleCollectionDate;
    }

    /**
     * Sets the value of the sampleCollectionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSampleCollectionDate(XMLGregorianCalendar value) {
        this.sampleCollectionDate = value;
    }

    /**
     * Gets the value of the viralLoadResult property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getViralLoadResult() {
        return viralLoadResult;
    }

    /**
     * Sets the value of the viralLoadResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setViralLoadResult(Double value) {
        this.viralLoadResult = value;
    }

    /**
     * Gets the value of the pmtctEntryPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPMTCTEntryPoint() {
        return pmtctEntryPoint;
    }

    /**
     * Sets the value of the pmtctEntryPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPMTCTEntryPoint(String value) {
        this.pmtctEntryPoint = value;
    }

    /**
     * Gets the value of the gestationalAgeAtSampleCollection property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGestationalAgeAtSampleCollection() {
        return gestationalAgeAtSampleCollection;
    }

    /**
     * Sets the value of the gestationalAgeAtSampleCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGestationalAgeAtSampleCollection(Integer value) {
        this.gestationalAgeAtSampleCollection = value;
    }

    /**
     * Gets the value of the gestationalAge property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGestationalAge() {
        return gestationalAge;
    }

    /**
     * Sets the value of the gestationalAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGestationalAge(Integer value) {
        this.gestationalAge = value;
    }

    /**
     * Gets the value of the timingOfArtInitiation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimingOfArtInitiation() {
        return timingOfArtInitiation;
    }

    /**
     * Sets the value of the timingOfArtInitiation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimingOfArtInitiation(String value) {
        this.timingOfArtInitiation = value;
    }

    /**
     * Gets the value of the tbStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTBStatus() {
        return tbStatus;
    }

    /**
     * Sets the value of the tbStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTBStatus(String value) {
        this.tbStatus = value;
    }

    /**
     * Gets the value of the gravida property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGravida() {
        return gravida;
    }

    /**
     * Sets the value of the gravida property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGravida(Integer value) {
        this.gravida = value;
    }

    /**
     * Gets the value of the artStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getARTStartDate() {
        return artStartDate;
    }

    /**
     * Sets the value of the artStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setARTStartDate(XMLGregorianCalendar value) {
        this.artStartDate = value;
    }

    /**
     * Gets the value of the dateOfDelivery property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfDelivery() {
        return dateOfDelivery;
    }

    /**
     * Sets the value of the dateOfDelivery property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfDelivery(XMLGregorianCalendar value) {
        this.dateOfDelivery = value;
    }

    /**
     * Gets the value of the familyPlanningCounselling property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyPlanningCounselling() {
        return familyPlanningCounselling;
    }

    /**
     * Sets the value of the familyPlanningCounselling property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyPlanningCounselling(String value) {
        this.familyPlanningCounselling = value;
    }

    /**
     * Gets the value of the familyPlanningMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyPlanningMethod() {
        return familyPlanningMethod;
    }

    /**
     * Sets the value of the familyPlanningMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyPlanningMethod(String value) {
        this.familyPlanningMethod = value;
    }

}
