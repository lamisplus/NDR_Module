package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PostTestCounsellingType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PostTestCounsellingType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TestedForHIVBeforeWithinThisYear" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="1"/&gt;
 *               &lt;enumeration value="2"/&gt;
 *               &lt;enumeration value="3"/&gt;
 *               &lt;enumeration value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AcceptedIndexTesting" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ProvidedWithInformationOnFPandDualContraception" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientOrPartnerUseFPMethodsOtherThanCondoms" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientOrPartnerUseCondomsAsOneFPMethods" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ClientRecievedHIVTestResult" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="CorrectCondomUseDemonstrated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="HIVSelfTestKitsProvided" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="HIVSelfTestKitsCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="CondomsProvidedToClient" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="CategoryOfClient" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{}CodeType"&gt;
 *               &lt;enumeration value="S"/&gt;
 *               &lt;enumeration value="P"/&gt;
 *               &lt;enumeration value="CG"/&gt;
 *               &lt;enumeration value="SN"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ClientReferredToOtherServices" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostTestCounsellingType", propOrder = {
        "testedForHIVBeforeWithinThisYear",
        "acceptedIndexTesting",
        "providedWithInformationOnFPandDualContraception",
        "clientOrPartnerUseFPMethodsOtherThanCondoms",
        "clientOrPartnerUseCondomsAsOneFPMethods",
        "clientRecievedHIVTestResult",
        "correctCondomUseDemonstrated",
        "hivSelfTestKitsProvided",
        "hivSelfTestKitsCount",
        "condomsProvidedToClient",
        "categoryOfClient",
        "clientReferredToOtherServices"
})
public class PostTestCounsellingType {

    @XmlElement(name = "TestedForHIVBeforeWithinThisYear")
    protected String testedForHIVBeforeWithinThisYear;
    @XmlElement(name = "AcceptedIndexTesting")
    protected Boolean acceptedIndexTesting;
    @XmlElement(name = "ProvidedWithInformationOnFPandDualContraception")
    protected Boolean providedWithInformationOnFPandDualContraception;
    @XmlElement(name = "ClientOrPartnerUseFPMethodsOtherThanCondoms")
    protected Boolean clientOrPartnerUseFPMethodsOtherThanCondoms;
    @XmlElement(name = "ClientOrPartnerUseCondomsAsOneFPMethods")
    protected Boolean clientOrPartnerUseCondomsAsOneFPMethods;
    @XmlElement(name = "ClientRecievedHIVTestResult")
    protected Boolean clientRecievedHIVTestResult;
    @XmlElement(name = "CorrectCondomUseDemonstrated")
    protected Boolean correctCondomUseDemonstrated;
    @XmlElement(name = "HIVSelfTestKitsProvided")
    protected Boolean hivSelfTestKitsProvided;
    @XmlElement(name = "HIVSelfTestKitsCount")
    protected Integer hivSelfTestKitsCount;
    @XmlElement(name = "CondomsProvidedToClient")
    protected Boolean condomsProvidedToClient;
    @XmlElement(name = "CategoryOfClient")
    protected String categoryOfClient;
    @XmlElement(name = "ClientReferredToOtherServices")
    protected Boolean clientReferredToOtherServices;

    /**
     * Gets the value of the testedForHIVBeforeWithinThisYear property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTestedForHIVBeforeWithinThisYear() {
        return testedForHIVBeforeWithinThisYear;
    }

    /**
     * Sets the value of the testedForHIVBeforeWithinThisYear property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTestedForHIVBeforeWithinThisYear(String value) {
        this.testedForHIVBeforeWithinThisYear = value;
    }

    /**
     * Gets the value of the acceptedIndexTesting property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isAcceptedIndexTesting() {
        return acceptedIndexTesting;
    }

    /**
     * Sets the value of the acceptedIndexTesting property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setAcceptedIndexTesting(Boolean value) {
        this.acceptedIndexTesting = value;
    }

    /**
     * Gets the value of the providedWithInformationOnFPandDualContraception property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isProvidedWithInformationOnFPandDualContraception() {
        return providedWithInformationOnFPandDualContraception;
    }

    /**
     * Sets the value of the providedWithInformationOnFPandDualContraception property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setProvidedWithInformationOnFPandDualContraception(Boolean value) {
        this.providedWithInformationOnFPandDualContraception = value;
    }

    /**
     * Gets the value of the clientOrPartnerUseFPMethodsOtherThanCondoms property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientOrPartnerUseFPMethodsOtherThanCondoms() {
        return clientOrPartnerUseFPMethodsOtherThanCondoms;
    }

    /**
     * Sets the value of the clientOrPartnerUseFPMethodsOtherThanCondoms property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientOrPartnerUseFPMethodsOtherThanCondoms(Boolean value) {
        this.clientOrPartnerUseFPMethodsOtherThanCondoms = value;
    }

    /**
     * Gets the value of the clientOrPartnerUseCondomsAsOneFPMethods property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientOrPartnerUseCondomsAsOneFPMethods() {
        return clientOrPartnerUseCondomsAsOneFPMethods;
    }

    /**
     * Sets the value of the clientOrPartnerUseCondomsAsOneFPMethods property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientOrPartnerUseCondomsAsOneFPMethods(Boolean value) {
        this.clientOrPartnerUseCondomsAsOneFPMethods = value;
    }

    /**
     * Gets the value of the clientRecievedHIVTestResult property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientRecievedHIVTestResult() {
        return clientRecievedHIVTestResult;
    }

    /**
     * Sets the value of the clientRecievedHIVTestResult property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientRecievedHIVTestResult(Boolean value) {
        this.clientRecievedHIVTestResult = value;
    }

    /**
     * Gets the value of the correctCondomUseDemonstrated property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isCorrectCondomUseDemonstrated() {
        return correctCondomUseDemonstrated;
    }

    /**
     * Sets the value of the correctCondomUseDemonstrated property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setCorrectCondomUseDemonstrated(Boolean value) {
        this.correctCondomUseDemonstrated = value;
    }

    /**
     * Gets the value of the hivSelfTestKitsProvided property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isHIVSelfTestKitsProvided() {
        return hivSelfTestKitsProvided;
    }

    /**
     * Sets the value of the hivSelfTestKitsProvided property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setHIVSelfTestKitsProvided(Boolean value) {
        this.hivSelfTestKitsProvided = value;
    }

    /**
     * Gets the value of the hivSelfTestKitsCount property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getHIVSelfTestKitsCount() {
        return hivSelfTestKitsCount;
    }

    /**
     * Sets the value of the hivSelfTestKitsCount property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setHIVSelfTestKitsCount(Integer value) {
        this.hivSelfTestKitsCount = value;
    }

    /**
     * Gets the value of the condomsProvidedToClient property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isCondomsProvidedToClient() {
        return condomsProvidedToClient;
    }

    /**
     * Sets the value of the condomsProvidedToClient property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setCondomsProvidedToClient(Boolean value) {
        this.condomsProvidedToClient = value;
    }

    /**
     * Gets the value of the categoryOfClient property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCategoryOfClient() {
        return categoryOfClient;
    }

    /**
     * Sets the value of the categoryOfClient property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCategoryOfClient(String value) {
        this.categoryOfClient = value;
    }

    /**
     * Gets the value of the clientReferredToOtherServices property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isClientReferredToOtherServices() {
        return clientReferredToOtherServices;
    }

    /**
     * Sets the value of the clientReferredToOtherServices property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setClientReferredToOtherServices(Boolean value) {
        this.clientReferredToOtherServices = value;
    }

}
