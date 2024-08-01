package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.ndr.domain.entities.NDRDATA;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.domain.dto.ARTClinicalInfo;
import org.lamisplus.modules.ndr.domain.dto.ArtCommencementDTO;
import org.lamisplus.modules.ndr.domain.dto.LabDTO;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.repositories.NDRDATARepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootData {
	
	private final NDRCodeSetRepository nDRCodeSetRepository;
	private final NDRDATARepository nDRDATARepository;
	private final NdrXmlStatusRepository ndrXmlStatusRepository;
	private final ArtPharmacyRepository pharmacyRepository;
	private final PersonRepository personRepository;
	
	
	public void init(List<String> patientIds) {
		log.info("processing ndr patients data  total size {} .... please wait...  ", patientIds.size());
		ObjectMapper map = new ObjectMapper();
		final int[] count = {1};
		
		patientIds.stream()
				.parallel()
				.forEach(patientId -> {
					try {
						log.info(" patient id {}",  patientId);
						Optional<PatientDemographics> demographic
								= ndrXmlStatusRepository.getPatientDemographicsByUUID(patientId);
						log.info("demographic is present {}", demographic.isPresent());
						demographic.ifPresent(person -> {
							log.info("start processing {} out {}", count[0], patientIds.size());
							
							List<ARTClinicalInfo> clinicalInfos =
									ndrXmlStatusRepository.getClinicalInfoByPersonUuid(patientId);
							
							List<LabDTO> labs =
									ndrXmlStatusRepository.getLabInfoByPersonUuid(patientId, LocalDateTime.now());
							
							Optional<Person> existPerson =
									personRepository.getPersonByUuidAndFacilityIdAndArchived(patientId, person.getFacilityId(), 0);
							
							List<ArtPharmacy> pharmacies =
									pharmacyRepository.getArtPharmaciesByPersonAndArchived(existPerson.get(), 0);
							
							Optional<ArtCommencementDTO> commencement =
									nDRCodeSetRepository.getArtCommencementByPatientUuid(patientId);
							NDRDATA ndrdata =
									buildData(map, person, clinicalInfos, labs, pharmacies, commencement.get());
							nDRDATARepository.save(ndrdata);
							log.info("done processing {} out {}", count[0], patientIds.size());
							count[0]++;
						});
					} catch (Exception e) {
						log.error("An error occurred while processing patient uuid: {}  error {}", patientId, e.getMessage());
					}
				});
	}
	
	
		
	
	
	private static NDRDATA  buildData(
			ObjectMapper map,
			PatientDemographics person,
			List<ARTClinicalInfo> clinicalInfos,
			List<LabDTO> labs, List<ArtPharmacy> pharmacies,
			ArtCommencementDTO commencement) {
		return NDRDATA.builder()
				.patientId(person.getPersonUuid())
				.commencement(map.valueToTree(commencement))
				.careCards(map.valueToTree(clinicalInfos))
				.demorgraphic(map.valueToTree(person))
				.labs(map.valueToTree(labs))
				.pharmacies(map.valueToTree(pharmacies))
				.dateModified(LocalDate.now())
				.build();
	}
}
