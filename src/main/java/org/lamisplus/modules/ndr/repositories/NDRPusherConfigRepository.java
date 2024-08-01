package org.lamisplus.modules.ndr.repositories;

import java.util.Optional;
import org.lamisplus.modules.ndr.domain.entities.NDRPusherConfig;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NDRPusherConfigRepository extends JpaRepository<NDRPusherConfig, Long>
{
    Optional <NDRPusherConfig> findByFacilityId(Long facilityId);
}
