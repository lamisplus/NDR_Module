package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.hiv.service.StatusManagementService;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.schema.CommonQuestionsType;
import org.lamisplus.modules.ndr.schema.FacilityType;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommonQuestionsTypeMapper {

	private final MessageHeaderTypeMapper messageHeaderTypeMapper;

	private final StatusManagementService statusManagementService;

	private final PregnancyStatus pregnancyStatus;


	public static class LogMessages {
		public static final String GENERATING_COMMON_QUESTIONS = "Generating common questions for patient with uuid {}";
	}

	public static class LogErrorMessages {
		public static final String GENERATING_ERROR_MSG = "An error occurred while Generating common questions for patient with uuid {}";
	}


	public CommonQuestionsType getPatientCommonQuestion(PatientDemographics demographics) {
		log.info(LogMessages.GENERATING_COMMON_QUESTIONS, demographics.getPersonUuid());
		try {
			CommonQuestionsType common = new CommonQuestionsType();
			FacilityType treatmentFacility = messageHeaderTypeMapper.getTreatmentFacility(demographics);
			common.setDiagnosisFacility(treatmentFacility);
			common.setHospitalNumber(demographics.getHospitalNumber());
			common.setPatientAge(demographics.getAge());
			if (demographics.getSex() != null) {
				if (demographics.getSex().contains("F")) {
					Map<String, Object> pStatus =
							pregnancyStatus.getPregnancyStatus(demographics.getPersonUuid());
					common.setPatientPregnancyStatusCode((String) pStatus.get("status"));
				}
			}
			String currentStatus = statusManagementService.getCurrentStatus(demographics.getId());
			if (currentStatus.equalsIgnoreCase("KNOWN_DEATH") || currentStatus.contains("DIED")) common.setPatientDieFromThisIllness(true);
			if (demographics.getDateOfRegistration() != null) {
				common.setDiagnosisDate(DateUtil.getXmlDate(Date.valueOf(demographics.getDateOfRegistration())));
			}

			return common;
		} catch (Exception e) {
			log.error(LogErrorMessages.GENERATING_ERROR_MSG,
					demographics.getPersonUuid());
			log.error("Error Message: {}", e.getMessage());
		}

		return null;
	}

	public CommonQuestionsType getPatientCommonQuestion(PatientDemographicDTO demographics) {
		log.info(LogMessages.GENERATING_COMMON_QUESTIONS, demographics.getPersonUuid());
		try {
			CommonQuestionsType common = new CommonQuestionsType();
			FacilityType treatmentFacility = messageHeaderTypeMapper.getTreatmentFacility(demographics);
			common.setDiagnosisFacility(treatmentFacility);
			common.setHospitalNumber(demographics.getHospitalNumber());
			common.setPatientAge(demographics.getAge());
			if (demographics.getPatientSexCode() != null) {
				if (demographics.getPatientSexCode().contains("F")) {
					Map<String, Object> pStatus =
							pregnancyStatus.getPregnancyStatus(demographics.getPersonUuid());
					common.setPatientPregnancyStatusCode((String) pStatus.get("status"));
				}
			}
			String currentStatus = statusManagementService.getCurrentStatus(demographics.getPersonId());

			log.info("Client current Status: {}", currentStatus);
			if (currentStatus.contains("DIED") || currentStatus.equalsIgnoreCase("KNOWN_DEATH")) {
				common.setPatientDieFromThisIllness(true);
			}
			if (demographics.getDiagnosisDate() != null) {
				common.setDiagnosisDate(DateUtil.getXmlDate(Date.valueOf(demographics.getDiagnosisDate())));
			} else {
				throw new IllegalArgumentException("Diagnosis date cannot be null");
			}

			return common;
		} catch (Exception e) {
			log.error(LogErrorMessages.GENERATING_ERROR_MSG,
					demographics.getPersonUuid());
			log.error("Error Message: {}", e.getMessage());
		}
		return null;
	}
}

