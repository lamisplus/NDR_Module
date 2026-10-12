//package org.lamisplus.modules.ndr.repositories;
//import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
//import org.lamisplus.modules.ndr.domain.dto.PrepDTO;
//import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//public interface NDRQueryRepository extends JpaRepository<NdrMessageLog, Integer> {
//    @Query(value = "", nativeQuery = true)
//    Optional<PatientDemographicDTO> getPrepPatientDemographics(long facilityId, String clientCode, LocalDateTime lastModified);
//
//    @Query(value = "", nativeQuery = true)
//    List<PrepDTO> getPrepReportByClientCodeAndLastModified(Long facilityId, String clientCode, LocalDateTime lastModified);
//}
