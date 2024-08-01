
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ChildBirthDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChildBirthDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ChildHospitalNumber" type="{}StringType"/>
 *         &lt;element name="ChildEIDNumber" type="{}StringType" minOccurs="0"/>
 *         &lt;element name="ChildDateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="ChildSexCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="F"/>
 *               &lt;enumeration value="M"/>
 *               &lt;enumeration value="A"/>
 *               &lt;enumeration value="N"/>
 *               &lt;enumeration value="O"/>
 *               &lt;enumeration value="U"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ChildGivenNVPWithin72hrs" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="N"/>
 *               &lt;enumeration value="U"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ChildStatus">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Alive"/>
 *               &lt;enumeration value="SB"/>
 *               &lt;enumeration value="NND"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="APGARScore" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="BirthMUAC" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="BirthLenght" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="BirthWeight" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="HeadCircumferenceAtBirth" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="ImmunizationReceived" type="{}StringType" minOccurs="0"/>
 *         &lt;element name="HBVExposedInfantGivenHepBIg" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="N"/>
 *               &lt;enumeration value="U"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NonHBVExposedInfantGivenHBV" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}CodeType">
 *               &lt;enumeration value="Y"/>
 *               &lt;enumeration value="N"/>
 *               &lt;enumeration value="U"/>
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
@XmlType(name = "ChildBirthDetailsType", propOrder = {
    "childHospitalNumber",
    "childEIDNumber",
    "childDateOfBirth",
    "childSexCode",
    "childGivenNVPWithin72Hrs",
    "childStatus",
    "apgarScore",
    "birthMUAC",
    "birthLenght",
    "birthWeight",
    "headCircumferenceAtBirth",
    "immunizationReceived",
    "hbvExposedInfantGivenHepBIg",
    "nonHBVExposedInfantGivenHBV"
})
public class ChildBirthDetailsType {

    @XmlElement(name = "ChildHospitalNumber", required = true)
    protected String childHospitalNumber;
    @XmlElement(name = "ChildEIDNumber")
    protected String childEIDNumber;
    @XmlElement(name = "ChildDateOfBirth", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar childDateOfBirth;
    @XmlElement(name = "ChildSexCode", required = true)
    protected String childSexCode;
    @XmlElement(name = "ChildGivenNVPWithin72hrs")
    protected String childGivenNVPWithin72Hrs;
    @XmlElement(name = "ChildStatus", required = true)
    protected String childStatus;
    @XmlElement(name = "APGARScore")
    protected Float apgarScore;
    @XmlElement(name = "BirthMUAC")
    protected Float birthMUAC;
    @XmlElement(name = "BirthLenght")
    protected Float birthLenght;
    @XmlElement(name = "BirthWeight")
    protected Float birthWeight;
    @XmlElement(name = "HeadCircumferenceAtBirth")
    protected Float headCircumferenceAtBirth;
    @XmlElement(name = "ImmunizationReceived")
    protected String immunizationReceived;
    @XmlElement(name = "HBVExposedInfantGivenHepBIg")
    protected String hbvExposedInfantGivenHepBIg;
    @XmlElement(name = "NonHBVExposedInfantGivenHBV")
    protected String nonHBVExposedInfantGivenHBV;

    /**
     * Gets the value of the childHospitalNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildHospitalNumber() {
        return childHospitalNumber;
    }

    /**
     * Sets the value of the childHospitalNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildHospitalNumber(String value) {
        this.childHospitalNumber = value;
    }

    /**
     * Gets the value of the childEIDNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildEIDNumber() {
        return childEIDNumber;
    }

    /**
     * Sets the value of the childEIDNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildEIDNumber(String value) {
        this.childEIDNumber = value;
    }

    /**
     * Gets the value of the childDateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getChildDateOfBirth() {
        return childDateOfBirth;
    }

    /**
     * Sets the value of the childDateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setChildDateOfBirth(XMLGregorianCalendar value) {
        this.childDateOfBirth = value;
    }

    /**
     * Gets the value of the childSexCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildSexCode() {
        return childSexCode;
    }

    /**
     * Sets the value of the childSexCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildSexCode(String value) {
        this.childSexCode = value;
    }

    /**
     * Gets the value of the childGivenNVPWithin72Hrs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildGivenNVPWithin72Hrs() {
        return childGivenNVPWithin72Hrs;
    }

    /**
     * Sets the value of the childGivenNVPWithin72Hrs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildGivenNVPWithin72Hrs(String value) {
        this.childGivenNVPWithin72Hrs = value;
    }

    /**
     * Gets the value of the childStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildStatus() {
        return childStatus;
    }

    /**
     * Sets the value of the childStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildStatus(String value) {
        this.childStatus = value;
    }

    /**
     * Gets the value of the apgarScore property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getAPGARScore() {
        return apgarScore;
    }

    /**
     * Sets the value of the apgarScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setAPGARScore(Float value) {
        this.apgarScore = value;
    }

    /**
     * Gets the value of the birthMUAC property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBirthMUAC() {
        return birthMUAC;
    }

    /**
     * Sets the value of the birthMUAC property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBirthMUAC(Float value) {
        this.birthMUAC = value;
    }

    /**
     * Gets the value of the birthLenght property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBirthLenght() {
        return birthLenght;
    }

    /**
     * Sets the value of the birthLenght property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBirthLenght(Float value) {
        this.birthLenght = value;
    }

    /**
     * Gets the value of the birthWeight property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBirthWeight() {
        return birthWeight;
    }

    /**
     * Sets the value of the birthWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBirthWeight(Float value) {
        this.birthWeight = value;
    }

    /**
     * Gets the value of the headCircumferenceAtBirth property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getHeadCircumferenceAtBirth() {
        return headCircumferenceAtBirth;
    }

    /**
     * Sets the value of the headCircumferenceAtBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setHeadCircumferenceAtBirth(Float value) {
        this.headCircumferenceAtBirth = value;
    }

    /**
     * Gets the value of the immunizationReceived property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImmunizationReceived() {
        return immunizationReceived;
    }

    /**
     * Sets the value of the immunizationReceived property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImmunizationReceived(String value) {
        this.immunizationReceived = value;
    }

    /**
     * Gets the value of the hbvExposedInfantGivenHepBIg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHBVExposedInfantGivenHepBIg() {
        return hbvExposedInfantGivenHepBIg;
    }

    /**
     * Sets the value of the hbvExposedInfantGivenHepBIg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHBVExposedInfantGivenHepBIg(String value) {
        this.hbvExposedInfantGivenHepBIg = value;
    }

    /**
     * Gets the value of the nonHBVExposedInfantGivenHBV property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNonHBVExposedInfantGivenHBV() {
        return nonHBVExposedInfantGivenHBV;
    }

    /**
     * Sets the value of the nonHBVExposedInfantGivenHBV property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNonHBVExposedInfantGivenHBV(String value) {
        this.nonHBVExposedInfantGivenHBV = value;
    }

}
