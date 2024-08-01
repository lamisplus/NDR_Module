
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for HealthFacilityVisitsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HealthFacilityVisitsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VisitDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="VisitStatus" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="A"/>
 *               &lt;enumeration value="TI"/>
 *               &lt;enumeration value="TO"/>
 *               &lt;enumeration value="L"/>
 *               &lt;enumeration value="DC"/>
 *               &lt;enumeration value="X"/>
 *               &lt;enumeration value="LTFU"/>
 *               &lt;enumeration value="D"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Cotrimoxazole" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="N"/>
 *               &lt;enumeration value="U"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Weight" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="BreastFeeding" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="N"/>
 *               &lt;enumeration value="U"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PrescribedRegimen" type="{}CodedSimpleType" minOccurs="0"/>
 *         &lt;element name="PrescribedRegimenLineCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="10"/>
 *               &lt;enumeration value="11"/>
 *               &lt;enumeration value="20"/>
 *               &lt;enumeration value="21"/>
 *               &lt;enumeration value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="MaternalOutcome" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Alive"/>
 *               &lt;enumeration value="Dead"/>
 *               &lt;enumeration value="A"/>
 *               &lt;enumeration value="TO"/>
 *               &lt;enumeration value="TP"/>
 *               &lt;enumeration value="TA"/>
 *               &lt;enumeration value="LTFU"/>
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
@XmlType(name = "HealthFacilityVisitsType", propOrder = {
    "visitDate",
    "visitStatus",
    "cotrimoxazole",
    "weight",
    "breastFeeding",
    "prescribedRegimen",
    "prescribedRegimenLineCode",
    "maternalOutcome"
})
public class HealthFacilityVisitsType {

    @XmlElement(name = "VisitDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar visitDate;
    @XmlElement(name = "VisitStatus")
    protected String visitStatus;
    @XmlElement(name = "Cotrimoxazole")
    protected String cotrimoxazole;
    @XmlElement(name = "Weight")
    protected Integer weight;
    @XmlElement(name = "BreastFeeding")
    protected String breastFeeding;
    @XmlElement(name = "PrescribedRegimen")
    protected CodedSimpleType prescribedRegimen;
    @XmlElement(name = "PrescribedRegimenLineCode")
    protected String prescribedRegimenLineCode;
    @XmlElement(name = "MaternalOutcome")
    protected String maternalOutcome;

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
     * Gets the value of the visitStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisitStatus() {
        return visitStatus;
    }

    /**
     * Sets the value of the visitStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisitStatus(String value) {
        this.visitStatus = value;
    }

    /**
     * Gets the value of the cotrimoxazole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCotrimoxazole() {
        return cotrimoxazole;
    }

    /**
     * Sets the value of the cotrimoxazole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCotrimoxazole(String value) {
        this.cotrimoxazole = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWeight(Integer value) {
        this.weight = value;
    }

    /**
     * Gets the value of the breastFeeding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBreastFeeding() {
        return breastFeeding;
    }

    /**
     * Sets the value of the breastFeeding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBreastFeeding(String value) {
        this.breastFeeding = value;
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
     * Gets the value of the maternalOutcome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaternalOutcome() {
        return maternalOutcome;
    }

    /**
     * Sets the value of the maternalOutcome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaternalOutcome(String value) {
        this.maternalOutcome = value;
    }

}
