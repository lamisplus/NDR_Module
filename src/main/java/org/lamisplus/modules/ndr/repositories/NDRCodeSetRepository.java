package org.lamisplus.modules.ndr.repositories;

import org.lamisplus.modules.ndr.domain.dto.ArtCommencementDTO;
import org.lamisplus.modules.ndr.domain.dto.BiometricDto;
import org.lamisplus.modules.ndr.domain.dto.RecaptureBiometricDTO;
import org.lamisplus.modules.ndr.domain.entities.NDRCodeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NDRCodeSetRepository extends JpaRepository<NDRCodeSet, String> {
    Optional<NDRCodeSet> getNDRCodeSetByCodeSetNmAndSysDescription(String codeSetNm, String sysDescription);
    
    Optional<NDRCodeSet> getNDRCodeSetBySysDescription(String sysDescription);

    Optional<NDRCodeSet> getNDRCodeSetByCodeDescription(String codeDescription);

    @Query(value = "select r.regimen from hiv_regimen_resolver r where r.regimensys = ? limit 1", nativeQuery = true)
    Optional<String> getNDREquivalentRegimenUsingSystemRegimen(String systemRegimen);


//    @Query(value = "select template_type as templateType, enrollment_date as enrollmentDate, template, image_quality as quality, replace_date as replaceDate, recapture as replacePrintCount from biometric where person_uuid = :patientUuid" +
//            " and biometric_type = 'FINGERPRINT' and archived = 0  and recapture = 0 and version_iso_20 = true  and iso = true", nativeQuery = true)
    @Query(value = "select template_type as templateType, enrollment_date as enrollmentDate, template, image_quality as quality from biometric where person_uuid = :patientUuid" +
        " and biometric_type = 'FINGERPRINT' and archived = 0  and recapture = 0 and version_iso_20 = true  and iso = true", nativeQuery = true)
    List<BiometricDto> getPatientBiometricByPatientUuid(String patientUuid);
    
    
    @Query(value = "SELECT template_type as templateType, \n" +
            "enrollment_date as enrollmentDate,\n" +
            "recapture as count,\n" +
            "template,\n" +
            "image_quality as quality,\n" +
            "hashed as templateTypeHash\n" +
            "FROM biometric where person_uuid = ?1 \n" +
            "AND biometric_type = 'FINGERPRINT' and archived = 0\n" +
            "AND recapture > 0\n" +
            "AND version_iso_20 = true  and iso = true\n" +
            "AND enrollment_date >= ?2 \n" +
            "ORDER BY enrollment_date DESC LIMIT 10;", nativeQuery = true)
    List<RecaptureBiometricDTO> getPatientRecapturedBiometricByPatientUuid(String patientUuid, LocalDate previousUploadDate);
    
    @Query(value = "SELECT template_type as templateType, \n" +
            "enrollment_date as enrollmentDate,\n" +
            "recapture as count,\n" +
            "template,\n" +
            "image_quality as quality,\n" +
            "hashed as templateTypeHash\n" +
            "FROM biometric where person_uuid = ?1 \n" +
            "AND biometric_type = 'FINGERPRINT' and archived = 0\n" +
            "AND recapture > 0\n" +
            "AND version_iso_20 = true  and iso = true\n" +
            "ORDER BY enrollment_date DESC LIMIT 10 ", nativeQuery = true)
    List<RecaptureBiometricDTO> getPatientRecapturedBiometricByPatientUuid(String patientUuid);
    
   
   @Query(value = "SELECT cd_4 as cd4, cd_4_percentage as cd4Percentage, arc.visit_date AS artStartDate,sgn.body_weight AS bodyWeight,\n" +
           "sgn.height, who.display AS whoStage, funstatus.display AS functionStatus, \n" +
           "reg.description AS regimen \n" +
           "FROM  hiv_art_clinical arc \n" +
           "INNER JOIN triage_vital_sign sgn ON arc.visit_id = sgn.visit_id \n" +
           "LEFT JOIN base_application_codeset who ON who.id = arc.who_staging_id \n" +
           "LEFT JOIN base_application_codeset funstatus ON  funstatus.id = arc.functional_status_id \n" +
           "LEFT JOIN hiv_regimen reg ON reg.id = arc.regimen_id\n" +
           "WHERE is_commencement IS TRUE \n" +
           "AND arc.archived = 0 "+
           "AND  arc.person_uuid = ?1 ORDER BY arc.visit_date DESC LIMIT 1", nativeQuery = true)
   Optional<ArtCommencementDTO> getArtCommencementByPatientUuid(String patientUuid);
   
   @Query(value = "SELECT  arc.person_uuid as personUuid \n" +
           "FROM  hiv_art_clinical arc \n" +
           "INNER JOIN patient_person p ON arc.person_uuid = p.uuid \n" +
           "WHERE is_commencement IS TRUE \n" +
           "AND p.facility_id = ?1 "+
           "AND arc.archived = 0  "+
           "AND p.archived = 0 ", nativeQuery = true)
   List<String> getNDREligiblePatientUuidList(Long facilityId);
   
   @Query(value = "select person_uuid from hiv_art_pharmacy \n" +
           "where last_modified_date > ?1\n" +
           "and archived = 0\n" +
           "and  facility_id = ?2", nativeQuery = true)
   List<String> getNDREligiblePatientUuidUpdatedListByLastModifyDate(LocalDateTime lastModifyDate, Long facilityId);
   
   
    



}
