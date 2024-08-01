package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PregnancyStatus {
	
	private final NdrXmlStatusRepository ndrXmlStatusRepository;
	
	public Map<String, Object> getPregnancyStatus(String personUuid) {
		Optional<String> pregnancyStatusOptional =
				ndrXmlStatusRepository.getPregnancyStatusByPersonUuid(personUuid);
		Map<String, Object> map = new HashMap<>();
		if (pregnancyStatusOptional.isPresent()) {
			String pregnancyStatus = pregnancyStatusOptional.get();
			if(pregnancyStatus.contains("Not")){
				map.put("status", "NP");  //If clinic record is found but no record with pregnancy status checked
				map.put("lmp", null);
				map.put("edd", null);
			}else if(pregnancyStatus.equals("Pregnant")){
				map.put("status", "P");
				map.put("lmp", null);
				map.put("edd", null);
			}else {
				map.put("status", "NK");   //If no clinic record is found the pregnancy status is Unknown
				map.put("lmp", null);
				map.put("edd", null);
			}
			
		} else {
			map.put("status", "NK");   //If no clinic record is found the pregnancy status is Unknown
			map.put("lmp", null);
			map.put("edd", null);
		}
		return map;
	}
}
