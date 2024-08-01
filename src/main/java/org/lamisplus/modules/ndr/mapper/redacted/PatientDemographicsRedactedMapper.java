package org.lamisplus.modules.ndr.mapper.redacted;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.PatientRedactedDemographicDTO;
import org.lamisplus.modules.ndr.schema.redacted.PatientDemographicsType;
import org.lamisplus.modules.ndr.schema.redacted.RedactedVisitType;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PatientDemographicsRedactedMapper {
    private final RedactedVisitTypeMapper redactedVisitTypeMapper;
    public PatientDemographicsType getRedactedPatient(PatientRedactedDemographicDTO demographicDTO) {
        PatientDemographicsType patientDemographicsType = new PatientDemographicsType();
        try {
            RedactedVisitType redactedVisits = redactedVisitTypeMapper.getPatientRedactedVisits(demographicDTO);

                if (redactedVisits != null) {
                    if(StringUtils.isNotBlank(demographicDTO.getPatientIdentifier())) {
                        patientDemographicsType.setPatientIdentifier(demographicDTO.getPatientIdentifier());
                    }else {
                        throw new IllegalArgumentException("Patient Identifier cannot be null");
                    }

                    if(StringUtils.isNotBlank(demographicDTO.getPatientIdentifier())) {
                        patientDemographicsType.setRedactedPatient("YES");
                    }else {
                        throw new IllegalArgumentException("Redacted Patient cannot be null");
                    }

                    if(StringUtils.isNotBlank(demographicDTO.getReason())) {
                        patientDemographicsType.setRedactedPatientReason(demographicDTO.getReason());
                    }else {
                        patientDemographicsType.setRedactedPatientReason("Enrolled Patient Deleted");
                    }
                    patientDemographicsType.getRedactedVisit().add(redactedVisits);
                    return patientDemographicsType;
                }
                return null;

        } catch (Exception e) {
            log.info("An error occurred while trying to get redacted patient with uuid {} : {}",
                    demographicDTO.getPersonUuid(), e.getMessage());
            throw new IllegalStateException(e.toString());
        }
    }
}
