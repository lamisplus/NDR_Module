package org.lamisplus.modules.ndr.mapper;

import org.lamisplus.modules.ndr.domain.dto.NDREligibleClient;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class NDREligibleClientMapper implements Function<Person,NDREligibleClient> {
	@Override
	public NDREligibleClient apply(Person person) {
		StringBuilder name = new StringBuilder();
		if(person.getSurname() != null){
			name.append(person.getSurname()).append(" ");
		}
		if(person.getFirstName() != null){
			name.append(person.getFirstName()).append(" ");
		}
		if(person.getOtherName() != null ){
			name.append(person.getOtherName());
		}
		return  new NDREligibleClient(person.getUuid(), person.getHospitalNumber(), name.toString().trim());
	}
}
