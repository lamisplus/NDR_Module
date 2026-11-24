package org.lamisplus.modules.ndr.repositories;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.domain.dto.ARTClinicalInfo;
import org.lamisplus.modules.ndr.domain.dto.LabDTO;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NdrXmlStatusRepository extends JpaRepository<NdrXmlStatus, Integer> {
	
	@Query(value = "SELECT\n" +
			"    p.id,\n" +
			"    p.uuid AS \"personUuid\",\n" +
			"    p.facility_id AS \"facilityId\",\n" +
			"    CAST(p.archived AS BOOLEAN) AS archived,\n" +
			"    p.hospital_number AS \"hospitalNumber\",\n" +
			"    p.surname,\n" +
			"    p.first_name AS \"firstName\",\n" +
			"    CAST(EXTRACT(YEAR FROM AGE(CURRENT_DATE, p.date_of_birth)) AS INTEGER) AS age,\n" +
			"    p.other_name AS \"otherName\",\n" +
			"    p.sex,\n" +
			"    p.date_of_birth AS \"dateOfBirth\",\n" +
			"    h.date_enrolled_in_hiv_care AS \"dateOfRegistration\",\n" +
			"    p.marital_status ->> 'display' AS \"maritalStatus\",\n" +
			"    p.education ->> 'display' AS education,\n" +
			"    p.employment_status ->> 'display' AS occupation,\n" +
			"    facility.name AS \"facilityName\",\n" +
			"    facility_lga.name AS lga,\n" +
			"    facility_state.name AS state,\n" +
			"    boui.code AS \"datimId\",\n" +
			"    addr.city AS town,\n" +
			"    res_state.name AS \"residentialState\",\n" +
			"    res_lga.name AS \"residentialLga\",\n" +
			"    addr.address AS address,\n" +
			"    p.contact_point -> 'contactPoint' -> 0 -> 'value' ->> 0 AS phone\n" +
			"FROM patient_person p\n" +
			"LEFT JOIN LATERAL (\n" +
			"    SELECT\n" +
			"        TRIM(BOTH '[]\"' FROM COALESCE(addr_elem -> 'line' ->> 0, addr_elem ->> 'line', '')) AS address,\n" +
			"        TRIM(BOTH '[]\"' FROM COALESCE(addr_elem -> 'city' ->> 0, addr_elem ->> 'city', '')) AS city,\n" +
			"        CASE\n" +
			"            WHEN (addr_elem ->> 'stateId') ~ '^\\d+$' THEN CAST((addr_elem ->> 'stateId') AS BIGINT)\n" +
			"        END AS state_id,\n" +
			"        CASE\n" +
			"            WHEN (addr_elem ->> 'district') ~ '^\\d+$' THEN CAST((addr_elem ->> 'district') AS BIGINT)\n" +
			"        END AS lga_id\n" +
			"    FROM jsonb_array_elements(\n" +
			"        CASE\n" +
			"            WHEN jsonb_typeof(p.address -> 'address') = 'array' THEN p.address -> 'address'\n" +
			"            ELSE CAST('[]' AS jsonb)\n" +
			"        END\n" +
			"    ) WITH ORDINALITY AS t(addr_elem, ord)\n" +
			"    ORDER BY ord\n" +
			"    LIMIT 1\n" +
			") AS addr ON TRUE\n" +
			"INNER JOIN base_organisation_unit AS facility\n" +
			"    ON facility.id = p.facility_id\n" +
			"INNER JOIN base_organisation_unit AS facility_lga\n" +
			"    ON facility_lga.id = facility.parent_organisation_unit_id\n" +
			"INNER JOIN base_organisation_unit AS facility_state\n" +
			"    ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
			"LEFT JOIN base_organisation_unit AS res_state\n" +
			"    ON res_state.id = addr.state_id\n" +
			"LEFT JOIN base_organisation_unit AS res_lga\n" +
			"    ON res_lga.id = addr.lga_id\n" +
			"LEFT JOIN LATERAL (\n" +
			"    SELECT b.code\n" +
			"    FROM base_organisation_unit_identifier AS b\n" +
			"    WHERE b.organisation_unit_id = p.facility_id\n" +
			"      AND b.name = 'DATIM_ID'\n" +
			"    ORDER BY b.id DESC\n" +
			"    LIMIT 1\n" +
			") AS boui ON TRUE\n" +
			"INNER JOIN LATERAL (\n" +
			"    SELECT hc.date_enrolled_in_hiv_care\n" +
			"    FROM hiv_enrollment_commencement AS hc\n" +
			"    WHERE hc.person_uuid = p.uuid\n" +
			"      AND hc.archived = 0\n" +
			"    ORDER BY hc.date_enrolled_in_hiv_care DESC, hc.id DESC\n" +
			"    LIMIT 1\n" +
			") AS h ON TRUE\n" +
			"WHERE p.uuid = ?1",
			nativeQuery = true)
	Optional<PatientDemographics> getPatientDemographicsByUUID(String patientUuid);
	
	@Query(value = "SELECT pregnancy_status from hiv_art_clinical\n" +
			"WHERE person_uuid = ?1 \n" +
			"AND archived = 0\n" +
			"ORDER BY visit_date DESC limit 1", nativeQuery = true)
	Optional<String> getPregnancyStatusByPersonUuid(String patientUuid);
	
	@Query(value = "SELECT a.display as tbStatus from hiv_art_clinical c " +
			"INNER JOIN base_application_codeset a on a.id = cast(c.tb_status as INTEGER) " +
			"WHERE person_uuid = ?1\n" +
			"AND c.archived = 0\n" +
			"ORDER BY visit_date DESC limit 1", nativeQuery = true)
	Optional<String> getTbStatusByPersonUuid(String personUuid);
	
	@Query(value = "SELECT c.id as clinicId,\n" +
			"\tc.facility_id as facilityId,\n" +
			"\tvisit_date as visitDate  ,\n" +
			"\tcd_4 as cd4,\n" +
			"\tcd_4_percentage as cd4Percentage,\t\n" +
			"\tis_commencement as isCommencement,\t\n" +
			"\tfunctional_status_id as functionalStatus,\t\n" +
			"\tclinical_stage_id as clinicalStageId ,\t\n" +
			"\tclinical_note as clinicalNote,\t\n" +
			"\tc.uuid as clinicalUuid,\t\n" +
			"\thiv_enrollment_uuid as hivEnrollmentUuid,\n" +
			"\tregimen_id as regimenId,\n" +
			"\tregimen_type_id as regimenTypeId,\n" +
			"\tc.archived as archived,\n" +
			"\tvital_sign_uuid as vitalSignUuid,\n" +
			"\twho_staging_id as whoStagingId,\n" +
			"\tc.person_uuid as personUuid,\n" +
			"    c.visit_id as visitId,\n" +
			"\tpregnancy_status as pregnencyStatus,\n" +
			"\tsti_treated as stiTreated,\n" +
			"\tadr_screened as adrScreened,\n" +
			"\tadherence_level as adherenceLevel,\n" +
			"\tnext_appointment as nextAppointment,\n" +
			"\tlmp_date as lmpDate,\n" +
			"\tis_viral_load_at_start_of_art as isViralLoadAtStartOfArt,\n" +
			"\tviral_load_at_start_of_art as viralLoadAtStartOfArt,\n" +
			"\tdate_of_viral_load_at_start_of_art as dateOfViralLoadAtStartOfArt,\n" +
			"\tcryptococcal_screening_status as cryptococcalScreeningStatus ,\n" +
			"\tcervical_cancer_screening_status as cervicalCancerScreeningStatus ,\n" +
			"\tcervical_cancer_treatment_provided as cervicalCancerTreatmentProvided ,\n" +
			"\thepatitis_screening_result as hepatitisScreeningResult,\n" +
			"\tfamily_planing as familyPlaning ,\n" +
			"\ton_family_planing as onFamilyPlaning,\n" +
			"\tlevel_of_adherence as levelOfAdherence,\n" +
			"\ttb_status as tbStatus,\n" +
			"\ttb_prevention as tbPrevention,\n" +
			"\tcd4_count as cd4Count,\n" +
			"\tbody_weight as bodyWeight,\n" +
			"\tdiastolic ,\n" +
			"\tcapture_date as captureDate,\n" +
			"\theight,\n" +
			"\ttemperature,\n" +
			"\tpulse,\n" +
			"\trespiratory_rate as respiratory,\n" +
			"\tsystolic\n" +
			"from public.hiv_art_clinical c \n" +
			"inner join triage_vital_sign  v on v.uuid = c.vital_sign_uuid\n" +
			"where c.person_uuid = ?1\n" +
			"and is_commencement is false\n" +
			"and c.archived = 0", nativeQuery = true)
	List<ARTClinicalInfo> getClinicalInfoByPersonUuid(String personUuid);
	
	@Query(value = "SELECT c.id as clinicId,\n" +
			"\tc.facility_id as facilityId,\n" +
			"\tvisit_date as visitDate  ,\n" +
			"\tcd_4 as cd4,\n" +
			"\tcd_4_percentage as cd4Percentage,\t\n" +
			"\tis_commencement as isCommencement,\t\n" +
			"\tfunctional_status_id as functionalStatus,\t\n" +
			"\tclinical_stage_id as clinicalStageId ,\t\n" +
			"\tclinical_note as clinicalNote,\t\n" +
			"\tc.uuid as clinicalUuid,\t\n" +
			"\thiv_enrollment_uuid as hivEnrollmentUuid,\n" +
			"\tregimen_id as regimenId,\n" +
			"\tregimen_type_id as regimenTypeId,\n" +
			"\tc.archived as archived,\n" +
			"\tvital_sign_uuid as vitalSignUuid,\n" +
			"\twho_staging_id as whoStagingId,\n" +
			"\tc.person_uuid as personUuid,\n" +
			"    c.visit_id as visitId,\n" +
			"\tpregnancy_status as pregnencyStatus,\n" +
			"\tsti_treated as stiTreated,\n" +
			"\tadr_screened as adrScreened,\n" +
			"\tadherence_level as adherenceLevel,\n" +
			"\tnext_appointment as nextAppointment,\n" +
			"\tlmp_date as lmpDate,\n" +
			"\tis_viral_load_at_start_of_art as isViralLoadAtStartOfArt,\n" +
			"\tviral_load_at_start_of_art as viralLoadAtStartOfArt,\n" +
			"\tdate_of_viral_load_at_start_of_art as dateOfViralLoadAtStartOfArt,\n" +
			"\tcryptococcal_screening_status as cryptococcalScreeningStatus ,\n" +
			"\tcervical_cancer_screening_status as cervicalCancerScreeningStatus ,\n" +
			"\tcervical_cancer_treatment_provided as cervicalCancerTreatmentProvided ,\n" +
			"\thepatitis_screening_result as hepatitisScreeningResult,\n" +
			"\tfamily_planing as familyPlaning ,\n" +
			"\ton_family_planing as onFamilyPlaning,\n" +
			"\tlevel_of_adherence as levelOfAdherence,\n" +
			"\ttb_status as tbStatus,\n" +
			"\ttb_prevention as tbPrevention,\n" +
			"\tcd4_count as cd4Count,\n" +
			"\tbody_weight as bodyWeight,\n" +
			"\tdiastolic ,\n" +
			"\tcapture_date as captureDate,\n" +
			"\theight,\n" +
			"\ttemperature,\n" +
			"\tpulse,\n" +
			"\trespiratory_rate as respiratory,\n" +
			"\tsystolic\n" +
			"from public.hiv_art_clinical c \n" +
			"inner join triage_vital_sign  v on v.uuid = c.vital_sign_uuid\n" +
			"where c.person_uuid = ?1\n" +
			"and c.last_modified_date >= ?2 \n" +
			"and is_commencement is false\n" +
			"and c.archived = 0", nativeQuery = true)
	List<ARTClinicalInfo> getClinicalInfoByPersonUuidByLastModifiedDate(String personUuid, LocalDateTime lastModified);
	
	
	@Query(value = "SELECT\n" +
			"lt.patient_uuid AS patientId, lt.viral_load_indication AS indicationId, \n" +
			"lr.date_result_reported resultDate,\n" +
			"ls.date_sample_collected dateSampleCollected,\n" +
			"lt.uuid AS visitId,\n" +
			"llt.lab_test_name AS labTestName,\n" +
			"ls.date_modified AS dateSampleModified,\n" +
			"(CASE WHEN lr.date_assayed IS NULL THEN lr.date_result_reported\n" +
			"ELSE lr.date_assayed END) AS dateAssayed,\n" +
			"lr.date_modified AS dateResultModified,\n" +
			"REPLACE(lr.result_reported, '<', '') AS resultReported FROM laboratory_test lt \n" +
			"INNER JOIN laboratory_result lr ON lr.patient_uuid=lt.patient_uuid AND lt.id=lr.test_id\n" +
			"INNER JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.patient_uuid = lr.patient_uuid\n" +
			"INNER JOIN laboratory_labtest llt ON lt.lab_test_id=llt.id\n" +
			"INNER JOIN laboratory_labtestgroup llg ON llt.labtestgroup_id=llg.id\n" +
			"WHERE (llt.lab_test_name = 'Viral Load' OR llt.lab_test_name='CD4') \n" +
			"AND lt.patient_uuid = ?1\n" +
			"AND lr.date_result_reported >= ?2", nativeQuery = true)
	List<LabDTO> getLabInfoByPersonUuid(String personUuid, LocalDateTime lastGenerateDateTime);
	
	@Query (value = "select last_modified from ndr_xml_status where \n" +
			"facility_id = ?1\n" +
			"AND type = ?2\n" +
			"order by last_modified desc limit 1", nativeQuery = true)
	 Optional<Timestamp> getLastGenerateDateTimeByFacilityId(Long facilityId, String Type);

	@Query (value = "SELECT * FROM public.ndr_xml_status ORDER BY  id DESC LIMIT 50", nativeQuery = true)
	List<NdrXmlStatus> getAllFiles();


	@Modifying
	@Transactional
	@Query(value = "delete from ndr_xml_status WHERE error is not null", nativeQuery = true)
	int deleteNdrXmlStatusByErrorIsNotNull();


	
}
