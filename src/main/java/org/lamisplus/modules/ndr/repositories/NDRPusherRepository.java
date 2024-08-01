package org.lamisplus.modules.ndr.repositories;
import org.lamisplus.modules.ndr.domain.entities.NDRPusher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NDRPusherRepository  extends JpaRepository<NDRPusher, Long>{
    @Query(value = "SELECT * FROM public.ndr_pusher where facility_id = ?1 ORDER BY id DESC", nativeQuery = true)
    Page<NDRPusher> findAllNDRPusherByFacId(String queryParam, Integer archived, Long facilityId, Pageable pageable);

}
