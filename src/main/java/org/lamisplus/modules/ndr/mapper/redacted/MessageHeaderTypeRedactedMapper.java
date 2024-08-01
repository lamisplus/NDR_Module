package org.lamisplus.modules.ndr.mapper.redacted;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.dto.PatientRedactedDemographicDTO;
import org.lamisplus.modules.ndr.schema.redacted.MessageHeaderType;
import org.lamisplus.modules.ndr.schema.redacted.MessageSendingOrganisationType;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MessageHeaderTypeRedactedMapper {
    public MessageHeaderType getMessageHeader(PatientRedactedDemographicDTO demographics) {
        MessageHeaderType header = new MessageHeaderType ();
        try {
            header.setMessageCreationDateTime (DateUtil.getXmlDate (new Date ()));
            header.setMessageUniqueID(demographics.getPatientIdentifier() + "redacted");
            header.setXmlType("REDACTED");
            header.setMessageVersion (new BigDecimal ("1.0"));
            MessageSendingOrganisationType sendingOrganization = getTreatmentFacility (demographics);
            header.setMessageSendingOrganisation (sendingOrganization);

            return header;
        } catch (Exception exception) {
            exception.printStackTrace ();
        }
        return null;
    }


    public MessageSendingOrganisationType getTreatmentFacility(PatientRedactedDemographicDTO demographics) {
        MessageSendingOrganisationType facility = new MessageSendingOrganisationType ();

        System.out.printf("facilityName: "+ demographics.getFacilityName());
        System.out.printf("facilityID: "+ demographics.getFacilityId());

        if(demographics.getFacilityName() != null )facility.setFacilityName (demographics.getFacilityName());
        if(demographics.getFacilityId() != null) facility.setFacilityID(demographics.getFacilityId());
        facility.setFacilityTypeCode ("FAC");
        return facility;
    }

}
