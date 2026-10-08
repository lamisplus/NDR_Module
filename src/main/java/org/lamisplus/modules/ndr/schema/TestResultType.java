package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TestResultType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TestResultType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ScreeningTestResult" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="NR"/&gt;
 *               &lt;enumeration value="R"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ScreeningTestResultDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="SuspectedAcuteHIVInfection" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="Yes"/&gt;
 *               &lt;enumeration value="No"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ConfirmatoryTestResult" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="NR"/&gt;
 *               &lt;enumeration value="R"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ConfirmatoryTestResultDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="FinalTestResult" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="Pos"/&gt;
 *               &lt;enumeration value="Neg"/&gt;
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
@XmlType(name = "TestResultType", propOrder = {
        "screeningTestResult",
        "screeningTestResultDate",
        "suspectedAcuteHIVInfection",
        "confirmatoryTestResult",
        "confirmatoryTestResultDate",
        "finalTestResult"
})
public class TestResultType {

    @XmlElement(name = "ScreeningTestResult")
    protected String screeningTestResult;
    @XmlElement(name = "ScreeningTestResultDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar screeningTestResultDate;
    @XmlElement(name = "SuspectedAcuteHIVInfection")
    protected String suspectedAcuteHIVInfection;
    @XmlElement(name = "ConfirmatoryTestResult")
    protected String confirmatoryTestResult;
    @XmlElement(name = "ConfirmatoryTestResultDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar confirmatoryTestResultDate;
    @XmlElement(name = "FinalTestResult")
    protected String finalTestResult;

    /**
     * Gets the value of the screeningTestResult property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getScreeningTestResult() {
        return screeningTestResult;
    }

    /**
     * Sets the value of the screeningTestResult property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setScreeningTestResult(String value) {
        this.screeningTestResult = value;
    }

    /**
     * Gets the value of the screeningTestResultDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getScreeningTestResultDate() {
        return screeningTestResultDate;
    }

    /**
     * Sets the value of the screeningTestResultDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setScreeningTestResultDate(XMLGregorianCalendar value) {
        this.screeningTestResultDate = value;
    }

    /**
     * Gets the value of the suspectedAcuteHIVInfection property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSuspectedAcuteHIVInfection() {
        return suspectedAcuteHIVInfection;
    }

    /**
     * Sets the value of the suspectedAcuteHIVInfection property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSuspectedAcuteHIVInfection(String value) {
        this.suspectedAcuteHIVInfection = value;
    }

    /**
     * Gets the value of the confirmatoryTestResult property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getConfirmatoryTestResult() {
        return confirmatoryTestResult;
    }

    /**
     * Sets the value of the confirmatoryTestResult property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setConfirmatoryTestResult(String value) {
        this.confirmatoryTestResult = value;
    }

    /**
     * Gets the value of the confirmatoryTestResultDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getConfirmatoryTestResultDate() {
        return confirmatoryTestResultDate;
    }

    /**
     * Sets the value of the confirmatoryTestResultDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setConfirmatoryTestResultDate(XMLGregorianCalendar value) {
        this.confirmatoryTestResultDate = value;
    }

    /**
     * Gets the value of the finalTestResult property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFinalTestResult() {
        return finalTestResult;
    }

    /**
     * Sets the value of the finalTestResult property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFinalTestResult(String value) {
        this.finalTestResult = value;
    }

}
