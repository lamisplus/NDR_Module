package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PregnancyStatus {
	
	private final NdrXmlStatusRepository ndrXmlStatusRepository;
	private static final String STATUS = "status";
	public Map<String, Object> getPregnancyStatus(String personUuid) {
		Optional<String> pregnancyStatusOptional =
				ndrXmlStatusRepository.getPregnancyStatusByPersonUuid(personUuid);
		Map<String, Object> map = new HashMap<>();
		if (pregnancyStatusOptional.isPresent()) {
			String pregnancyStatus = pregnancyStatusOptional.get();
			if(pregnancyStatus.contains("Not") || pregnancyStatus.contains("PREGANACY_STATUS_NOT_PREGNANT")
					|| pregnancyStatus.contains("PREGANACY_STATUS_BREASTFEEDING")){
				map.put(STATUS, "NP");  //If clinic record is found but no record with pregnancy status checked
				map.put("lmp", null);
				map.put("edd", null);
			}else if(pregnancyStatus.equals("Pregnant") || pregnancyStatus.contains("PREGANACY_STATUS_PREGNANT")){
				map.put(STATUS, "P");
				map.put("lmp", null);
				map.put("edd", null);
			}else {
				map.put(STATUS, "NK");   //If no clinic record is found the pregnancy status is Unknown
				map.put("lmp", null);
				map.put("edd", null);
			}
			
		} else {
			map.put(STATUS, "NK");   //If no clinic record is found the pregnancy status is Unknown
			map.put("lmp", null);
			map.put("edd", null);
		}
		return map;
	}

	public Map<String, Object> getEDDandPMTCTLinkStatus(String personUuid) {
		Optional<String> pregnancyStatusOptional =
				ndrXmlStatusRepository.getPregnancyStatusByPersonUuid(personUuid);
		Map<String, Object> map = new HashMap<>();
		if (pregnancyStatusOptional.isPresent()) {
			String pregnancyStatus = pregnancyStatusOptional.get();
			if(pregnancyStatus.contains("Not") || pregnancyStatus.contains("PREGANACY_STATUS_NOT_PREGNANT")){
				map.put(STATUS, "NP");  //If clinic record is found but no record with pregnancy status checked
			}else if(pregnancyStatus.equals("Pregnant") || pregnancyStatus.contains("PREGANACY_STATUS_PREGNANT")){
				map.put(STATUS, "P");
			}else if(pregnancyStatus.contains("PREGANACY_STATUS_BREASTFEEDING")){
				map.put(STATUS, "BF");
			}
			else {
				map.put(STATUS, "NK");   //If no clinic record is found the pregnancy status is Unknown
			}

		} else {
			map.put(STATUS, "NK");   //If no clinic record is found the pregnancy status is Unknown
		}
		return map;
	}

	public Map<String, Object> getPregnancyBFStatusStatus(String personUuid) {
		Optional<String> pregnancyStatusOptional =
				ndrXmlStatusRepository.getPregnancyStatusByPersonUuid(personUuid);
		Map<String, Object> map = new HashMap<>();
		if (pregnancyStatusOptional.isPresent()) {
			String pregnancyStatus = pregnancyStatusOptional.get();
			if(pregnancyStatus.contains("PREGANACY_STATUS_BREASTFEEDING")){
				map.put(STATUS, "Breastfeeding");
			}else if(pregnancyStatus.contains("PREGANACY_STATUS_PREGNANT")){
				map.put(STATUS, "Pregnant");
			}

		}
		return map;
	}
}
