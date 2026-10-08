package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KnowledgeAssessmentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="KnowledgeAssessmentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PreviouslyTestedHIVNegative" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientInformedAboutHIVTransmissionRoutes" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientPregnant" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientInformedOfHIVTransmissionRiskFactors" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientInformedAboutPreventingHIV" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientInformedAboutPossibleTestResults" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="InformedConsentForHIVTestingGiven" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="TimeOfLastHIVNegativeTest" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="LT3M"/&gt;
 *               &lt;enumeration value="GT6M"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KnowledgeAssessmentType", propOrder = {
        "previouslyTestedHIVNegative",
        "clientInformedAboutHIVTransmissionRoutes",
        "clientPregnant",
        "clientInformedOfHIVTransmissionRiskFactors",
        "clientInformedAboutPreventingHIV",
        "clientInformedAboutPossibleTestResults",
        "informedConsentForHIVTestingGiven",
        "timeOfLastHIVNegativeTest"
})
public class KnowledgeAssessmentType {

    @XmlElement(name = "PreviouslyTestedHIVNegative")
    protected Boolean previouslyTestedHIVNegative;
    @XmlElement(name = "ClientInformedAboutHIVTransmissionRoutes")
    protected Boolean clientInformedAboutHIVTransmissionRoutes;
    @XmlElement(name = "ClientPregnant")
    protected Boolean clientPregnant;
    @XmlElement(name = "ClientInformedOfHIVTransmissionRiskFactors")
    protected Boolean clientInformedOfHIVTransmissionRiskFactors;
    @XmlElement(name = "ClientInformedAboutPreventingHIV")
    protected Boolean clientInformedAboutPreventingHIV;
    @XmlElement(name = "ClientInformedAboutPossibleTestResults")
    protected Boolean clientInformedAboutPossibleTestResults;
    @XmlElement(name = "InformedConsentForHIVTestingGiven")
    protected Boolean informedConsentForHIVTestingGiven;
    @XmlElement(name = "TimeOfLastHIVNegativeTest")
    protected String timeOfLastHIVNegativeTest;

    /**
     * Gets the value of the previouslyTestedHIVNegative property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isPreviouslyTestedHIVNegative() {
        return previouslyTestedHIVNegative;
    }

    /**
     * Sets the value of the previouslyTestedHIVNegative property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setPreviouslyTestedHIVNegative(Boolean value) {
        this.previouslyTestedHIVNegative = value;
    }

    /**
     * Gets the value of the clientInformedAboutHIVTransmissionRoutes property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientInformedAboutHIVTransmissionRoutes() {
        return clientInformedAboutHIVTransmissionRoutes;
    }

    /**
     * Sets the value of the clientInformedAboutHIVTransmissionRoutes property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientInformedAboutHIVTransmissionRoutes(Boolean value) {
        this.clientInformedAboutHIVTransmissionRoutes = value;
    }

    /**
     * Gets the value of the clientPregnant property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientPregnant() {
        return clientPregnant;
    }

    /**
     * Sets the value of the clientPregnant property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientPregnant(Boolean value) {
        this.clientPregnant = value;
    }

    /**
     * Gets the value of the clientInformedOfHIVTransmissionRiskFactors property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientInformedOfHIVTransmissionRiskFactors() {
        return clientInformedOfHIVTransmissionRiskFactors;
    }

    /**
     * Sets the value of the clientInformedOfHIVTransmissionRiskFactors property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientInformedOfHIVTransmissionRiskFactors(Boolean value) {
        this.clientInformedOfHIVTransmissionRiskFactors = value;
    }

    /**
     * Gets the value of the clientInformedAboutPreventingHIV property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientInformedAboutPreventingHIV() {
        return clientInformedAboutPreventingHIV;
    }

    /**
     * Sets the value of the clientInformedAboutPreventingHIV property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientInformedAboutPreventingHIV(Boolean value) {
        this.clientInformedAboutPreventingHIV = value;
    }

    /**
     * Gets the value of the clientInformedAboutPossibleTestResults property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientInformedAboutPossibleTestResults() {
        return clientInformedAboutPossibleTestResults;
    }

    /**
     * Sets the value of the clientInformedAboutPossibleTestResults property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientInformedAboutPossibleTestResults(Boolean value) {
        this.clientInformedAboutPossibleTestResults = value;
    }

    /**
     * Gets the value of the informedConsentForHIVTestingGiven property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isInformedConsentForHIVTestingGiven() {
        return informedConsentForHIVTestingGiven;
    }

    /**
     * Sets the value of the informedConsentForHIVTestingGiven property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setInformedConsentForHIVTestingGiven(Boolean value) {
        this.informedConsentForHIVTestingGiven = value;
    }

    /**
     * Gets the value of the timeOfLastHIVNegativeTest property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTimeOfLastHIVNegativeTest() {
        return timeOfLastHIVNegativeTest;
    }

    /**
     * Sets the value of the timeOfLastHIVNegativeTest property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTimeOfLastHIVNegativeTest(String value) {
        this.timeOfLastHIVNegativeTest = value;
    }

}
