
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MessageHeader" type="{}MessageHeaderType"/>
 *         &lt;choice>
 *           &lt;element name="IndividualReport" type="{}IndividualReportType"/>
 *         &lt;/choice>
 *         &lt;element name="Validation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "messageHeader",
    "individualReport",
    "validation"
})
@XmlRootElement(name = "Container")
public class Container {
    @Override
    public String toString() {
        return "Container{" +
                "messageHeader=" + messageHeader +
                ", individualReport=" + individualReport +
                ", validation='" + validation + '\'' +
                '}';
    }
    
    @XmlElement(name = "MessageHeader", required = true)
    protected MessageHeaderType messageHeader;
    @XmlElement(name = "IndividualReport")
    protected IndividualReportType individualReport;
    @XmlElement(name = "Validation")
    protected String validation;

    /**
     * Gets the value of the messageHeader property.
     * 
     * @return
     *     possible object is
     *     {@link MessageHeaderType }
     *     
     */
    public MessageHeaderType getMessageHeader() {
        return messageHeader;
    }

    /**
     * Sets the value of the messageHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageHeaderType }
     *     
     */
    public void setMessageHeader(MessageHeaderType value) {
        this.messageHeader = value;
    }

    /**
     * Gets the value of the individualReport property.
     * 
     * @return
     *     possible object is
     *     {@link IndividualReportType }
     *     
     */
    public IndividualReportType getIndividualReport() {
        return individualReport;
    }

    /**
     * Sets the value of the individualReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndividualReportType }
     *     
     */
    public void setIndividualReport(IndividualReportType value) {
        this.individualReport = value;
    }

    /**
     * Gets the value of the validation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidation() {
        return validation;
    }

    /**
     * Sets the value of the validation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidation(String value) {
        this.validation = value;
    }

}
