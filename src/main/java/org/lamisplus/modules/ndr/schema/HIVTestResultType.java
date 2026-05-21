package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HIVTestResultType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="HIVTestResultType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TestResult" type="{}TestResultType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HIVTestResultType", propOrder = {
        "testResult"
})
public class HIVTestResultType {

    @XmlElement(name = "TestResult")
    protected TestResultType testResult;

    /**
     * Gets the value of the testResult property.
     *
     * @return
     *     possible object is
     *     {@link TestResultType }
     *
     */
    public TestResultType getTestResult() {
        return testResult;
    }

    /**
     * Sets the value of the testResult property.
     *
     * @param value
     *     allowed object is
     *     {@link TestResultType }
     *
     */
    public void setTestResult(TestResultType value) {
        this.testResult = value;
    }

}
