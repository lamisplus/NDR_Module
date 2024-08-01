package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.ClientVerificationDTO;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.schema.ClientVerificationType;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientVerificationTypeMapper {

    private final NdrMessageLogRepository ndrMessageLogRepository;

    public ClientVerificationType getClientVerifications(String patientId, long facilityId, LocalDate start, LocalDate end, List<NDRErrorDTO> ndrErrors) {

        ClientVerificationType clientVerificationType = new ClientVerificationType();

        try {
            ClientVerificationDTO clientVerificationVal = ndrMessageLogRepository.getClientVerification(patientId, facilityId, start, end);

            if (clientVerificationVal != null) {
//            log.info("Started client");
                if (clientVerificationVal.getClientVerification() != null) {
                    clientVerificationType.setClientVerification("YES");
                } else {
                    clientVerificationType.setClientVerification("NO");
                }

                if (clientVerificationVal.getPickupByProxy() != null) {
                    clientVerificationType.setPickupByProxy("TRUE");
                } else {
                    clientVerificationType.setPickupByProxy("FALSE");
                }

                if (clientVerificationVal.getDuplicatedDemographic() != null) {
                    clientVerificationType.setDuplicatedDemographic("TRUE");
                } else {
                    clientVerificationType.setDuplicatedDemographic("FALSE");
                }

                if (clientVerificationVal.getNoRecapture() != null) {
                    clientVerificationType.setNoRecapture("TRUE");
                } else {
                    clientVerificationType.setNoRecapture("FALSE");
                }

                if (clientVerificationVal.getBatchPickupDates() != null) {
                    clientVerificationType.setBatchPickupDates("TRUE");
                } else {
                    clientVerificationType.setBatchPickupDates("FALSE");
                }

                if (clientVerificationVal.getLastVisitIsOver18M() != null) {
                    clientVerificationType.setLastVisitIsOver18M("TRUE");
                } else {
                    clientVerificationType.setLastVisitIsOver18M("FALSE");
                }

                if (clientVerificationVal.getArtStartPickupDate() != null) {
                    clientVerificationType.setARTStartPickupDate("TRUE");
                } else {
                    clientVerificationType.setARTStartPickupDate("FALSE");
                }

                if (clientVerificationVal.getNoInitBiometric() != null) {
                    clientVerificationType.setNoInitBiometric("TRUE");
                } else {
                    clientVerificationType.setNoInitBiometric("FALSE");
                }

                if (clientVerificationVal.getIncompleteVisitData() != null) {
                    clientVerificationType.setIncompleteVisitData("TRUE");
                } else {
                    clientVerificationType.setIncompleteVisitData("FALSE");
                }

                if (clientVerificationVal.getRepeatEncounterNoPrint() != null) {
                    clientVerificationType.setRepeatEncounterNoPrint("TRUE");
                } else {
                    clientVerificationType.setRepeatEncounterNoPrint("FALSE");
                }

                if (clientVerificationVal.getLongIntervalsARVPickup() != null) {
                    clientVerificationType.setLongIntervalsARVPickup("TRUE");
                } else {
                    clientVerificationType.setLongIntervalsARVPickup("FALSE");
                }

                if (clientVerificationVal.getSameSexDOBARTStartDate() != null) {
                    clientVerificationType.setSameSexDOBARTStartDate("TRUE");
                } else {
                    clientVerificationType.setSameSexDOBARTStartDate("FALSE");
                }

                if (clientVerificationVal.getOtherSpecifyForCV() != null) {
                    clientVerificationType.setOtherSpecifyForCV(clientVerificationVal.getOtherSpecifyForCV());
                }

                if (clientVerificationVal.getCt1STDate() != null) {
//            log.info("122 {}", clientVerificationVal.ct1STDate());
                    Date ct1STDate = java.sql.Date.valueOf(clientVerificationVal.getCt1STDate());
                    try {
                        clientVerificationType.setCT1STDate(DateUtil.getXmlDate(ct1STDate));
                    } catch (DatatypeConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (clientVerificationVal.getFirstStatus() != null) {
                    getFirstStatus(clientVerificationVal.getFirstStatus(), clientVerificationType);
                }

                if (clientVerificationVal.getFirstOutcome() != null) {
                    getFirstOutcome(clientVerificationVal.getFirstOutcome(), clientVerificationType);
                }

                if (clientVerificationVal.getCt2NdDate() != null) {
//                    log.info("122 {}", clientVerificationVal.getCt2NdDate());
                    Date ct2NdDate = java.sql.Date.valueOf(clientVerificationVal.getCt2NdDate());
                    try {
                        clientVerificationType.setCT2NdDate(DateUtil.getXmlDate(ct2NdDate));
                    } catch (DatatypeConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (clientVerificationVal.getSecondStatus() != null) {
                    getSecondStatus(clientVerificationVal.getSecondStatus(), clientVerificationType);
                }

                if (clientVerificationVal.getSecondOutcome() != null) {
                    getSecondOutcome(clientVerificationVal.getSecondOutcome(), clientVerificationType);
                }

                if (clientVerificationVal.getCtLastDate() != null) {
//                    log.info("123 {}", clientVerificationVal.getCtLastDate());
                    Date ctLastDate = java.sql.Date.valueOf(clientVerificationVal.getCtLastDate());
                    try {
                        clientVerificationType.setCTLastDate(DateUtil.getXmlDate(ctLastDate));
                    } catch (DatatypeConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (clientVerificationVal.getLastStatus() != null) {
                    getThirdStatus(clientVerificationVal.getLastStatus(), clientVerificationType);
                }

                if (clientVerificationVal.getLastOutcome() != null) {
                    getThirdOutcome(clientVerificationVal.getLastOutcome(), clientVerificationType);
                }
                return clientVerificationType;
            }
        } catch (Exception e) {
            log.error("An error occur while fetching client verification info for patient with uuid {} {}", patientId, e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }
        return null;
    }

    private void getFirstStatus(String firstStatus, ClientVerificationType clientVerificationType) {
        if (firstStatus.contains("Verification Ongoing")) {
            clientVerificationType.setFirstStatus("VerificationOngoing");
        } else if (firstStatus.contains("Records Discontinued Archived")) {
            clientVerificationType.setFirstStatus("RecordDiscontinued");
        } else if (firstStatus.contains("Records Verified")) {
            clientVerificationType.setFirstStatus("RecordVerified");
        }
    }

    private void getFirstOutcome(String firstOutcome, ClientVerificationType clientVerificationType) {
        if (firstOutcome.contains("Verification Ongoing")) {
            clientVerificationType.setFirstOutcome("Pending");
        } else if (firstOutcome.contains("valid")) {
            clientVerificationType.setFirstOutcome("Valid");
        } else if (firstOutcome.contains("invalid")) {
            clientVerificationType.setFirstOutcome("Invalid");
        }
    }

    private void getSecondStatus(String firstStatus, ClientVerificationType clientVerificationType) {
        if (firstStatus.contains("Verification Ongoing")) {
            clientVerificationType.setSecondStatus("VerificationOngoing");
        } else if (firstStatus.contains("Records Discontinued Archived")) {
            clientVerificationType.setSecondStatus("RecordDiscontinued");
        } else if (firstStatus.contains("Records Verified")) {
            clientVerificationType.setSecondStatus("RecordVerified");
        }
    }

    private void getSecondOutcome(String firstOutcome, ClientVerificationType clientVerificationType) {
        if (firstOutcome.contains("Verification Ongoing")) {
            clientVerificationType.setSecondOutcome("Pending");
        } else if (firstOutcome.contains("valid")) {
            clientVerificationType.setSecondOutcome("Valid");
        } else if (firstOutcome.contains("invalid")) {
            clientVerificationType.setSecondOutcome("Invalid");
        }
    }

    private void getThirdStatus(String firstStatus, ClientVerificationType clientVerificationType) {
        if (firstStatus.contains("Verification Ongoing")) {
            clientVerificationType.setLastStatus("VerificationOngoing");
        } else if (firstStatus.contains("Records Discontinued Archived")) {
            clientVerificationType.setLastStatus("RecordDiscontinued");
        } else if (firstStatus.contains("Records Verified")) {
            clientVerificationType.setLastStatus("RecordVerified");
        }
    }

    private void getThirdOutcome(String firstOutcome, ClientVerificationType clientVerificationType) {
        if (firstOutcome.contains("Verification Ongoing")) {
            clientVerificationType.setLastOutcome("Pending");
        } else if (firstOutcome.contains("valid")) {
            clientVerificationType.setLastOutcome("Valid");
        } else if (firstOutcome.contains("invalid")) {
            clientVerificationType.setLastOutcome("Invalid");
        }
    }


}
