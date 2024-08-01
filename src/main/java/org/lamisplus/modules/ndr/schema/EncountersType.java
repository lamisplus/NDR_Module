
package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for EncountersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EncountersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HIVEncounter" type="{}HIVEncounterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncountersType", propOrder = {
    "hivEncounter"
})
@XmlRootElement
public class EncountersType {

    @XmlElement(name = "HIVEncounter")
    protected List<HIVEncounterType> hivEncounter;

    /**
     * Gets the value of the hivEncounter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hivEncounter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHIVEncounter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HIVEncounterType }
     * 
     * 
     */
    public List<HIVEncounterType> getHIVEncounter() {
        if (hivEncounter == null) {
            hivEncounter = new ArrayList<HIVEncounterType>();
        }
        return this.hivEncounter;
    }

}
