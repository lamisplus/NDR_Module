
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PMTCTHTSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PMTCTHTSType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VisitID" type="{}StringType"/>
 *         &lt;element name="VisitDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
 *         &lt;element name="PreviouslyKnownHIVPositive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AcceptedHIVTesting" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HIVTestResult" type="{}TestResultType" minOccurs="0"/>
 *         &lt;element name="ReceivedHIVTestResult" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HIVRetesting" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="RHN"/>
 *               &lt;enumeration value="SHP"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TestedForHepB" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HepBTestResult" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Pos"/>
 *               &lt;enumeration value="Neg"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TestedForHepC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HepCTestResult" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Pos"/>
 *               &lt;enumeration value="Neg"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="HIVHBVCoInfected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="HIVHCVCoInfected" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AgreedToPartnerNotification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ClinicalTBScreening" type="{}PMTCTClinicalTBScreeningType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PMTCTHTSType", propOrder = {
    "visitID",
    "visitDate",
    "pmtctEntryPoint",
    "previouslyKnownHIVPositive",
    "acceptedHIVTesting",
    "hivTestResult",
    "receivedHIVTestResult",
    "hivRetesting",
    "testedForHepB",
    "hepBTestResult",
    "testedForHepC",
    "hepCTestResult",
    "hivhbvCoInfected",
    "hivhcvCoInfected",
    "agreedToPartnerNotification",
    "clinicalTBScreening"
})
public class PMTCTHTSType {

    @XmlElement(name = "VisitID", required = true)
    protected String visitID;
    @XmlElement(name = "VisitDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar visitDate;
    @XmlElement(name = "PMTCTEntryPoint")
    protected String pmtctEntryPoint;
    @XmlElement(name = "PreviouslyKnownHIVPositive")
    protected boolean previouslyKnownHIVPositive;
    @XmlElement(name = "AcceptedHIVTesting")
    protected Boolean acceptedHIVTesting;
    @XmlElement(name = "HIVTestResult")
    protected TestResultType hivTestResult;
    @XmlElement(name = "ReceivedHIVTestResult")
    protected Boolean receivedHIVTestResult;
    @XmlElement(name = "HIVRetesting")
    protected String hivRetesting;
    @XmlElement(name = "TestedForHepB")
    protected Boolean testedForHepB;
    @XmlElement(name = "HepBTestResult")
    protected String hepBTestResult;
    @XmlElement(name = "TestedForHepC")
    protected Boolean testedForHepC;
    @XmlElement(name = "HepCTestResult")
    protected String hepCTestResult;
    @XmlElement(name = "HIVHBVCoInfected")
    protected Boolean hivhbvCoInfected;
    @XmlElement(name = "HIVHCVCoInfected")
    protected Boolean hivhcvCoInfected;
    @XmlElement(name = "AgreedToPartnerNotification")
    protected Boolean agreedToPartnerNotification;
    @XmlElement(name = "ClinicalTBScreening")
    protected PMTCTClinicalTBScreeningType clinicalTBScreening;

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
     * Sets the value of the visitID pmvn dependency:treeroperty.
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
     * Gets the value of the previouslyKnownHIVPositive property.
     * 
     */
    public boolean isPreviouslyKnownHIVPositive() {
        return previouslyKnownHIVPositive;
    }

    /**
     * Sets the value of the previouslyKnownHIVPositive property.
     * 
     */
    public void setPreviouslyKnownHIVPositive(boolean value) {
        this.previouslyKnownHIVPositive = value;
    }

    /**
     * Gets the value of the acceptedHIVTesting property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAcceptedHIVTesting() {
        return acceptedHIVTesting;
    }

    /**
     * Sets the value of the acceptedHIVTesting property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAcceptedHIVTesting(Boolean value) {
        this.acceptedHIVTesting = value;
    }

    /**
     * Gets the value of the hivTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link TestResultType }
     *     
     */
    public TestResultType getHIVTestResult() {
        return hivTestResult;
    }

    /**
     * Sets the value of the hivTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link TestResultType }
     *     
     */
    public void setHIVTestResult(TestResultType value) {
        this.hivTestResult = value;
    }

    /**
     * Gets the value of the receivedHIVTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReceivedHIVTestResult() {
        return receivedHIVTestResult;
    }

    /**
     * Sets the value of the receivedHIVTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReceivedHIVTestResult(Boolean value) {
        this.receivedHIVTestResult = value;
    }

    /**
     * Gets the value of the hivRetesting property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHIVRetesting() {
        return hivRetesting;
    }

    /**
     * Sets the value of the hivRetesting property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHIVRetesting(String value) {
        this.hivRetesting = value;
    }

    /**
     * Gets the value of the testedForHepB property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTestedForHepB() {
        return testedForHepB;
    }

    /**
     * Sets the value of the testedForHepB property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTestedForHepB(Boolean value) {
        this.testedForHepB = value;
    }

    /**
     * Gets the value of the hepBTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHepBTestResult() {
        return hepBTestResult;
    }

    /**
     * Sets the value of the hepBTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHepBTestResult(String value) {
        this.hepBTestResult = value;
    }

    /**
     * Gets the value of the testedForHepC property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTestedForHepC() {
        return testedForHepC;
    }

    /**
     * Sets the value of the testedForHepC property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTestedForHepC(Boolean value) {
        this.testedForHepC = value;
    }

    /**
     * Gets the value of the hepCTestResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHepCTestResult() {
        return hepCTestResult;
    }

    /**
     * Sets the value of the hepCTestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHepCTestResult(String value) {
        this.hepCTestResult = value;
    }

    /**
     * Gets the value of the hivhbvCoInfected property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHIVHBVCoInfected() {
        return hivhbvCoInfected;
    }

    /**
     * Sets the value of the hivhbvCoInfected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHIVHBVCoInfected(Boolean value) {
        this.hivhbvCoInfected = value;
    }

    /**
     * Gets the value of the hivhcvCoInfected property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHIVHCVCoInfected() {
        return hivhcvCoInfected;
    }

    /**
     * Sets the value of the hivhcvCoInfected property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHIVHCVCoInfected(Boolean value) {
        this.hivhcvCoInfected = value;
    }

    /**
     * Gets the value of the agreedToPartnerNotification property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAgreedToPartnerNotification() {
        return agreedToPartnerNotification;
    }

    /**
     * Sets the value of the agreedToPartnerNotification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAgreedToPartnerNotification(Boolean value) {
        this.agreedToPartnerNotification = value;
    }

    /**
     * Gets the value of the clinicalTBScreening property.
     * 
     * @return
     *     possible object is
     *     {@link PMTCTClinicalTBScreeningType }
     *     
     */
    public PMTCTClinicalTBScreeningType getClinicalTBScreening() {
        return clinicalTBScreening;
    }

    /**
     * Sets the value of the clinicalTBScreening property.
     * 
     * @param value
     *     allowed object is
     *     {@link PMTCTClinicalTBScreeningType }
     *     
     */
    public void setClinicalTBScreening(PMTCTClinicalTBScreeningType value) {
        this.clinicalTBScreening = value;
    }

}
