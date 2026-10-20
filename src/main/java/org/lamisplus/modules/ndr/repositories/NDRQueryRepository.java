package org.lamisplus.modules.ndr.repositories;

import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PrepDTO;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NDRQueryRepository extends JpaRepository<NdrMessageLog, Integer> {

    @Query(value = "SELECT \n" +
            "\tp.uuid AS personUuid,\n" +
            "\tp.date_of_registration AS diagnosisDate,\n" +
            "\tp.date_of_birth AS dateOfBirth,\n" +
            "\tp.id AS personId,\n" +
            "\tp.hospital_number AS hospitalNumber,\n" +
            "\tCONCAT(boui.code, '_', p.uuid) AS patientIdentifier,\n" +
            "\tEXTRACT(YEAR FROM AGE(NOW(), p.date_of_birth)) AS age,\n" +
            "\tCASE WHEN INITCAP(p.sex) = 'Female' THEN 'F' ELSE 'M' END AS patientSexCode,\n" +
            "\tp.date_of_birth AS patientDateOfBirth,\n" +
            "\t'FAC' AS facilityTypeCode,\n" +
            "\tfacility.name AS facilityName,\n" +
            "\tfacility_lga.name AS lga,\n" +
            "\tfacility_state.name AS state,\n" +
            "\tboui.code AS facilityId,\n" +
            "\tCASE WHEN lgaCode.code = '521' AND stateCode.code = '20' THEN '520' ELSE lgaCode.code END AS lgaCode,\n" +
            "\tstateCode.code AS stateCode,\n" +
            "\t'NGA' AS countryCode,\n" +
            "\templCode.code AS patientOccupationCode,\n" +
            "\tmariCode.code AS PatientMaritalStatusCode,\n" +
            "\tstateCode.code AS stateOfNigeriaOriginCode,\n" +
            "\teduCode.code AS patientEducationLevelCode\n" +
            "FROM patient_person p\n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = p.facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = p.facility_id AND boui.name = 'DATIM_ID'\n" +
            "INNER JOIN prophylaxis_screening pel ON pel.person_uuid = p.uuid AND pel.archived = false\n" +
            "LEFT JOIN ndr_code_set lgaCode ON TRIM(lgaCode.code_description) = TRIM(facility_lga.name) AND lgaCode.code_set_nm = 'LGA'\n" +
            "LEFT JOIN ndr_code_set stateCode ON TRIM(stateCode.code_description) = TRIM(facility_state.name) AND stateCode.code_set_nm = 'STATES'\n" +
            "LEFT JOIN ndr_code_set emplCode ON emplCode.code_description = CAST(p.employment_status->>'display' AS VARCHAR) AND emplCode.code_set_nm = 'OCCUPATION_STATUS'\n" +
            "LEFT JOIN ndr_code_set mariCode ON mariCode.code_description = CAST(p.marital_status->>'display' AS VARCHAR) AND mariCode.code_set_nm = 'MARITAL_STATUS'\n" +
            "LEFT JOIN ndr_code_set eduCode ON eduCode.code_description = CAST(p.education->>'display' AS VARCHAR) AND eduCode.code_set_nm = 'EDUCATIONAL_LEVEL'\n" +
            "WHERE p.uuid = ?1\n" +
            "AND pel.facility_id = ?2\n" +
            "AND pel.date_modified >= ?3\n" +
            "LIMIT 1", nativeQuery = true)
    Optional<PatientDemographicDTO> getPrepPatientDemographics(String identifier, Long facilityId, LocalDateTime lastModified);

    @Query(value = "SELECT\n" +
            "    CAST(ps.unique_Client_Id AS VARCHAR) AS uniqueClientId, \n" +
            "    ps.visit_date AS dateOfVisit, \n" +
            "    CAST(ps.referred_from AS VARCHAR) AS referredFrom,\n" +
            "    CAST(ps.setting AS VARCHAR) AS setting,\n" +
            "    p.date_of_birth AS dateOfBirth,\n" +
            "    CASE WHEN INITCAP(p.sex) = 'Female' THEN 'F' ELSE 'M' END AS sex,\n" +
            "    CAST(ps.visit_type AS VARCHAR) AS visitType,\n" +
            "    CAST(ps.population_type AS VARCHAR) AS populationType,\n" +
            "    emplCode.code AS occupation,\n" +
            "    mariCode.code AS maritalStatus,\n" +
            "    eduCode.code AS educationLevel,\n" +
            "    ps.num_children_less_than_five AS numberOfOwnChildrenUnder5,\n" +
            "    CAST(ps.type_of_session AS VARCHAR) AS typOfSession,\n" +
            "    CAST(ps.sex_partner AS VARCHAR) AS sexPartners,\n" +
            "    CAST(hts_en.observation->>'pregnancyStatus' AS VARCHAR) AS pregnancyStatus,\n" +
            "    CASE \n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWihHIVPositive' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWihHIVPositive' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS partnerIsHivPositive,\n" +
            "    CASE \n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWithPartnerInjectDrug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWithPartnerInjectDrug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS partnerInjectsDrugs,\n" +
            "    CASE \n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWithPartnerWhoHasSexWithMen' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWithPartnerWhoHasSexWithMen' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS partnerHasSexWithMen,\n" +
            "    CASE \n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWithPartnerTransgender' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.sex_partner_risk->>'haveSexWithPartnerTransgender' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS partnerIsTransgender,\n" +
            "    CASE \n" +
            "        WHEN ps.sex_partner_risk->>'sexWithPartnersWithoutCondoms' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.sex_partner_risk->>'sexWithPartnersWithoutCondoms' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS partnerHasMultiplePartnersWithoutCondoms,\n" +
            "    CASE \n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'unprotectedVaginalSexCasual' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'unprotectedVaginalSexCasual' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS unprotectedVaginalSexWithCasualPartner,\n" +
            "    CASE \n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'unprotectedAnalSexWithPartner' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'unprotectedAnalSexWithPartner' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS unprotectedAnalSex,\n" +
            "    CASE \n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'sharedNeedles' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'sharedNeedles' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS sharedNeedlesOrInjectingMaterials,\n" +
            "    CASE \n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'moreThan1SexPartner' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'moreThan1SexPartner' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS moreThanOneSexPartner,\n" +
            "    CASE \n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'haveYouPaidForSex' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true\n" +
            "        WHEN ps.personal_hiv_risk_assessment->>'haveYouPaidForSex' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false\n" +
            "    END AS paidOrBeenPaidForSex,\n" +
            "    CASE WHEN ps.drug_use_history->>'drug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'drug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesCocaine,\n" +
            "    CASE WHEN ps.drug_use_history->>'drug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'drug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesHeroin,\n" +
            "    CASE WHEN ps.drug_use_history->>'drug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'drug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesMarijuana,\n" +
            "    CASE WHEN ps.drug_use_history->>'drug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'drug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesAmphetamine,\n" +
            "    CASE WHEN ps.drug_use_history->>'drug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'drug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesCodeineSyrup,\n" +
            "    CASE WHEN ps.drug_use_history->>'drug' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'drug' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesOtherDrugs,\n" +
            "    NULLIF(CAST(ps.drug_use_history->>'drug' AS VARCHAR), '') AS otherDrugsSpecify,\n" +
            "    CASE WHEN ps.drug_use_history->>'routesOfAdministration' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'routesOfAdministration' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS routeInject,\n" +
            "    CASE WHEN ps.drug_use_history->>'routesOfAdministration' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'routesOfAdministration' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS routeSniff,\n" +
            "    CASE WHEN ps.drug_use_history->>'routesOfAdministration' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'routesOfAdministration' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS routeSnort,\n" +
            "    CASE WHEN ps.drug_use_history->>'routesOfAdministration' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'routesOfAdministration' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS routeSmoke,\n" +
            "    CASE WHEN ps.use_drug_sexual_performance IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.use_drug_sexual_performance IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS usesDrugsForSexualEnhancement,\n" +
            "    CASE WHEN ps.assessment_for_pep_indication->>'unprotectedSexWithHivPositiveOrUnknownStatusLast72Hours' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_pep_indication->>'unprotectedSexWithHivPositiveOrUnknownStatusLast72Hours' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS hadSexWithoutCondomUnkHivStatLast72Hours,\n" +
            "    CASE WHEN ps.assessment_for_pep_indication->>'sharedInjectionOrNeedleWithHivPositiveOrUnknownStatusLast72Hours' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_pep_indication->>'sharedInjectionOrNeedleWithHivPositiveOrUnknownStatusLast72Hours' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS sharedInjectionEquipmentUnkHivStatLast72Hours,\n" +
            "    CASE WHEN ps.assessment_for_acute_hiv_infection->>'acuteHivSymptomsLasttwoWeeks' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_acute_hiv_infection->>'acuteHivSymptomsLasttwoWeeks' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS hadColdFluSymptomsLast2Weeks,\n" +
            "    CASE WHEN ps.assessment_for_acute_hiv_infection->>'unprotectedAnalOrVaginalOrSharedNeedlesLast28Days' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_acute_hiv_infection->>'unprotectedAnalOrVaginalOrSharedNeedlesLast28Days' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS hadUnprotectedSexOrSharedMaterialsLast28Days,\n" +
            "    CASE WHEN ps.sti_screening->>'vaginalDischarge' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'vaginalDischarge' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS femaleVaginalDischargeOrBurning,\n" +
            "    CASE WHEN ps.sti_screening->>'lowerAbdominalPains' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'lowerAbdominalPains' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS femaleLowerAbdominalPain,\n" +
            "    CASE WHEN ps.sti_screening->>'urethralDischarge' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'urethralDischarge' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS maleUrethralDischargeOrBurning,\n" +
            "    CASE WHEN ps.sti_screening->>'complaintsOfScrotal' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'complaintsOfScrotal' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS maleScrotumSwellingAndPain,\n" +
            "    CASE WHEN ps.sti_screening->>'genitalScore' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'genitalScore' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS genitalSores,\n" +
            "    CASE WHEN ps.sti_screening->>'swollenIguinal' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'swollenIguinal' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS swollenInguinalLymphNodes,\n" +
            "    CASE WHEN ps.sti_screening->>'analpain' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'analpain' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS analPainOnStooling,\n" +
            "    CASE WHEN ps.sti_screening->>'analItching' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'analItching' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS analItching,\n" +
            "    CASE WHEN ps.sti_screening->>'analDischarge' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.sti_screening->>'analDischarge' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS analDischarge,\n" +
            "    NULLIF(CAST(ps.drug_use_history->>'lastTest' AS VARCHAR), '') AS lastHivTestTimeframe,\n" +
            "    CASE WHEN ps.drug_use_history->>'recommendHivRetest' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'recommendHivRetest' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS recommendedForHivRetest,\n" +
            "    CASE WHEN ps.drug_use_history->>'clinicalSetting' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'clinicalSetting' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS testedInOtherClinicalSettings,\n" +
            "    NULLIF(CAST(ps.drug_use_history->>'hivTestResultAtvisit' AS VARCHAR), '') AS hivTestResult,\n" +
            "    CASE WHEN ps.drug_use_history->>'reportHivRisk' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'reportHivRisk' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS reportsOngoingHivRiskBehaviors,\n" +
            "    CASE WHEN ps.drug_use_history->>'hivExposure' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.drug_use_history->>'hivExposure' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS reportsSpecificHivExposureLast3Months,\n" +
            "    CASE WHEN ps.assessment_for_prep_eligibility->>'hivNegative' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_prep_eligibility->>'hivNegative' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS hivNegative,\n" +
            "    CASE WHEN ps.assessment_for_prep_eligibility->>'hivRiskScore' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_prep_eligibility->>'hivRiskScore' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS hivRiskScoreAtLeastOne,\n" +
            "    CASE WHEN ps.assessment_for_prep_eligibility->>'noSignsAndSymptomsOfAcuteHivInfection' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_prep_eligibility->>'noSignsAndSymptomsOfAcuteHivInfection' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS noSignsOfAcuteHivInfection,\n" +
            "    CASE WHEN ps.assessment_for_prep_eligibility->>'noIndicationForPep' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_prep_eligibility->>'noIndicationForPep' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS noIndicationForPep,\n" +
            "    CASE WHEN ps.assessment_for_prep_eligibility->>'hasNoProteinuria' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.assessment_for_prep_eligibility->>'hasNoProteinuria' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS hasNoProteinuria,\n" +
            "    CASE WHEN ps.consideration_for_injections->>'noHistoryOrSignsOfLiverAbnormalitiesInjectable' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.consideration_for_injections->>'noHistoryOrSignsOfLiverAbnormalitiesInjectable' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS noHistoryOfLiverAbnormalities,\n" +
            "    CASE WHEN ps.consideration_for_injections->>'noHistoryOfDrugToDrugInteractionInjectable' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.consideration_for_injections->>'noHistoryOfDrugToDrugInteractionInjectable' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS noHistoryOfDrugDrugInteractionsForInjectable,\n" +
            "    CASE WHEN ps.consideration_for_injections->>'noHistoryOfDrugHypersensitivityInjectable' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.consideration_for_injections->>'noHistoryOfDrugHypersensitivityInjectable' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS noHistoryOfDrugHypersensitivity,\n" +
            "    CASE WHEN ps.services_received_by_client->>'prepOffered' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.services_received_by_client->>'prepOffered' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS prepOffered,\n" +
            "    CASE WHEN ps.services_received_by_client->>'willingToCommencePrep' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.services_received_by_client->>'willingToCommencePrep' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS willingToCommencePrep,\n" +
            "    CASE WHEN ps.received_prep_first_time_this_year IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.received_prep_first_time_this_year IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS receivedPrepForFirstTimeThisYear,\n" +
            "    CASE WHEN ps.services_received_by_client->>'clientReferredToOtherServices' IN ('true', 'YES', 'YES_NO_YES', '1') THEN true WHEN ps.services_received_by_client->>'clientReferredToOtherServices' IN ('false', 'NO', 'YES_NO_NO', '0') THEN false END AS clientReferredToOtherServices,\n" +
            "    NULLIF(CAST(ps.services_received_by_client->>'othersSpecify' AS VARCHAR), '') AS referralServiceSpecify\n" +
            "FROM patient_person p\n" +
            "INNER JOIN prophylaxis_screening ps \n" +
            "    ON ps.person_uuid = p.uuid \n" +
            "   AND ps.facility_id = ?1 \n" +
            "   AND ps.archived = false\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT observation \n" +
            "    FROM hts_encounter \n" +
            "    WHERE patient_uuid = p.uuid \n" +
            "    ORDER BY id DESC\n" +
            "    LIMIT 1\n" +
            ") hts_en ON true\n" +
            "LEFT JOIN ndr_code_set emplCode \n" +
            "    ON emplCode.code_set_nm = 'OCCUPATION_STATUS' \n" +
            "   AND emplCode.code_description = CAST(p.employment_status->>'display' AS VARCHAR)\n" +
            "LEFT JOIN ndr_code_set mariCode \n" +
            "    ON mariCode.code_set_nm = 'MARITAL_STATUS' \n" +
            "   AND mariCode.code_description = CAST(p.marital_status->>'display' AS VARCHAR)\n" +
            "LEFT JOIN ndr_code_set eduCode \n" +
            "    ON eduCode.code_set_nm = 'EDUCATIONAL_LEVEL' \n" +
            "   AND eduCode.code_description = CAST(p.education->>'display' AS VARCHAR)\n" +
            "WHERE p.uuid = ?2\n" +
            "LIMIT 1;", nativeQuery = true)
    List<PrepDTO> getPrepReportByClientCodeAndLastModified(Long facilityId, String patientId, LocalDateTime lastModified);

    @Query(value = "SELECT person_uuid from prophylaxis_screening \n" +
            "WHERE facility_id=?1 AND date_modified >= ?2 AND archived = false", nativeQuery = true)
    List<String> getPrepId(Long facilityId, LocalDateTime lastModified);

    @Query(value = "SELECT\n" +
            "    p.hospital_number                                           AS hospitalNumber,\n" +
            "    CAST(pi.enrollment_type AS VARCHAR)                         AS enrollmentType,\n" +
            "    CAST(pi.unique_id AS VARCHAR)                               AS uniqueId,\n" +
            "    pi.date_enrolled                                            AS dateEnrolled,\n" +
            "    CAST('' AS VARCHAR)                                         AS partnerAncOrUniqueArtNumber,\n" +
            "    CASE WHEN INITCAP(p.sex) = 'Female' THEN 'F' ELSE 'M' END   AS sex,\n" +
            "    CAST(EXTRACT(YEAR FROM AGE(NOW(), p.date_of_birth)) AS INTEGER) AS age,\n" +
            "    mariCode.code                                               AS maritalStatus,\n" +
            "    emplCode.code                                               AS occupation,\n" +
            "    eduCode.code                                                AS educationLevel,\n" +
            "    CAST(hts_en.setting AS VARCHAR)                             AS hivTestingPoint,\n" +
            "    hts_en.date_of_visit                                        AS dateOfLastHivTest,\n" +
            "    CAST(hts_en.finalHivTestResult AS VARCHAR)                  AS hivTestResult,\n" +
            "    pi.date_referred                                            AS dateReferred,\n" +
            "    CAST(pi.population_type AS VARCHAR)                         AS populationType,\n" +
            "    CAST('' AS VARCHAR)                                         AS otherPopulationSpecify,\n" +
            "    pi.date_of_initial_adherence_counseling                     AS dateInitialAdherenceCounseling,\n" +
            "    pi.date_prep_started                                        AS dateStarted,\n" +
            "    CAST(pi.prep_type_at_start AS VARCHAR)                      AS prepTypeAtStart,\n" +
            "    CAST(pi.prep_regimen AS VARCHAR)                            AS prepRegimen,\n" +
            "    CAST(pi.weight AS NUMERIC)                                  AS weight,\n" +
            "    CAST(pi.height AS NUMERIC)                                  AS height,\n" +
            "    CAST(\n" +
            "        CASE\n" +
            "            WHEN pi.height IS NOT NULL AND pi.height > 0\n" +
            "            THEN pi.weight / ((pi.height/100.0) * (pi.height/100.0))\n" +
            "            ELSE NULL\n" +
            "        END AS NUMERIC\n" +
            "    )                                                           AS bmi,\n" +
            "    CAST(CASE WHEN hts_en.pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%'\n" +
            "              THEN TRUE ELSE FALSE END AS BOOLEAN)              AS isPregnant,\n" +
            "    CAST(CASE WHEN hts_en.breastfeeding ILIKE '%PREGANACY_STATUS_BREASTFEEDING%'\n" +
            "              THEN TRUE ELSE FALSE END AS BOOLEAN)              AS isBreastfeeding,\n" +
            "    CAST(pi.history_of_drug_allergies AS VARCHAR)               AS historyOfDrugAllergies,\n" +
            "    CAST(pi.history_of_drug_to_drug_interaction AS VARCHAR)     AS historyOfDrugDrugInteractions,\n" +
            "    CAST(pi.urinalysis_result AS VARCHAR)                       AS urinalysisResult,\n" +
            "    CAST(pi.liver_function_test_results AS VARCHAR)             AS liverFunctionTestResult,\n" +
            "    CAST('' AS VARCHAR)                                         AS referredAtInitiation,\n" +
            "    CAST(NULL AS DATE)                                          AS dateReferredAtInitiation,\n" +
            "    CAST('' AS VARCHAR)                                         AS serviceReferredFor,\n" +
            "    CAST('' AS VARCHAR)                                         AS pepCompletion\n" +
            "FROM patient_person p\n" +
            "INNER JOIN prophylaxis_initiation pi\n" +
            "        ON pi.person_uuid = p.uuid\n" +
            "       AND pi.facility_id = ?1\n" +
            "       AND pi.archived = false\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT setting,\n" +
            "           date_of_visit,\n" +
            "           CAST(observation->>'finalHivTestResult' AS VARCHAR)  AS finalHivTestResult,\n" +
            "           CAST(observation->>'pregnancyStatus' AS VARCHAR)     AS pregnancyStatus,\n" +
            "           CAST(observation->>'breastfeedingDuration' AS VARCHAR) AS breastfeeding\n" +
            "    FROM hts_encounter\n" +
            "    WHERE patient_uuid = pi.person_uuid\n" +
            "    ORDER BY id DESC\n" +
            "    LIMIT 1\n" +
            ") hts_en ON TRUE\n" +
            "LEFT JOIN ndr_code_set emplCode\n" +
            "      ON emplCode.code_set_nm = 'OCCUPATION_STATUS'\n" +
            "     AND emplCode.code_description = CAST(p.employment_status->>'display' AS VARCHAR)\n" +
            "LEFT JOIN ndr_code_set mariCode\n" +
            "      ON mariCode.code_set_nm = 'MARITAL_STATUS'\n" +
            "     AND mariCode.code_description = CAST(p.marital_status->>'display' AS VARCHAR)\n" +
            "LEFT JOIN ndr_code_set eduCode\n" +
            "      ON eduCode.code_set_nm = 'EDUCATIONAL_LEVEL'\n" +
            "     AND eduCode.code_description = CAST(p.education->>'display' AS VARCHAR)\n" +
            "WHERE p.uuid = ?2", nativeQuery = true)
    List<PrepDTO> getCardEnrollment(Long facilityId, String patientId, LocalDateTime lastModified);
}


