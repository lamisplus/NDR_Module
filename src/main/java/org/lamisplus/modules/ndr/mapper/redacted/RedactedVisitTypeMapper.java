package org.lamisplus.modules.ndr.mapper.redacted;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.PatientRedactedDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.RedactedVisitTypeDTO;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.schema.redacted.RedactedVisitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedactedVisitTypeMapper {
    @Autowired
    private NdrMessageLogRepository ndrMessageLogRepository;
    public RedactedVisitType getPatientRedactedVisits(PatientRedactedDemographicDTO patientRedactedDemographicDTO) {
        try{
            RedactedVisitType redactedVisitType = new RedactedVisitType();

            List<RedactedVisitTypeDTO> visits = ndrMessageLogRepository.getRedactedPatientVisits(patientRedactedDemographicDTO.getPersonUuid());

            if (visits != null && visits.size() != 0) {
                log.info("Patient with ID {} has {} visits", patientRedactedDemographicDTO.getPersonUuid(), visits.size());
                visits.forEach(visit -> {
                    if(visit.getVisitID() != null) {
                        redactedVisitType.setVisitID(visit.getVisitID());
                    }else {
                        throw new IllegalArgumentException("Redacted Patient Visit ID cannot be null");
                    }

                    if(StringUtils.isNotBlank(visit.getReason())) {
                        redactedVisitType.setRedactedVisitReason(visit.getReason());
                    }else {
                        redactedVisitType.setRedactedVisitReason("Patient Clinical Visit Deleted");
                    }
                });

                return redactedVisitType;
            }
            return null;
        }
        catch (Exception e) {
            log.info("An error occurred getting redacted patient visits with uuid {} : {}",
                    patientRedactedDemographicDTO.getPersonUuid(), e.getMessage());
            throw new IllegalStateException(e.toString());
        }
    }
}
