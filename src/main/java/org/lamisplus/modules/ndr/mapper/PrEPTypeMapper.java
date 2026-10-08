package org.lamisplus.modules.ndr.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PrepDTO;
import org.lamisplus.modules.ndr.schema.HIVTestingReportType;
import org.lamisplus.modules.ndr.schema.IndividualReportType;
import org.lamisplus.modules.ndr.schema.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrEPTypeMapper {
    private final ObjectMapper objectMapper;
    public boolean getPrEPType(
            IndividualReportType individualReportType,
            ObjectFactory objectFactory,
            List<PrepDTO> projections,
            List<NDRErrorDTO> errors) {

        log.info("Prep Mapping started... ");
        if (projections == null || projections.isEmpty()) {
            return false;
        }

        List<HIVTestingReportType> hivTestingReport = individualReportType.getHIVTestingReport();

        projections.parallelStream().forEach(projection -> {
            try {
                HIVTestingReportType reportType = NDRObjectFactory.createHIVTestingReportType();
                mapProjectionToReportType(projection, reportType, objectFactory);
                hivTestingReport.add(reportType);
            } catch (Exception e) {
//                errors.add(new NDRErrorDTO(
//                        projection.getClientCode(),
//                        null,
//                        e.getMessage() + " | " + Arrays.toString(e.getStackTrace())
//                ));
//                log.error("Error mapping projection for client: {}", projection.getClientCode(), e);
            }
        });

        return true;
    }

    private void mapProjectionToReportType(
            PrepDTO projection,
            HIVTestingReportType reportType,
            ObjectFactory objectFactory) {

    }
}
