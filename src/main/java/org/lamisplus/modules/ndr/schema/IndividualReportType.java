
package org.lamisplus.modules.ndr.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for IndividualReportType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IndividualReportType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PatientDemographics" type="{}PatientDemographicsType"/&gt;
 *         &lt;element name="Condition" type="{}ConditionType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="HIVTestingReport" type="{}HIVTestingReportType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PMTCT" type="{}PMTCTType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Mortality" type="{}MortalityType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Recency" type="{}RecencyType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PrEP_PEP" type="{}PrEPType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndividualReportType", propOrder = {
        "patientDemographics",
        "condition",
        "hivTestingReport",
        "pmtct",
        "mortality",
        "recency",
        "prEPPEP"
})
public class IndividualReportType {
// add the needed class in the pre tag and propOrder
    @XmlElement(name = "PatientDemographics", required = true)
    protected PatientDemographicsType patientDemographics;
    @XmlElement(name = "Condition", required = true)
    protected List<ConditionType> condition;
    @XmlElement(name = "HIVTestingReport")
    protected List<HIVTestingReportType> hivTestingReport;
    @XmlElement(name = "PMTCT")
    protected List<PMTCTType> pmtct;
    @XmlElement(name = "Mortality")
    protected List<MortalityType> mortality;
    @XmlElement(name = "Recency")
    protected List<RecencyType> recency;
    @XmlElement(name = "PrEP_PEP")
    protected PrEPType prEPPEP;

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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pmtct property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPMTCT().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PMTCTType }
     *
     *
     */
    public List<PMTCTType> getPMTCT() {
        if (pmtct == null) {
            pmtct = new ArrayList<PMTCTType>();
        }
        return this.pmtct;
    }

    /**
     * Gets the value of the tb property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tb property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTB().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrEPType }
     *
     *
     */
//    public List<PrEPType> getTB() {
//        if (tb == null) {
//            tb = new ArrayList<PrEPType>();
//        }
//        return this.tb;
//    }

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

    /**
     * Gets the value of the recency property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recency property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecency().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecencyType }
     *
     *
     */
    public List<RecencyType> getRecency() {
        if (recency == null) {
            recency = new ArrayList<RecencyType>();
        }
        return this.recency;
    }

    /**
     * Gets the value of the viralHepatitis property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viralHepatitis property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViralHepatitis().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ViralHepatitisType }
     *
     *
     */
//    public List<ViralHepatitisType> getViralHepatitis() {
//        if (viralHepatitis == null) {
//            viralHepatitis = new ArrayList<ViralHepatitisType>();
//        }
//        return this.viralHepatitis;
//    }

    /**
     * Gets the value of the stiEntry property.
     *
     * @return
     *     possible object is
     *     {@link STIEntryType }
     *
     */
//    public STIEntryType getSTIEntry() {
//        return stiEntry;
//    }

    /**
     * Sets the value of the stiEntry property.
     *
     * @param value
     *     allowed object is
     *     {@link STIEntryType }
     *
     */
//    public void setSTIEntry(STIEntryType value) {
//        this.stiEntry = value;
//    }

    /**
     * Gets the value of the prEPPEP property.
     *
     * @return
     *     possible object is
     *     {@link PrEPType }
     *
     */
    public PrEPType getPrEPPEP() {
        return prEPPEP;
    }

    /**
     * Sets the value of the prEPPEP property.
     *
     * @param value
     *     allowed object is
     *     {@link PrEPType }
     *
     */
    public void setPrEPPEP(PrEPType value) {
        this.prEPPEP = value;
    }

}
