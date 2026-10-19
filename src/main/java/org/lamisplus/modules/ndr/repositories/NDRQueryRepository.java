package org.lamisplus.modules.ndr.repositories;

import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PrepDTO;
import org.lamisplus.modules.ndr.domain.dto.pmtct.DeliveryChildrenDetailsProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.InfantCohortRegistrationProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.MotherInfantPairVisitProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTRegisterProjection;
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

    @Query(value = "SELECT person_uuid from prep_enrollment \n" +
            "WHERE facility_id=?1 AND date_modified > ?2 AND archived = false", nativeQuery = true)
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

    @Query(value = "SELECT DISTINCT p.uuid\n" +
            "FROM patient_person p\n" +
            "INNER JOIN (\n" +
            "    SELECT patient_uuid, facility_id, COALESCE(last_modified_date, created_date, NOW()) AS mod_date\n" +
            "    FROM pmtct_anc WHERE COALESCE(archived, false) = false\n" +
            "    UNION\n" +
            "    SELECT patient_uuid, facility_id, COALESCE(last_modified_date, created_date, NOW()) AS mod_date\n" +
            "    FROM pmtct_enrollment WHERE COALESCE(archived, false) = false\n" +
            "    UNION\n" +
            "    SELECT patient_uuid, facility_id, COALESCE(last_modified_date, created_date, NOW()) AS mod_date\n" +
            "    FROM pmtct_mother_visitation WHERE COALESCE(archived, false) = false\n" +
            "    UNION\n" +
            "    SELECT patient_uuid, facility_id, COALESCE(last_modified_date, created_date, NOW()) AS mod_date\n" +
            "    FROM pmtct_delivery WHERE COALESCE(archived, false) = false\n" +
            "    UNION\n" +
            "    SELECT mother_patient_uuid AS patient_uuid, facility_id, COALESCE(last_modified_date, created_date, NOW()) AS mod_date\n" +
            "    FROM pmtct_infant_information WHERE COALESCE(archived, false) = false\n" +
            "    UNION\n" +
            "    SELECT mother_patient_uuid AS patient_uuid, facility_id, COALESCE(last_modified_date, created_date, NOW()) AS mod_date\n" +
            "    FROM pmtct_infant_visit WHERE COALESCE(archived, false) = false\n" +
            ") pmtct_p ON pmtct_p.patient_uuid = p.uuid\n" +
            "WHERE pmtct_p.facility_id = ?1\n" +
            "  AND pmtct_p.mod_date >= ?2\n" +
            "  AND COALESCE(p.archived, 0) = 0", nativeQuery = true)
    List<String> getPmtctPatientIds(Long facilityId, LocalDateTime lastModified);

    @Query(value = "SELECT \n" +
            "    p.uuid AS personUuid,\n" +
            "    p.date_of_registration AS diagnosisDate,\n" +
            "    p.date_of_birth AS dateOfBirth,\n" +
            "    p.id AS personId,\n" +
            "    p.hospital_number AS hospitalNumber,\n" +
            "    CONCAT(boui.code, '_', p.uuid) AS patientIdentifier,\n" +
            "    EXTRACT(YEAR FROM AGE(NOW(), p.date_of_birth)) AS age,\n" +
            "    CASE WHEN INITCAP(p.sex) = 'Female' THEN 'F' ELSE 'M' END AS patientSexCode,\n" +
            "    p.date_of_birth AS patientDateOfBirth,\n" +
            "    'FAC' AS facilityTypeCode,\n" +
            "    facility.name AS facilityName,\n" +
            "    facility_lga.name AS lga,\n" +
            "    facility_state.name AS state,\n" +
            "    boui.code AS facilityId,\n" +
            "    CASE WHEN lgaCode.code = '521' AND stateCode.code = '20' THEN '520' ELSE lgaCode.code END AS lgaCode,\n" +
            "    stateCode.code AS stateCode,\n" +
            "    'NGA' AS countryCode,\n" +
            "    emplCode.code AS patientOccupationCode,\n" +
            "    mariCode.code AS PatientMaritalStatusCode,\n" +
            "    stateCode.code AS stateOfNigeriaOriginCode,\n" +
            "    eduCode.code AS patientEducationLevelCode\n" +
            "FROM patient_person p\n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = p.facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = p.facility_id AND boui.name = 'DATIM_ID'\n" +
            "LEFT JOIN ndr_code_set lgaCode ON TRIM(lgaCode.code_description) = TRIM(facility_lga.name) AND lgaCode.code_set_nm = 'LGA'\n" +
            "LEFT JOIN ndr_code_set stateCode ON TRIM(stateCode.code_description) = TRIM(facility_state.name) AND stateCode.code_set_nm = 'STATES'\n" +
            "LEFT JOIN ndr_code_set emplCode ON emplCode.code_description = CAST(p.employment_status->>'display' AS VARCHAR) AND emplCode.code_set_nm = 'OCCUPATION_STATUS'\n" +
            "LEFT JOIN ndr_code_set mariCode ON mariCode.code_description = CAST(p.marital_status->>'display' AS VARCHAR) AND mariCode.code_set_nm = 'MARITAL_STATUS'\n" +
            "LEFT JOIN ndr_code_set eduCode ON eduCode.code_description = CAST(p.education->>'display' AS VARCHAR) AND eduCode.code_set_nm = 'EDUCATIONAL_LEVEL'\n" +
            "WHERE p.uuid = ?1\n" +
            "  AND p.facility_id = ?2\n" +
            "  AND COALESCE(p.archived, 0) = 0\n" +
            "LIMIT 1", nativeQuery = true)
    Optional<PatientDemographicDTO> getPmtctPatientDemographics(String identifier, Long facilityId, LocalDateTime lastModified);

    @Query(value = "SELECT\n" +
            "    CAST(COALESCE(anc.uuid, pe.uuid) AS VARCHAR) AS visitId,\n" +
            "    COALESCE(anc.date_of_enrollment, pe.pmtct_enrollment_date) AS visitDate,\n" +
            "    CAST(p.hospital_number AS VARCHAR) AS hospitalNo,\n" +
            "    CAST(anc.anc_no AS VARCHAR) AS ancNumber,\n" +
            "    CAST(COALESCE(mv_latest.current_status, phts.pregnancy_status_at_entry, pe.entry_point, 'P') AS VARCHAR) AS pregnancyBreastfeedingStatus,\n" +
            "    CAST(COALESCE(anc.gaweeks, pe.gaweeks) AS INTEGER) AS gestationalAgeAtBooking,\n" +
            "    CAST(COALESCE(anc.previously_known_hiv_status, pe.time_of_hiv_diagnosis) AS VARCHAR) AS previouslyKnownHivStatus,\n" +
            "    CAST(COALESCE(anc.currently_on_art, CASE WHEN pe.art_start_date IS NOT NULL THEN 'Yes' END) AS VARCHAR) AS currentlyOnArt,\n" +
            "    CAST(COALESCE(hts_en.hiv_early_acute, phts.hiv_early_detect) AS VARCHAR) AS hivEarlyAcute,\n" +
            "    CAST(COALESCE(hts_en.hiv_early_vl, phts.hiv_early_detect_viral_load) AS VARCHAR) AS hivEarlyViralLoad,\n" +
            "    CAST(COALESCE(phts.result_of_hiv_test, CASE WHEN pe.time_of_hiv_diagnosis ILIKE '%ANC%' THEN pe.hiv_status END, hts_en.final_result) AS VARCHAR) AS hivTrAnc,\n" +
            "    CAST(CASE WHEN pe.time_of_hiv_diagnosis ILIKE '%LABOUR%' OR pe.entry_point ILIKE '%LABOUR%' THEN COALESCE(pe.hiv_status, phts.result_of_hiv_test) END AS VARCHAR) AS hivTrLd,\n" +
            "    CAST(CASE WHEN pe.entry_point ILIKE '%POST-PARTUM%' OR pe.entry_point ILIKE '%BREASTFEEDING%' THEN COALESCE(pe.hiv_status, phts.result_of_hiv_test) END AS VARCHAR) AS hivTrBf,\n" +
            "    CAST(CASE WHEN phts.hiv_retesting ILIKE '%ANC%' OR phts.hiv_retesting ILIKE '%Yes%' THEN phts.result_of_hiv_test END AS VARCHAR) AS hivRtAnc,\n" +
            "    CAST(CASE WHEN phts.hiv_retesting ILIKE '%LD%' OR phts.hiv_retesting ILIKE '%LABOUR%' THEN phts.result_of_hiv_test END AS VARCHAR) AS hivRtLd,\n" +
            "    CAST(CASE WHEN phts.hiv_retesting ILIKE '%BF%' OR phts.hiv_retesting ILIKE '%BREASTFEEDING%' THEN phts.result_of_hiv_test END AS VARCHAR) AS hivRtBf,\n" +
            "    pe.art_start_date AS dateOfInitiation,\n" +
            "    CAST(pe.art_start_time AS VARCHAR) AS timingOfArtInitiation,\n" +
            "    CAST(COALESCE(NULLIF(anc.syphilis_info->>'testResultSyphilis', ''), NULLIF(pe.syphilis_details->>'testResult', ''), NULLIF(mv_latest.syphilis_test_result, '')) AS VARCHAR) AS syphilisTestResult,\n" +
            "    CAST(COALESCE(NULLIF(anc.syphilis_info->>'treatedSyphilis', ''), NULLIF(pe.syphilis_details->>'treatment', ''), NULLIF(mv_latest.syphilis_treated, '')) AS VARCHAR) AS syphilisTreated,\n" +
            "    CAST(COALESCE(NULLIF(anc.syphilis_info->>'referredSyphilisTreatment', ''), NULLIF(mv_latest.syphilis_referred, '')) AS VARCHAR) AS syphilisReferred,\n" +
            "    CAST(COALESCE(NULLIF(anc.hepatitis_b_info->>'previouslyKnownPositive', ''), NULLIF(mv_latest.hbv_known_pos, '')) AS VARCHAR) AS hbvKnownPositive,\n" +
            "    CAST(COALESCE(NULLIF(anc.hepatitis_b_info->>'currentHbvStatus', ''), NULLIF(anc.hepatitis_b_info->>'hepatitisB', ''), NULLIF(pe.hbv_details->>'testResult', ''), NULLIF(mv_latest.hbv_status, '')) AS VARCHAR) AS hbvTestResult,\n" +
            "    CAST(COALESCE(NULLIF(anc.hepatitis_b_info->>'treatedHepatitisB', ''), NULLIF(pe.hbv_details->>'treatmentType', ''), NULLIF(mv_latest.hbv_treated, '')) AS VARCHAR) AS hbvTreated,\n" +
            "    CAST(COALESCE(NULLIF(anc.hepatitis_b_info->>'referredHepatitisB', ''), NULLIF(mv_latest.hbv_referred, '')) AS VARCHAR) AS hbvReferred,\n" +
            "    CAST(COALESCE(NULLIF(pe.hbv_details->>'treatmentType', ''), NULLIF(anc.hepatitis_b_info->>'currentHbvStatus', ''), NULLIF(mv_latest.hbv_status, '')) AS VARCHAR) AS initiatedOnProphylaxis,\n" +
            "    CAST(pe.tb_status AS VARCHAR) AS tbScreeningStatus,\n" +
            "    CAST(NULL AS VARCHAR) AS tbTreatmentReferral,\n" +
            "    vl_lt32.date_of_viral_load AS vlLessThan32WeeksDate,\n" +
            "    CAST(vl_lt32.result_of_viral_load AS NUMERIC) AS vlLessThan32WeeksResult,\n" +
            "    vl_32_36.date_of_viral_load AS vl32To36WeeksDate,\n" +
            "    CAST(vl_32_36.result_of_viral_load AS NUMERIC) AS vl32To36WeeksResult,\n" +
            "    vl_bf.date_of_viral_load AS vlBreastfeedingDate,\n" +
            "    CAST(vl_bf.result_of_viral_load AS NUMERIC) AS vlBreastfeedingResult,\n" +
            "    CAST(COALESCE(phts.partner_counseling->>'agreedToPartnerNotification', hts_en.agreed_notification) AS VARCHAR) AS partnerAgreedToNotification,\n" +
            "    CAST(COALESCE(phts.partner_counseling->>'testedHiv', hts_en.partner_tested_hiv) AS VARCHAR) AS partnerTestedHiv,\n" +
            "    CAST(COALESCE(phts.partner_counseling->>'testedSyphilis', hts_en.partner_tested_syphilis) AS VARCHAR) AS partnerTestedSyphilis,\n" +
            "    CAST(COALESCE(phts.partner_counseling->>'testedHbv', hts_en.partner_tested_hbv) AS VARCHAR) AS partnerTestedHepatitisB,\n" +
            "    CAST(COALESCE(phts.partner_counseling->>'referral', hts_en.partner_referral) AS VARCHAR) AS partnerReferral\n" +
            "FROM patient_person p\n" +
            "LEFT JOIN pmtct_anc anc\n" +
            "    ON anc.patient_uuid = p.uuid\n" +
            "   AND anc.facility_id = ?1\n" +
            "   AND COALESCE(anc.archived, false) = false\n" +
            "FULL OUTER JOIN pmtct_enrollment pe\n" +
            "    ON pe.patient_uuid = p.uuid\n" +
            "   AND pe.facility_id = ?1\n" +
            "   AND COALESCE(pe.archived, false) = false\n" +
            "   AND (anc.pmtct_cycle_uuid IS NULL OR pe.pmtct_cycle_uuid = anc.pmtct_cycle_uuid)\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_hts ph\n" +
            "    WHERE ph.patient_uuid = COALESCE(anc.patient_uuid, pe.patient_uuid)\n" +
            "      AND COALESCE(ph.archived, false) = false\n" +
            "    ORDER BY ph.id DESC\n" +
            "    LIMIT 1\n" +
            ") phts ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT\n" +
            "        observation->>'hivEarlyDetectResult' AS hiv_early_acute,\n" +
            "        observation->>'hivEarlyDetectViralLoad' AS hiv_early_vl,\n" +
            "        observation->>'finalHivTestResult' AS final_result,\n" +
            "        observation->>'agreedToPartnerNotification' AS agreed_notification,\n" +
            "        observation->>'testedHiv' AS partner_tested_hiv,\n" +
            "        observation->>'testedSyphilis' AS partner_tested_syphilis,\n" +
            "        observation->>'testedHbv' AS partner_tested_hbv,\n" +
            "        observation->>'referral' AS partner_referral\n" +
            "    FROM hts_encounter he\n" +
            "    WHERE he.patient_uuid = COALESCE(anc.patient_uuid, pe.patient_uuid)\n" +
            "      AND COALESCE(he.archived, 0) = 0\n" +
            "    ORDER BY he.id DESC\n" +
            "    LIMIT 1\n" +
            ") hts_en ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT\n" +
            "        mv.current_status,\n" +
            "        mv.syphilis_info->>'testResultSyphilis' AS syphilis_test_result,\n" +
            "        mv.syphilis_info->>'treatedSyphilis' AS syphilis_treated,\n" +
            "        mv.syphilis_info->>'referredSyphilisTreatment' AS syphilis_referred,\n" +
            "        mv.hepatitis_b_info->>'previouslyKnownPositive' AS hbv_known_pos,\n" +
            "        COALESCE(NULLIF(mv.hepatitis_b_info->>'currentHbvStatus', ''), NULLIF(mv.hepatitis_b_info->>'hepatitisB', '')) AS hbv_status,\n" +
            "        mv.hepatitis_b_info->>'treatedHepatitisB' AS hbv_treated,\n" +
            "        mv.hepatitis_b_info->>'referredHepatitisB' AS hbv_referred\n" +
            "    FROM pmtct_mother_visitation mv\n" +
            "    WHERE mv.patient_uuid = COALESCE(anc.patient_uuid, pe.patient_uuid)\n" +
            "      AND COALESCE(mv.archived, false) = false\n" +
            "    ORDER BY mv.date_of_visit DESC NULLS LAST, mv.id DESC\n" +
            "    LIMIT 1\n" +
            ") mv_latest ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT mv.date_of_viral_load, mv.result_of_viral_load\n" +
            "    FROM pmtct_mother_visitation mv\n" +
            "    WHERE mv.patient_uuid = COALESCE(anc.patient_uuid, pe.patient_uuid)\n" +
            "      AND COALESCE(mv.archived, false) = false\n" +
            "      AND mv.date_of_viral_load IS NOT NULL\n" +
            "      AND mv.result_of_viral_load IS NOT NULL\n" +
            "      AND (mv.time_of_viral_load ILIKE '%Other%' OR COALESCE(mv.ga_of_viral_load, mv.ga_weeks, 0) < 32)\n" +
            "      AND COALESCE(mv.current_status, '') NOT ILIKE '%Breastfeeding%'\n" +
            "    ORDER BY mv.date_of_viral_load DESC, mv.id DESC\n" +
            "    LIMIT 1\n" +
            ") vl_lt32 ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT mv.date_of_viral_load, mv.result_of_viral_load\n" +
            "    FROM pmtct_mother_visitation mv\n" +
            "    WHERE mv.patient_uuid = COALESCE(anc.patient_uuid, pe.patient_uuid)\n" +
            "      AND COALESCE(mv.archived, false) = false\n" +
            "      AND mv.date_of_viral_load IS NOT NULL\n" +
            "      AND mv.result_of_viral_load IS NOT NULL\n" +
            "      AND (mv.time_of_viral_load ILIKE '%32%' OR (COALESCE(mv.ga_of_viral_load, mv.ga_weeks, 0) BETWEEN 32 AND 36))\n" +
            "    ORDER BY mv.date_of_viral_load DESC, mv.id DESC\n" +
            "    LIMIT 1\n" +
            ") vl_32_36 ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT mv.date_of_viral_load, mv.result_of_viral_load\n" +
            "    FROM pmtct_mother_visitation mv\n" +
            "    WHERE mv.patient_uuid = COALESCE(anc.patient_uuid, pe.patient_uuid)\n" +
            "      AND COALESCE(mv.archived, false) = false\n" +
            "      AND mv.date_of_viral_load IS NOT NULL\n" +
            "      AND mv.result_of_viral_load IS NOT NULL\n" +
            "      AND (mv.current_status ILIKE '%Breastfeeding%' OR mv.time_of_viral_load ILIKE '%Breastfeeding%')\n" +
            "    ORDER BY mv.date_of_viral_load DESC, mv.id DESC\n" +
            "    LIMIT 1\n" +
            ") vl_bf ON TRUE\n" +
            "WHERE COALESCE(anc.patient_uuid, pe.patient_uuid) = ?2\n" +
            "  AND p.uuid = ?2\n" +
            "  AND COALESCE(anc.uuid, pe.uuid) IS NOT NULL\n" +
            "  AND COALESCE(anc.date_of_enrollment, pe.pmtct_enrollment_date) IS NOT NULL", nativeQuery = true)
    List<PMTCTRegisterProjection> getPmtctRegisterByPatient(Long facilityId, String patientUuid, LocalDateTime lastModified);

    @Query(value = "SELECT\n" +
            "    CAST(mv.uuid AS VARCHAR) AS visitId,\n" +
            "    mv.date_of_visit AS visitDate,\n" +
            "    CAST(NULL AS BOOLEAN) AS scheduled,\n" +
            "    CAST(mv.ga_weeks AS INTEGER) AS gestationalAgeWeeks,\n" +
            "    CAST(NULLIF(REGEXP_REPLACE(COALESCE(mv.vital_signs->>'weight', ''), '[^0-9.]', '', 'g'), '') AS NUMERIC) AS motherWeightKg,\n" +
            "    CAST(NULLIF(REGEXP_REPLACE(COALESCE(mv.vital_signs->>'sfhLength', ''), '[^0-9.]', '', 'g'), '') AS NUMERIC) AS sfhLengthCm,\n" +
            "    CAST(mv.current_status AS VARCHAR) AS motherCurrentStatus,\n" +
            "    CAST(mv.current_art_status AS VARCHAR) AS motherCurrentArtStatus,\n" +
            "    CAST(COALESCE(reg_info.regimen_code, NULLIF(mv.regimen_line_id, '')) AS VARCHAR) AS regimenCode,\n" +
            "    CAST(COALESCE(reg_info.regimen_desc, NULLIF(mv.mothers_art_regimen, '')) AS VARCHAR) AS regimenDescription,\n" +
            "    CAST(reg_info.ndr_code AS VARCHAR) AS regimenNdrCode,\n" +
            "    CAST(COALESCE(NULLIF(mv.hepatitis_b_info->>'currentHbvStatus', ''), NULLIF(mv.hepatitis_b_info->>'hepatitisB', '')) AS VARCHAR) AS motherCurrentHbvStatus,\n" +
            "    CAST(NULLIF(mv.hepatitis_b_info->>'nameOfHbvDrug', '') AS VARCHAR) AS motherCurrentHbvDrugName,\n" +
            "    CAST(COALESCE(NULLIF(mv.syphilis_info->>'currentSyphilisStatus', ''), NULLIF(mv.syphilis_info->>'testResultSyphilis', '')) AS VARCHAR) AS motherCurrentSyphilisStatus,\n" +
            "    CAST(NULLIF(mv.syphilis_info->>'nameOfSyphilisDrug', '') AS VARCHAR) AS syphilisDrugAdministered,\n" +
            "    mv.date_of_viral_load AS vlDateSampleCollected,\n" +
            "    mv.date_of_vl_result_received AS vlDateResultReceived,\n" +
            "    CAST(mv.result_of_viral_load AS NUMERIC) AS vlResultCopiesPerMl,\n" +
            "    CAST(mv.infant_feeding_practice AS VARCHAR) AS infantFeedingPractice,\n" +
            "    CAST(mv.infant_on_ctx AS VARCHAR) AS infantOnCtx,\n" +
            "    CAST(mv.referred_to_treatment AS VARCHAR) AS referredToTreatment,\n" +
            "    mv.next_appointment_date AS nextAppointmentDate\n" +
            "FROM pmtct_mother_visitation mv\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT\n" +
            "        COALESCE(ncs_reg.code, ncs_others.code) AS regimen_code,\n" +
            "        COALESCE(ncs_reg.code_description, ncs_others.code_description, hr.description) AS regimen_desc,\n" +
            "        COALESCE(ncs_reg.ndr_code, ncs_others.ndr_code) AS ndr_code\n" +
            "    FROM hiv_regimen hr\n" +
            "    LEFT JOIN hiv_regimen_resolver hrr ON hrr.regimensys = hr.description OR hrr.regimensys = hr.composition\n" +
            "    LEFT JOIN ndr_code_set ncs_reg ON ncs_reg.code_description = hrr.regimen AND ncs_reg.code_set_nm = 'ARV_REGIMEN'\n" +
            "    LEFT JOIN ndr_code_set ncs_others ON ncs_others.code_description = hr.description\n" +
            "    WHERE hr.description = mv.mothers_art_regimen OR CAST(hr.id AS VARCHAR) = mv.mothers_art_regimen\n" +
            "    LIMIT 1\n" +
            ") reg_info ON TRUE\n" +
            "WHERE mv.facility_id = ?1\n" +
            "  AND mv.patient_uuid = ?2\n" +
            "  AND COALESCE(mv.archived, false) = false\n" +
            "  AND mv.uuid IS NOT NULL\n" +
            "  AND mv.date_of_visit IS NOT NULL\n" +
            "ORDER BY mv.date_of_visit ASC, mv.id ASC", nativeQuery = true)
    List<MotherInfantPairVisitProjection> getMotherInfantPairVisitsByPatient(Long facilityId, String patientUuid, LocalDateTime lastModified);

    @Query(value = "SELECT\n" +
            "    CAST(d.uuid AS VARCHAR) AS deliveryUuid,\n" +
            "    CAST(ii.uuid AS VARCHAR) AS infantUuid,\n" +
            "    COALESCE(ii.date_of_delivery, d.date_of_delivery) AS dateOfDelivery,\n" +
            "    CAST(d.mode_of_delivery AS VARCHAR) AS modeOfDelivery,\n" +
            "    CAST(COALESCE(NULLIF(ii.body_weight, ''), NULLIF(d.baby_info->>'babyLiveBirthWeight', ''), NULLIF(d.baby_info->'aliveOutcomes'->0->>'babyLiveBirthWeight', '')) AS VARCHAR) AS birthWeightKg,\n" +
            "    CAST(NULLIF(REGEXP_REPLACE(COALESCE(ii.length, ''), '[^0-9.]', '', 'g'), '') AS NUMERIC) AS birthLengthCm,\n" +
            "    CAST(COALESCE(NULLIF(ii.birth_outcome, ''), NULLIF(d.child_status, ''), CASE WHEN COALESCE(d.number_of_infants_alive, 0) > 0 THEN 'Alive' WHEN COALESCE(d.number_of_infants_dead, 0) > 0 THEN 'Dead' END) AS VARCHAR) AS birthOutcome,\n" +
            "    CAST(COALESCE(NULLIF(ii.infant_hospital_number, ''), NULLIF(iv_latest.infant_hospital_number, '')) AS VARCHAR) AS childHospitalNumber,\n" +
            "    CAST(ii.entry_point AS VARCHAR) AS childEntryPoint,\n" +
            "    CAST(COALESCE(NULLIF(d.baby_info->>'sexOfBaby', ''), NULLIF(d.baby_info->'aliveOutcomes'->0->>'sexOfBaby', '')) AS VARCHAR) AS sex,\n" +
            "    CAST(d.child_given_arv_within72 AS VARCHAR) AS childGivenArvWithin72,\n" +
            "    CAST(d.baby_info AS VARCHAR) AS babyInfoJson,\n" +
            "    CAST(COALESCE(ii.infant_arv_data, iv_latest.infant_arv_data) AS VARCHAR) AS infantArvDataJson,\n" +
            "    CAST(ii.syphilis_prophylaxis AS VARCHAR) AS syphilisProphylaxisJson,\n" +
            "    CAST(ii.hbv_vaccinations AS VARCHAR) AS hbvVaccinationsJson,\n" +
            "    CAST(COALESCE(iv_latest.infant_pcr_data, ii.infant_pcr_data) AS VARCHAR) AS infantPcrDataJson,\n" +
            "    CAST(iv_latest.hbv_vaccination_data AS VARCHAR) AS visitHbvVaccinationDataJson,\n" +
            "    CAST(iv_latest.rapid_test_data AS VARCHAR) AS rapidTestDataJson,\n" +
            "    CAST(iv_latest.infant_outcome_at18_months AS VARCHAR) AS outcomeAt18Months,\n" +
            "    iarv.date_of_arv AS relArvDate,\n" +
            "    CAST(iarv.infant_arv_type AS VARCHAR) AS relArvType,\n" +
            "    CAST(iarv.infant_arv_time AS VARCHAR) AS relArvTime,\n" +
            "    CAST(iarv.other_prophylaxis_type AS VARCHAR) AS relArvOtherSpecify,\n" +
            "    iarv.date_of_ctx AS relCtxDate,\n" +
            "    CAST(iarv.age_at_ctx AS VARCHAR) AS relCtxAge,\n" +
            "    CAST(ipcr.pcr_list AS VARCHAR) AS relPcrListJson,\n" +
            "    CAST(irapid.rapid_json AS VARCHAR) AS relRapidTestJson\n" +
            "FROM (\n" +
            "    SELECT * FROM pmtct_delivery\n" +
            "    WHERE facility_id = ?1 AND patient_uuid = ?2 AND COALESCE(archived, false) = false\n" +
            ") d\n" +
            "FULL OUTER JOIN (\n" +
            "    SELECT * FROM pmtct_infant_information\n" +
            "    WHERE facility_id = ?1 AND mother_patient_uuid = ?2 AND COALESCE(archived, false) = false\n" +
            ") ii ON (ii.pmtct_cycle_uuid = d.pmtct_cycle_uuid OR ii.date_of_delivery = d.date_of_delivery)\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_infant_visit iv\n" +
            "    WHERE iv.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(iv.archived, false) = false\n" +
            "      AND (ii.infant_hospital_number IS NULL OR iv.infant_hospital_number = ii.infant_hospital_number OR iv.pmtct_cycle_uuid = COALESCE(ii.pmtct_cycle_uuid, d.pmtct_cycle_uuid))\n" +
            "    ORDER BY iv.visit_date DESC NULLS LAST, iv.id DESC\n" +
            "    LIMIT 1\n" +
            ") iv_latest ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_infant_arv ia\n" +
            "    WHERE ia.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(ia.archived, false) = false\n" +
            "    ORDER BY ia.visit_date DESC NULLS LAST, ia.id DESC\n" +
            "    LIMIT 1\n" +
            ") iarv ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT json_agg(jsonb_build_object(\n" +
            "        'testType', ip.test_type,\n" +
            "        'ageAtTest', ip.age_at_test,\n" +
            "        'dateSampleCollected', ip.date_sample_collected,\n" +
            "        'dateResultReceivedByCaregiver', COALESCE(ip.date_result_received_by_caregiver, ip.date_result_received_at_facility),\n" +
            "        'results', ip.results\n" +
            "    )) AS pcr_list\n" +
            "    FROM pmtct_infant_pcr ip\n" +
            "    WHERE ip.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(ip.archived, false) = false\n" +
            ") ipcr ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT jsonb_build_object(\n" +
            "        'ageAtTest', ir.age_at_test,\n" +
            "        'dateOfTest', COALESCE(ir.date_of_test, ir.visit_date),\n" +
            "        'result', COALESCE(NULLIF(ir.result, ''), NULLIF(ir.rapid_result, ''))\n" +
            "    ) AS rapid_json\n" +
            "    FROM pmtct_infant_rapid_antibody ir\n" +
            "    WHERE ir.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(ir.archived, false) = false\n" +
            "    ORDER BY ir.visit_date DESC NULLS LAST, ir.id DESC\n" +
            "    LIMIT 1\n" +
            ") irapid ON TRUE", nativeQuery = true)
    List<DeliveryChildrenDetailsProjection> getDeliveryChildrenDetailsByPatient(Long facilityId, String patientUuid, LocalDateTime lastModified);

    @Query(value = "SELECT\n" +
            "    CAST(COALESCE(ii.uuid, iv.uuid) AS VARCHAR) AS infantUuid,\n" +
            "    COALESCE(ii.date_of_delivery, d.date_of_delivery) AS dateOfBirth,\n" +
            "    iv.visit_date AS dateOfFirstVisit,\n" +
            "    CAST(CASE\n" +
            "        WHEN iv.visit_date IS NOT NULL AND COALESCE(ii.date_of_delivery, d.date_of_delivery) IS NOT NULL\n" +
            "             AND iv.visit_date >= COALESCE(ii.date_of_delivery, d.date_of_delivery)\n" +
            "        THEN EXTRACT(DAY FROM AGE(iv.visit_date, COALESCE(ii.date_of_delivery, d.date_of_delivery))) / 7\n" +
            "        ELSE NULL\n" +
            "    END AS INTEGER) AS ageAtFirstVisit,\n" +
            "    CAST(COALESCE(NULLIF(ii.infant_hospital_number, ''), NULLIF(iv.infant_hospital_number, ''), NULLIF(iarv.infant_hospital_number, '')) AS VARCHAR) AS childHospitalRegNo,\n" +
            "    CAST(COALESCE(NULLIF(d.baby_info->>'sexOfBaby', ''), NULLIF(d.baby_info->'aliveOutcomes'->0->>'sexOfBaby', '')) AS VARCHAR) AS infantSex,\n" +
            "    CAST(ii.entry_point AS VARCHAR) AS childEntryPoint,\n" +
            "    CAST(p.hospital_number AS VARCHAR) AS motherHospitalNumber,\n" +
            "    CAST(COALESCE(NULLIF(iv.anc_number, ''), NULLIF(anc.anc_no, ''), NULLIF(iarv.anc_number, '')) AS VARCHAR) AS motherAncNumber,\n" +
            "    CAST(COALESCE(NULLIF(iv.mother_art_data->>'motherCurrentArtStatus', ''), 'HIV+') AS VARCHAR) AS motherStatus,\n" +
            "    CAST(COALESCE(NULLIF(iv.mother_art_data->>'motherArtInitiationTime', ''), NULLIF(ima.mother_art_initiation_time, ''), NULLIF(pe.art_start_time, '')) AS VARCHAR) AS motherArtInitiationTiming,\n" +
            "    CAST(COALESCE(reg_info.regimen_code, NULLIF(mv.regimen_line_id, '')) AS VARCHAR) AS motherRegimenCode,\n" +
            "    CAST(COALESCE(reg_info.regimen_desc, NULLIF(mv.mothers_art_regimen, ''), NULLIF(pe.regimen_id, '')) AS VARCHAR) AS motherRegimenDescription,\n" +
            "    CAST(reg_info.ndr_code AS VARCHAR) AS motherRegimenNdrCode,\n" +
            "    CAST(COALESCE(NULLIF(mv.syphilis_info->>'date', ''), NULLIF(pe.syphilis_details->>'date', '')) AS VARCHAR) AS motherSyphilisTreatmentStartDate,\n" +
            "    CAST(COALESCE(NULLIF(mv.hepatitis_b_info->>'dateOfHepatitisB', ''), NULLIF(pe.hbv_details->>'vlResultDate', '')) AS VARCHAR) AS motherHepatitisBTreatmentStartDate,\n" +
            "    d.hiv_exposed_infant_given_hb_within24hrs AS hivExposedInfantGivenHbWithin24hrs,\n" +
            "    d.non_hbv_exposed_infant_given_hb_within_24hrs AS nonHbvExposedInfantGivenHbWithin24hrs,\n" +
            "    CAST(ii.hbv_vaccinations AS VARCHAR) AS hbvVaccinationsJson,\n" +
            "    CAST(iv.hbv_vaccination_data AS VARCHAR) AS visitHbvVaccinationDataJson,\n" +
            "    CAST(COALESCE(ii.infant_arv_data, iv.infant_arv_data) AS VARCHAR) AS infantArvDataJson,\n" +
            "    CAST(iarv.infant_arv_type AS VARCHAR) AS relArvType,\n" +
            "    CAST(iarv.infant_arv_time AS VARCHAR) AS relArvTime,\n" +
            "    CAST(iarv.arv_delivery_point AS VARCHAR) AS relArvDeliveryPoint,\n" +
            "    CAST(iarv.timing_of_avr_within_72hours AS VARCHAR) AS relTimingWithin72,\n" +
            "    CAST(iarv.timing_of_avr_after_72hours AS VARCHAR) AS relTimingAfter72,\n" +
            "    CAST(iarv.age_at_ctx AS VARCHAR) AS relAgeAtCtx,\n" +
            "    CAST(COALESCE(iv.infant_pcr_data, ii.infant_pcr_data) AS VARCHAR) AS infantPcrDataJson,\n" +
            "    CAST(ipcr.pcr_list AS VARCHAR) AS relPcrListJson,\n" +
            "    CAST(iv.rapid_test_data AS VARCHAR) AS rapidTestDataJson,\n" +
            "    CAST(irapid.rapid_json AS VARCHAR) AS relRapidTestJson,\n" +
            "    CAST(iv.infant_outcome_at18_months AS VARCHAR) AS outcomeAt18Months,\n" +
            "    iv.date_linked_to_art_clinic AS dateLinkedToArtClinic,\n" +
            "    CAST(iv.art_enrollment_no AS VARCHAR) AS artEnrollmentNumber\n" +
            "FROM patient_person p\n" +
            "LEFT JOIN (\n" +
            "    SELECT * FROM pmtct_infant_information\n" +
            "    WHERE facility_id = ?1 AND mother_patient_uuid = ?2 AND COALESCE(archived, false) = false\n" +
            ") ii ON ii.mother_patient_uuid = p.uuid\n" +
            "FULL OUTER JOIN (\n" +
            "    SELECT DISTINCT ON (COALESCE(NULLIF(infant_hospital_number, ''), uuid)) *\n" +
            "    FROM pmtct_infant_visit\n" +
            "    WHERE facility_id = ?1 AND mother_patient_uuid = ?2 AND COALESCE(archived, false) = false\n" +
            "    ORDER BY COALESCE(NULLIF(infant_hospital_number, ''), uuid), visit_date ASC NULLS LAST, id ASC\n" +
            ") iv ON iv.mother_patient_uuid = ii.mother_patient_uuid\n" +
            "    AND (ii.infant_hospital_number IS NULL OR iv.infant_hospital_number IS NULL OR iv.infant_hospital_number = ii.infant_hospital_number OR iv.pmtct_cycle_uuid = ii.pmtct_cycle_uuid)\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_delivery pd\n" +
            "    WHERE pd.patient_uuid = ?2\n" +
            "      AND COALESCE(pd.archived, false) = false\n" +
            "    ORDER BY pd.date_of_delivery DESC NULLS LAST, pd.id DESC\n" +
            "    LIMIT 1\n" +
            ") d ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_anc pa\n" +
            "    WHERE pa.patient_uuid = ?2\n" +
            "      AND COALESCE(pa.archived, false) = false\n" +
            "    ORDER BY pa.date_of_enrollment DESC NULLS LAST, pa.id DESC\n" +
            "    LIMIT 1\n" +
            ") anc ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_enrollment pe_sub\n" +
            "    WHERE pe_sub.patient_uuid = ?2\n" +
            "      AND COALESCE(pe_sub.archived, false) = false\n" +
            "    ORDER BY pe_sub.pmtct_enrollment_date DESC NULLS LAST, pe_sub.id DESC\n" +
            "    LIMIT 1\n" +
            ") pe ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_mother_visitation pmv\n" +
            "    WHERE pmv.patient_uuid = ?2\n" +
            "      AND COALESCE(pmv.archived, false) = false\n" +
            "    ORDER BY pmv.date_of_visit DESC NULLS LAST, pmv.id DESC\n" +
            "    LIMIT 1\n" +
            ") mv ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_infant_mother_art pima\n" +
            "    WHERE pima.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(pima.archived, false) = false\n" +
            "    ORDER BY pima.visit_date DESC NULLS LAST, pima.id DESC\n" +
            "    LIMIT 1\n" +
            ") ima ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT\n" +
            "        COALESCE(ncs_reg.code, ncs_others.code) AS regimen_code,\n" +
            "        COALESCE(ncs_reg.code_description, ncs_others.code_description, hr.description) AS regimen_desc,\n" +
            "        COALESCE(ncs_reg.ndr_code, ncs_others.ndr_code) AS ndr_code\n" +
            "    FROM hiv_regimen hr\n" +
            "    LEFT JOIN hiv_regimen_resolver hrr ON hrr.regimensys = hr.description OR hrr.regimensys = hr.composition\n" +
            "    LEFT JOIN ndr_code_set ncs_reg ON ncs_reg.code_description = hrr.regimen AND ncs_reg.code_set_nm = 'ARV_REGIMEN'\n" +
            "    LEFT JOIN ndr_code_set ncs_others ON ncs_others.code_description = hr.description\n" +
            "    WHERE CAST(hr.id AS VARCHAR) = COALESCE(NULLIF(iv.mother_art_data->>'regimenId', ''), NULLIF(ima.regimen_id, ''))\n" +
            "       OR hr.description = COALESCE(NULLIF(mv.mothers_art_regimen, ''), NULLIF(pe.regimen_id, ''))\n" +
            "    LIMIT 1\n" +
            ") reg_info ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT *\n" +
            "    FROM pmtct_infant_arv ia\n" +
            "    WHERE ia.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(ia.archived, false) = false\n" +
            "    ORDER BY ia.visit_date DESC NULLS LAST, ia.id DESC\n" +
            "    LIMIT 1\n" +
            ") iarv ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT json_agg(jsonb_build_object(\n" +
            "        'testType', ip.test_type,\n" +
            "        'ageAtTest', ip.age_at_test,\n" +
            "        'dateSampleCollected', ip.date_sample_collected,\n" +
            "        'dateResultReceivedByCaregiver', COALESCE(ip.date_result_received_by_caregiver, ip.date_result_received_at_facility),\n" +
            "        'results', ip.results\n" +
            "    )) AS pcr_list\n" +
            "    FROM pmtct_infant_pcr ip\n" +
            "    WHERE ip.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(ip.archived, false) = false\n" +
            ") ipcr ON TRUE\n" +
            "LEFT JOIN LATERAL (\n" +
            "    SELECT jsonb_build_object(\n" +
            "        'ageAtTest', ir.age_at_test,\n" +
            "        'dateOfTest', COALESCE(ir.date_of_test, ir.visit_date),\n" +
            "        'result', COALESCE(NULLIF(ir.result, ''), NULLIF(ir.rapid_result, ''))\n" +
            "    ) AS rapid_json\n" +
            "    FROM pmtct_infant_rapid_antibody ir\n" +
            "    WHERE ir.mother_patient_uuid = ?2\n" +
            "      AND COALESCE(ir.archived, false) = false\n" +
            "    ORDER BY ir.visit_date DESC NULLS LAST, ir.id DESC\n" +
            "    LIMIT 1\n" +
            ") irapid ON TRUE\n" +
            "WHERE p.uuid = ?2\n" +
            "  AND COALESCE(ii.uuid, iv.uuid) IS NOT NULL", nativeQuery = true)
    List<InfantCohortRegistrationProjection> getInfantCohortRegistrationsByPatient(Long facilityId, String patientUuid, LocalDateTime lastModified);
}


