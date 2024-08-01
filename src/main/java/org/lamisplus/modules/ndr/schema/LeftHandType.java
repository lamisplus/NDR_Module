
package org.lamisplus.modules.ndr.schema;

import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for leftHandType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="leftHandType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LeftThumb" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeftThumbQuality" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="LeftIndex" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeftIndexQuality" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="LeftMiddle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeftMiddleQuality" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="LeftWedding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeftWeddingQuality" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="LeftSmall" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeftSmallQuality" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "leftHandType", propOrder = {
    "leftThumb",
    "leftThumbQuality",
    "leftIndex",
    "leftIndexQuality",
    "leftMiddle",
    "leftMiddleQuality",
    "leftWedding",
    "leftWeddingQuality",
    "leftSmall",
    "leftSmallQuality"
})
@ToString
public class LeftHandType {

    @XmlElement(name = "LeftThumb")
    protected String leftThumb;
    @XmlElement(name = "LeftThumbQuality")
    protected Integer leftThumbQuality;
    @XmlElement(name = "LeftIndex")
    protected String leftIndex;
    @XmlElement(name = "LeftIndexQuality")
    protected Integer leftIndexQuality;
    @XmlElement(name = "LeftMiddle")
    protected String leftMiddle;
    @XmlElement(name = "LeftMiddleQuality")
    protected Integer leftMiddleQuality;
    @XmlElement(name = "LeftWedding")
    protected String leftWedding;
    @XmlElement(name = "LeftWeddingQuality")
    protected Integer leftWeddingQuality;
    @XmlElement(name = "LeftSmall")
    protected String leftSmall;
    @XmlElement(name = "LeftSmallQuality")
    protected Integer leftSmallQuality;

    /**
     * Gets the value of the leftThumb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftThumb() {
        return leftThumb;
    }

    /**
     * Sets the value of the leftThumb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftThumb(String value) {
        this.leftThumb = value;
    }

    /**
     * Gets the value of the leftThumbQuality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLeftThumbQuality() {
        return leftThumbQuality;
    }

    /**
     * Sets the value of the leftThumbQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftThumbQuality(Integer value) {
        this.leftThumbQuality = value;
    }

    /**
     * Gets the value of the leftIndex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftIndex() {
        return leftIndex;
    }

    /**
     * Sets the value of the leftIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftIndex(String value) {
        this.leftIndex = value;
    }

    /**
     * Gets the value of the leftIndexQuality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLeftIndexQuality() {
        return leftIndexQuality;
    }

    /**
     * Sets the value of the leftIndexQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftIndexQuality(Integer value) {
        this.leftIndexQuality = value;
    }

    /**
     * Gets the value of the leftMiddle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftMiddle() {
        return leftMiddle;
    }

    /**
     * Sets the value of the leftMiddle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftMiddle(String value) {
        this.leftMiddle = value;
    }

    /**
     * Gets the value of the leftMiddleQuality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLeftMiddleQuality() {
        return leftMiddleQuality;
    }

    /**
     * Sets the value of the leftMiddleQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftMiddleQuality(Integer value) {
        this.leftMiddleQuality = value;
    }

    /**
     * Gets the value of the leftWedding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftWedding() {
        return leftWedding;
    }

    /**
     * Sets the value of the leftWedding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftWedding(String value) {
        this.leftWedding = value;
    }

    /**
     * Gets the value of the leftWeddingQuality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLeftWeddingQuality() {
        return leftWeddingQuality;
    }

    /**
     * Sets the value of the leftWeddingQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftWeddingQuality(Integer value) {
        this.leftWeddingQuality = value;
    }

    /**
     * Gets the value of the leftSmall property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLeftSmall() {
        return leftSmall;
    }

    /**
     * Sets the value of the leftSmall property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLeftSmall(String value) {
        this.leftSmall = value;
    }

    /**
     * Gets the value of the leftSmallQuality property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLeftSmallQuality() {
        return leftSmallQuality;
    }

    /**
     * Sets the value of the leftSmallQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLeftSmallQuality(Integer value) {
        this.leftSmallQuality = value;
    }

}
