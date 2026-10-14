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
            "INNER JOIN prep_eligibility pel ON pel.person_uuid = p.uuid AND pel.archived = 0\n" +
            "INNER JOIN prep_enrollment pe ON pe.prep_eligibility_uuid = pel.uuid AND pe.archived = 0\n" +
            "LEFT JOIN ndr_code_set lgaCode ON TRIM(lgaCode.code_description) = TRIM(facility_lga.name) AND lgaCode.code_set_nm = 'LGA'\n" +
            "LEFT JOIN ndr_code_set stateCode ON TRIM(stateCode.code_description) = TRIM(facility_state.name) AND stateCode.code_set_nm = 'STATES'\n" +
            "LEFT JOIN ndr_code_set emplCode ON emplCode.code_description = p.employment_status->>'display' AND emplCode.code_set_nm = 'OCCUPATION_STATUS'\n" +
            "LEFT JOIN ndr_code_set mariCode ON mariCode.code_description = p.marital_status->>'display' AND mariCode.code_set_nm = 'MARITAL_STATUS'\n" +
            "LEFT JOIN ndr_code_set eduCode ON eduCode.code_description = p.education->>'display' AND eduCode.code_set_nm = 'EDUCATIONAL_LEVEL'\n" +
            "WHERE p.uuid = ?1\n" +
            "AND pe.facility_id = ?2\n" +
            "LIMIT 1", nativeQuery = true)
    Optional<PatientDemographicDTO> getPrepPatientDemographics(String identifier, Long facilityId);

    @Query(value = "SELECT\n" +
            "        ps.unique_Client_Id AS uniqueClientId, \n" +
            "        ps.visit_date AS dateOfVisit, \n" +
            "        ps.referred_from AS referredFrom,\n" +
            "        ps.setting AS setting,\n" +
            "        p.date_of_birth AS dateOfBirth,\n" +
            "        CASE WHEN INITCAP(p.sex) = 'Female' THEN 'F' ELSE 'M' END AS sex,\n" +
            "        ps.visit_type AS visitType,\n" +
            "        ps.population_type AS populationType,\n" +
            "        emplCode.code AS occupation,\n" +
            "        mariCode.code AS maritalStatus,\n" +
            "        eduCode.code AS educationLevel,\n" +
            "        ps.num_children_less_than_five AS numberOfOwnChildrenUnder5,\n" +
            "        ps.type_of_session AS typOfSession,\n" +
            "        ps.sex_partner AS sexPartners,\n" +
            "        hts_en.observation->>'pregnancyStatus' pregnancyStatus,\n" +
            "        CAST(NULLIF(ps.sex_partner_risk->>'haveSexWihHIVPositive', '') AS boolean) AS partnerIsHivPositive,\n" +
            "        CAST(NULLIF(ps.sex_partner_risk->>'haveSexWithPartnerInjectDrug', '') AS boolean) AS partnerInjectsDrugs,\n" +
            "        CAST(NULLIF(ps.sex_partner_risk->>'haveSexWithPartnerWhoHasSexWithMen', '') AS boolean) AS partnerHasSexWithMen,\n" +
            "        CAST(NULLIF(ps.sex_partner_risk->>'haveSexWithPartnerTransgender', '') AS boolean) AS partnerIsTransgender,\n" +
            "        CAST(NULLIF(ps.sex_partner_risk->>'sexWithPartnersWithoutCondoms', '') AS boolean) AS partnerHasMultiplePartnersWithoutCondoms,\n" +
            "        CAST(NULLIF(ps.personal_hiv_risk_assessment->>'unprotectedVaginalSexCasual', '') AS boolean) AS unprotectedVaginalSexWithCasualPartner,\n" +
            "        CAST(NULLIF(ps.personal_hiv_risk_assessment->>'unprotectedAnalSexWithPartner', '') AS boolean) AS unprotectedAnalSex,\n" +
            "        CAST(NULLIF(ps.personal_hiv_risk_assessment->>'sharedNeedles', '') AS boolean) AS sharedNeedlesOrInjectingMaterials,\n" +
            "        CAST(NULLIF(ps.personal_hiv_risk_assessment->>'moreThan1SexPartner', '') AS boolean) AS moreThanOneSexPartner,\n" +
            "        CAST(NULLIF(ps.personal_hiv_risk_assessment->>'haveYouPaidForSex', '') AS boolean) AS paidOrBeenPaidForSex,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS boolean) AS usesCocaine,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS boolean) AS usesHeroin,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS boolean) AS usesMarijuana,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS boolean) AS usesAmphetamine,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS boolean) AS usesCodeineSyrup,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS boolean) AS usesOtherDrugs,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'drug', '') AS varchar) AS otherDrugsSpecify,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'routesOfAdministration', '') AS boolean) AS routeInject,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'routesOfAdministration', '') AS boolean) AS routeSniff,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'routesOfAdministration', '') AS boolean) AS routeSnort,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'routesOfAdministration', '') AS boolean) AS routeSmoke,\n" +
            "\t\tCAST(NULLIF(ps.use_drug_sexual_performance, '') AS boolean) AS usesDrugsForSexualEnhancement,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_pep_indication->>'unprotectedSexWithHivPositiveOrUnknownStatusLast72Hours', '') AS boolean) AS hadSexWithoutCondomUnkHivStatLast72Hours,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_pep_indication->>'sharedInjectionOrNeedleWithHivPositiveOrUnknownStatusLast72Hours', '') AS boolean) AS sharedInjectionEquipmentUnkHivStatLast72Hours,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_acute_hiv_infection->>'acuteHivSymptomsLasttwoWeeks', '') AS boolean) AS hadColdFluSymptomsLast2Weeks,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_acute_hiv_infection->>'unprotectedAnalOrVaginalOrSharedNeedlesLast28Days', '') AS boolean) AS hadUnprotectedSexOrSharedMaterialsLast28Days,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'vaginalDischarge', '') AS boolean) AS femaleVaginalDischargeOrBurning,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'lowerAbdominalPains', '') AS boolean) AS femaleLowerAbdominalPain,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'urethralDischarge', '') AS boolean) AS maleUrethralDischargeOrBurning,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'complaintsOfScrotal', '') AS boolean) AS maleScrotumSwellingAndPain,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'genitalScore', '') AS boolean) AS genitalSores,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'swollenIguinal', '') AS boolean) AS swollenInguinalLymphNodes,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'analpain', '') AS boolean) AS analPainOnStooling,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'analItching', '') AS boolean) AS analItching,\n" +
            "\t\tCAST(NULLIF(ps.sti_screening->>'analDischarge', '') AS boolean) AS analDischarge,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'lastTest', '') AS varchar) AS lastHivTestTimeframe,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'recommendHivRetest', '') AS boolean) AS recommendedForHivRetest,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'clinicalSetting', '') AS boolean) AS testedInOtherClinicalSettings,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'hivTestResultAtvisit', '') AS varchar) AS hivTestResult,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'reportHivRisk', '') AS boolean) AS reportsOngoingHivRiskBehaviors,\n" +
            "\t\tCAST(NULLIF(ps.drug_use_history->>'hivExposure', '') AS boolean) AS reportsSpecificHivExposureLast3Months,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_prep_eligibility->>'hivNegative', '') AS boolean) AS hivNegative,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_prep_eligibility->>'hivRiskScore', '') AS boolean) AS hivRiskScoreAtLeastOne,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_prep_eligibility->>'noSignsAndSymptomsOfAcuteHivInfection', '') AS boolean) AS noSignsOfAcuteHivInfection,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_prep_eligibility->>'noIndicationForPep', '') AS boolean) AS noIndicationForPep,\n" +
            "\t\tCAST(NULLIF(ps.assessment_for_prep_eligibility->>'hasNoProteinuria', '') AS boolean) AS hasNoProteinuria,\n" +
            "\t\tCAST(NULLIF(ps.consideration_for_injections->>'noHistoryOrSignsOfLiverAbnormalitiesInjectable', '') AS boolean) AS noHistoryOfLiverAbnormalities,\n" +
            "\t\tCAST(NULLIF(ps.consideration_for_injections->>'noHistoryOfDrugToDrugInteractionInjectable', '') AS boolean) AS noHistoryOfDrugDrugInteractionsForInjectable,\n" +
            "\t\tCAST(NULLIF(ps.consideration_for_injections->>'noHistoryOfDrugHypersensitivityInjectable', '') AS boolean) AS noHistoryOfDrugHypersensitivity,\n" +
            "\t\tCAST(NULLIF(ps.services_received_by_client->>'prepOffered', '') AS boolean) AS prepOffered,\n" +
            "\t\tCAST(NULLIF(ps.services_received_by_client->>'willingToCommencePrep', '') AS boolean) AS willingToCommencePrep,\n" +
            "\t\tCAST(NULLIF(ps.received_prep_first_time_this_year, '') AS boolean) AS receivedPrepForFirstTimeThisYear,\n" +
            "\t\tCAST(NULLIF(ps.services_received_by_client->>'clientReferredToOtherServices', '') AS boolean) AS clientReferredToOtherServices,\n" +
            "\t\tCAST(NULLIF(ps.services_received_by_client->>'othersSpecify', '') AS varchar) AS referralServiceSpecify\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineNoNeedForPrep,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineDoesNotWishDailyMedication,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineConcernAboutSideEffects,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineConcernAboutStigmatization,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineConcernAboutClinicFollowupTime,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineConcernAboutSafetyOfMedication,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS boolean) AS declineConcernAboutEffectiveness,\n" +
            "-- \t\tCAST(NULLIF(ps.services_received_by_client->>'reasonsForDecline', '') AS varchar) AS declineOtherReasonSpecify\n" +
            "    FROM patient_person p\n" +
            "    INNER JOIN prophylaxis_screening ps ON ps.person_uuid = p.uuid AND ps.archived = false\n" +
            "    LEFT JOIN ndr_code_set emplCode ON emplCode.code_description = p.employment_status->>'display' AND emplCode.code_set_nm = 'OCCUPATION_STATUS'\n" +
            "    LEFT JOIN ndr_code_set mariCode ON mariCode.code_description = p.marital_status->>'display' AND mariCode.code_set_nm = 'MARITAL_STATUS'\n" +
            "    LEFT JOIN ndr_code_set eduCode ON eduCode.code_description = p.education->>'display' AND eduCode.code_set_nm = 'EDUCATIONAL_LEVEL'\n" +
            "    LEFT JOIN hts_encounter hts_en ON hts_en.patient_uuid = p.uuid\n" +
            "    WHERE ps.facility_id= ?1\n" +
            "    AND ps.person_uuid = ?2\n" +
            "    AND ps.archived = false\n" +
            "    LIMIT 1", nativeQuery = true)
    List<PrepDTO> getPrepReportByClientCodeAndLastModified(Long facilityId, String patientId, LocalDateTime lastModified);
}
