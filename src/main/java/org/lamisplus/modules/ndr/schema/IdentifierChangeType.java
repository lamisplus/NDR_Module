
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IdentifierChangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IdentifierChangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PatientIdentifierChange" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="OldPatientIdentifier" type="{}StringType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifierChangeType", propOrder = {
    "patientIdentifierChange",
    "oldPatientIdentifier"
})
public class IdentifierChangeType {

    @XmlElement(name = "PatientIdentifierChange")
    protected Boolean patientIdentifierChange;
    @XmlElement(name = "OldPatientIdentifier")
    protected String oldPatientIdentifier;

    /**
     * Gets the value of the patientIdentifierChange property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPatientIdentifierChange() {
        return patientIdentifierChange;
    }

    /**
     * Sets the value of the patientIdentifierChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPatientIdentifierChange(Boolean value) {
        this.patientIdentifierChange = value;
    }

    /**
     * Gets the value of the oldPatientIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldPatientIdentifier() {
        return oldPatientIdentifier;
    }

    /**
     * Sets the value of the oldPatientIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldPatientIdentifier(String value) {
        this.oldPatientIdentifier = value;
    }

}
