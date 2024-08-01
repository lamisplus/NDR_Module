package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.schema.IdentifierType;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class IdentifiersTypeMapper {
    public IdentifierType getIdentifiers(PatientDemographicDTO demographicDTO) {
        IdentifierType identifierType = new IdentifierType();

        identifierType.setIDNumber(demographicDTO.getPatientIdentifier());
        identifierType.setIDTypeCode("HTS");
        return identifierType;
    }

}
