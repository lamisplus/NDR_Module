package org.lamisplus.modules.ndr.mapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.schema.AddressType;
import org.lamisplus.modules.ndr.service.NDRCodeSetResolverService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AddressTypeMapper {
	private final NDRCodeSetResolverService ndrCodeSetResolverService;
	
	
	
	public AddressType getPatientAddress(PatientDemographics demographics ) {
		AddressType address = new AddressType();
		address.setAddressTypeCode("H");
		address.setCountryCode("NGA");
		processAndSetPatientCurrentAddress(address, demographics);
		return address;
	}
	
	public AddressType getPatientAddress(PatientDemographicDTO demographics ) {
		AddressType address = new AddressType();
		address.setAddressTypeCode("H");
		address.setCountryCode("NGA");
		processAndSetPatientCurrentAddress(address, demographics);
		return address;
	}
	
	
	private void processAndSetPatientCurrentAddress(AddressType addressType, PatientDemographics patientDemographics) {
		log.info("Processing address...");
		try {
			if (patientDemographics.getTown() != null) {
				addressType.setTown(patientDemographics.getTown());
			}
			Optional<String> stateCode =
					ndrCodeSetResolverService.getNDRCodeSetCode("STATES", patientDemographics.getState());
			Optional<String> lgaCode =
					ndrCodeSetResolverService.getNDRCodeSetCode("LGA", patientDemographics.getLga());
			stateCode.ifPresent(addressType::setStateCode);
			lgaCode.ifPresent(addressType::setLGACode);
		} catch (Exception e) {
			log.error("An error occur why processing patient address with uuid {}", patientDemographics.getPersonUuid());
			log.error("Error message {} ", e.getMessage());
		}
		
	}



	
	private void processAndSetPatientCurrentAddress(AddressType addressType, PatientDemographicDTO patientDemographics) {
		log.info("Processing address...");
		try {
//			if (patientDemographics.!= null) {
//				addressType.setTown(patientDemographics.getTown());
//			}
			if(patientDemographics.getLgaCode() != null) {
				addressType.setLGACode(patientDemographics.getLgaCode() );
			}
			if(patientDemographics.getStateCode() != null) {
				addressType.setStateCode(patientDemographics.getStateCode());
			}
		} catch (Exception e) {
			log.error("An error occur why processing patient address with uuid {}", patientDemographics.getPersonUuid());
			log.error("Error message {} ", e.getMessage());
		}
		
	}
	
}