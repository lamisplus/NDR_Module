
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for IndividualReportType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IndividualReportType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PatientDemographics" type="{}PatientDemographicsType"/>
 *         &lt;element name="Condition" type="{}ConditionType" maxOccurs="unbounded"/>
 *         &lt;element name="HIVTestingReport" type="{}HIVTestingReportType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PMTCT" type="{}PMTCTType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndividualReportType", propOrder = {
    "patientDemographics",
    "condition",
    "hivTestingReport",
    "pmtct", "mortality",
})
public class IndividualReportType {
    @Override
    public String toString() {
        return "IndividualReportType{" +
                "patientDemographics=" + patientDemographics +
                ", condition=" + condition +
                ", hivTestingReport=" + hivTestingReport +
                ", pmtct=" + pmtct +
                ", mortality=" + mortality +
                '}';
    }
    
    @XmlElement(name = "PatientDemographics", required = true)
    protected PatientDemographicsType patientDemographics;
   
    @XmlElement(name = "Condition", required = true)
    protected List<ConditionType> condition;
    @XmlElement(name = "HIVTestingReport")
    protected List<HIVTestingReportType> hivTestingReport;
   
    @XmlElement(name = "PMTCT")
    protected PMTCTType pmtct;

    @XmlElement(name = "Mortality")
    protected List<MortalityType> mortality;

    /**
     * Gets the value of the patientDemographics property.
     * 
     * @return
     *     possible object is
     *     {@link PatientDemographicsType }
     *     
     */
    public PatientDemographicsType getPatientDemographics() {
        return patientDemographics;
    }

    /**
     * Sets the value of the patientDemographics property.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientDemographicsType }
     *     
     */
    public void setPatientDemographics(PatientDemographicsType value) {
        this.patientDemographics = value;
    }

    /**
     * Gets the value of the condition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the condition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConditionType }
     * 
     * 
     */
    public List<ConditionType> getCondition() {
        if (condition == null) {
            condition = new ArrayList<ConditionType>();
        }
        return this.condition;
    }

    /**
     * Gets the value of the hivTestingReport property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hivTestingReport property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHIVTestingReport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HIVTestingReportType }
     * 
     * 
     */
    public List<HIVTestingReportType> getHIVTestingReport() {
        if (hivTestingReport == null) {
            hivTestingReport = new ArrayList<HIVTestingReportType>();
        }
        return this.hivTestingReport;
    }

    /**
     * Gets the value of the pmtct property.
     * 
     * @return
     *     possible object is
     *     {@link PMTCTType }
     *     
     */
    public PMTCTType getPMTCT() {
        return pmtct;
    }

    /**
     * Sets the value of the pmtct property.
     * 
     * @param value
     *     allowed object is
     *     {@link PMTCTType }
     *     
     */
    public void setPMTCT(PMTCTType value) {
        this.pmtct = value;
    }
    /**
     * Gets the value of the mortality property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mortality property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMortality().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MortalityType }
     *
     *
     */
    public List<MortalityType> getMortality() {
        if (mortality == null) {
            mortality = new ArrayList<MortalityType>();
        }
        return this.mortality;
    }

}
