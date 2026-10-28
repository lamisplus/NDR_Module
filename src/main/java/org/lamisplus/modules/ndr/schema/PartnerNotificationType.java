package org.lamisplus.modules.ndr.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartnerNotificationType", propOrder = {
    "agreedToNotification",
    "partnerTestedHIV",
    "partnerTestedSyphilis",
    "partnerTestedHepatitiB",
    "referredToART",
    "referredToFamilyPlanning",
    "referredToPrEP",
    "referredToOther"
})
public class PartnerNotificationType {

    @XmlElement(name = "AgreedToNotification")
    protected Boolean agreedToNotification;
    @XmlElement(name = "PartnerTestedHIV")
    protected String partnerTestedHIV;
    @XmlElement(name = "PartnerTestedSyphilis")
    protected String partnerTestedSyphilis;
    @XmlElement(name = "PartnerTestedHepatitiB")
    protected String partnerTestedHepatitiB;
    @XmlElement(name = "ReferredToART")
    protected Boolean referredToART;
    @XmlElement(name = "ReferredToFamilyPlanning")
    protected Boolean referredToFamilyPlanning;
    @XmlElement(name = "ReferredToPrEP")
    protected Boolean referredToPrEP;
    @XmlElement(name = "ReferredToOther")
    protected Boolean referredToOther;

    @XmlTransient
    protected String partnername;
    @XmlTransient
    protected String partnerGender;
    @XmlTransient
    protected String indexRelation;
    @XmlTransient
    protected String descriptiveAddress;
    @XmlTransient
    protected String phoneNumber;

    public Boolean isAgreedToNotification() {
        return agreedToNotification;
    }

    public void setAgreedToNotification(Boolean value) {
        this.agreedToNotification = value;
    }

    public String getPartnerTestedHIV() {
        return partnerTestedHIV;
    }

    public void setPartnerTestedHIV(String value) {
        this.partnerTestedHIV = value;
    }

    public String getPartnerTestedSyphilis() {
        return partnerTestedSyphilis;
    }

    public void setPartnerTestedSyphilis(String value) {
        this.partnerTestedSyphilis = value;
    }

    public String getPartnerTestedHepatitiB() {
        return partnerTestedHepatitiB;
    }

    public void setPartnerTestedHepatitiB(String value) {
        this.partnerTestedHepatitiB = value;
    }

    public Boolean isReferredToART() {
        return referredToART;
    }

    public void setReferredToART(Boolean value) {
        this.referredToART = value;
    }

    public Boolean isReferredToFamilyPlanning() {
        return referredToFamilyPlanning;
    }

    public void setReferredToFamilyPlanning(Boolean value) {
        this.referredToFamilyPlanning = value;
    }

    public Boolean isReferredToPrEP() {
        return referredToPrEP;
    }

    public void setReferredToPrEP(Boolean value) {
        this.referredToPrEP = value;
    }

    public Boolean isReferredToOther() {
        return referredToOther;
    }

    public void setReferredToOther(Boolean value) {
        this.referredToOther = value;
    }

    public String getPartnername() {
        return partnername;
    }

    public void setPartnername(String value) {
        this.partnername = value;
    }

    public String getPartnerGender() {
        return partnerGender;
    }

    public void setPartnerGender(String value) {
        this.partnerGender = value;
    }

    public String getIndexRelation() {
        return indexRelation;
    }

    public void setIndexRelation(String value) {
        this.indexRelation = value;
    }

    public String getDescriptiveAddress() {
        return descriptiveAddress;
    }

    public void setDescriptiveAddress(String value) {
        this.descriptiveAddress = value;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

}
