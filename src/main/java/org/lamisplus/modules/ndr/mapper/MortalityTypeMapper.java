package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.MortalityDTO;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.schema.ClientVerificationType;
import org.lamisplus.modules.ndr.schema.MortalityType;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MortalityTypeMapper {

    private final NdrMessageLogRepository ndrMessageLogRepository;
    private final ClientVerificationTypeMapper clientVerificationTypeMapper;
    public MortalityType getMortalityType(String patientId, long facilityId, LocalDate start, LocalDate end, List<NDRErrorDTO> ndrErrors) {
        MortalityType mortalityType = new MortalityType();
        try {
            ClientVerificationType clientVerification = clientVerificationTypeMapper.getClientVerifications(patientId, facilityId, start, end, ndrErrors);
            List<MortalityDTO> mortalityVariables = ndrMessageLogRepository.getPatientMortalities(patientId, facilityId, start, end);

            if (mortalityVariables != null && !mortalityVariables.isEmpty()) {
//            log.info("mortality init");
                mortalityVariables.forEach(mortality -> {

                    if (clientVerification != null) {
                        mortalityType.setClientVerification(clientVerification);
                    }

//                log.info("patient mortality visit Id {}", mortality.getVisitID());
                    if(mortality.getVisitID() != null) {
                        mortalityType.setVisitID(mortality.getVisitID());
                    }else {
                        throw new RuntimeException("Mortality visit id cannot be null");
                    }
                    if(mortality.getVisitDate() != null) {
                        Date visitDate = java.sql.Date.valueOf(mortality.getVisitDate());
                        try {
                            mortalityType.setVisitDate(DateUtil.getXmlDate(visitDate));
                        } catch (DatatypeConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                    }else {
                        throw new RuntimeException("Mortality visit date  cannot be null");
                    }
                    if(mortality.getReasonForTracking() != null) {
                        reasonForTracking(mortality.getReasonForTracking(), mortalityType);
                    }
                    if(mortality.getOtherTrackingReason() != null) {
                        mortalityType.setOtherTrackingReason(mortality.getOtherTrackingReason());
                    }
                    if(mortality.getPartnerFullName() != null && mortality.getPartnerFullName().isEmpty()) {
                        mortalityType.setPartnerFullName(mortality.getPartnerFullName());
                    }
                    if(mortality.getAddressofTreatmentSupporter() != null) {
                        mortalityType.setAddressofTreatmentSupporter(mortality.getAddressofTreatmentSupporter());
                    }
                    if(mortality.getContactPhoneNumber() != null) {
                        mortalityType.setContactPhoneNumber(mortality.getContactPhoneNumber());
                    }
                    if(mortality.getDateofLastActualContact() != null) {
                        Date contactDate = java.sql.Date.valueOf(mortality.getDateofLastActualContact());
                        try {
                            mortalityType.setDateofLastActualContact(DateUtil.getXmlDate(contactDate));
                        } catch (DatatypeConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(mortality.getDateofMissedScheduledAppointment() != null) {
                        Date missedDate = java.sql.Date.valueOf(mortality.getDateofMissedScheduledAppointment());
                        try {
                            mortalityType.setDateofMissedScheduledAppointment(DateUtil.getXmlDate(missedDate));
                        } catch (DatatypeConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(mortality.getDatePatientContacted() != null) {
                        Date patientContectedDate = java.sql.Date.valueOf(mortality.getDatePatientContacted());
                        try {
                            mortalityType.setDatePatientContacted(DateUtil.getXmlDate(patientContectedDate));
                        } catch (DatatypeConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(mortality.getNameofPersonWhoAttemptedContact() != null) {
                        mortalityType.setNameofPersonWhoAttemptedContact(mortality.getNameofPersonWhoAttemptedContact());
                    }
                    if(mortality.getModeofCommunication() != null) {
                        modeofCommunication(mortality.getModeofCommunication(), mortalityType);
                    }
                    if(mortality.getPersonContacted() != null) {
                        personContacted(mortality.getPersonContacted(), mortalityType);
                    }
                    if(mortality.getReasonforDefaulting() != null) {
                        reasonforDefaulting(mortality.getReasonforDefaulting(), mortalityType);
                    }
                    if(mortality.getOtherReasonforDefaulting() != null) {
                        mortalityType.setOtherReasonforDefaulting(mortality.getOtherReasonforDefaulting());
                    }
//                if(mortality.getLosttoFollowup() != null) {
//                    log.info("110 {}", mortality.getLosttoFollowup());
//                    mortalityType.setLosttoFollowup(true);
//                }
                    if(mortality.getReasonforLosttoFollowup() != null) {
                        reasonforLosttoFollowup(mortality.getReasonforLosttoFollowup(), mortalityType);
                    }
//                if(mortality.getDateLosttoFollowup() != null) {
//                    log.info("121 {}", mortality.getReasonforLosttoFollowup());
//                    Date lostOtFollowUpDate = java.sql.Date.valueOf(mortality.getDateLosttoFollowup());
//                    try {
//                        mortalityType.setDateLosttoFollowup(DateUtil.getXmlDate(lostOtFollowUpDate));
//                    } catch (DatatypeConfigurationException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                };
                    if(mortality.getPreviousARVExposure() != null) {

                        if(mortality.getPreviousARVExposure().contains("previousARVExposure")) {
                            mortalityType.setPreviousARVExposure("Yes");
                        }
                    }
                    if(mortality.getDateofTermination() != null) {
                        Date terminationDate = java.sql.Date.valueOf(mortality.getDateofTermination());
                        try {
                            mortalityType.setDateofTermination(DateUtil.getXmlDate(terminationDate));
                        } catch (DatatypeConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(mortality.getReasonforTermination() != null) {
                        reasonforTermination(mortality.getReasonforTermination(), mortalityType);
                    }

                    if(mortality.getTransferredOutTo() != null) {
                        mortalityType.setTransferredOutTo(mortality.getTransferredOutTo());
                    }
                    if(mortality.getDeath() != null) {
                        death(mortality.getDeath(), mortalityType);
                    }
                    if(mortality.getVaCauseofDeath() != null) {
                        vaCauseOfDeath(mortality.getVaCauseofDeath(), mortalityType);
                    }
                    if(mortality.getOtherCauseofDeath() != null) {
                        mortalityType.setOtherCauseofDeath(mortality.getOtherCauseofDeath());
                    }
                    if(mortality.getCauseOfDeath() != null) {
                        causeOfDeath(mortality.getCauseOfDeath(), mortalityType);
                    }
                    if(mortality.getDiscontinuedCare() != null) {
                        discontinuedCare(mortality.getDiscontinuedCare(), mortalityType);
                    }

                    if(mortality.getDiscontinueCareOtherSpecify() != null) {
                        mortalityType.setDiscontinueCareOtherSpecify(mortality.getDiscontinueCareOtherSpecify());
                    }
                    if(mortality.getDateReturnedtoCare() != null) {
                        Date returnedDate = java.sql.Date.valueOf(mortality.getDateReturnedtoCare());
                        try {
                            mortalityType.setDateReturnedtoCare(DateUtil.getXmlDate(returnedDate));
                        } catch (DatatypeConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(mortality.getReffferedFor() != null) {
                        reffferedFor(mortality.getReffferedFor(), mortalityType);
                    }
                    if(mortality.getReffferedForOther() != null) {
                        mortalityType.setReffferedForOther(mortality.getReffferedForOther());
                    }
                    if(mortality.getNameofContactTracer() != null) {
                        log.info("124 {}", mortality.getNameofContactTracer());
                        mortalityType.setNameofContactTracer(mortality.getNameofContactTracer());
                    }
//                if(mortality.getContactTrackerSignatureDate() != null) {
//                    Date trackDate = java.sql.Date.valueOf(mortality.getContactTrackerSignatureDate());
//                    try {
//                        mortalityType.setContactTrackerSignatureDate(DateUtil.getXmlDate(trackDate));
//                    } catch (DatatypeConfigurationException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                };
                });
                return  mortalityType;
            }

        }catch (Exception e) {
            log.error("An error occur while fetching mortality records for patient with uuid {} information {}", patientId, e.getMessage());
            ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
        }

        return null;
    }

    private void reasonForTracking(String reasonForTracking, MortalityType mortalityType) {
        if (reasonForTracking.contains("REASON_TRACKING_OTHER_(SPECIFY)")) {
            mortalityType.setReasonForTracking("Others");
        } else if (reasonForTracking.contains("REASON_TRACKING_INTENSIVE_FOLLOW-UP")) {
            mortalityType.setReasonForTracking("MissAppDate");
        } else if (reasonForTracking.contains("REASON_TRACKING_MISSED_APPOINTMENT")) {
            mortalityType.setReasonForTracking("MissAppDate");
        } else if (reasonForTracking.contains("REASON_TRACKING_LOST_TO_FOLLOW-UP")) {
            mortalityType.setReasonForTracking("MissPharmDate");
        }
    }

    private void modeofCommunication(String modeofCommunication, MortalityType mortalityType) {
        if (modeofCommunication.contains("MODE_OF_COMMUNICATION_TELEPHONE")) {
            mortalityType.setModeofCommunication("MobilePhone");
        }else if(modeofCommunication.contains("MODE_OF_COMMUNICATION_HOME_VISIT")){
            mortalityType.setModeofCommunication("HomeVisit");
        }
    }

    private void personContacted(String personContacted, MortalityType mortalityType) {
        if (personContacted.contains("PERSON_CONTACTED_GUARDIAN")) {
            mortalityType.setPersonContacted("Guardian");
        }else if (personContacted.contains("PERSON_CONTACTED_TX_PARTNER")) {
            mortalityType.setPersonContacted("TreatmentSupporter");
        }else if (personContacted.contains("PERSON_CONTACTED_NO_CONTACT")) {
            mortalityType.setPersonContacted("Patient");
        }else if (personContacted.contains("PERSON_CONTACTED_CLIENT")) {
            mortalityType.setPersonContacted("Patient");
        }
    }

    private void reasonforDefaulting(String reasonforDefaulting, MortalityType mortalityType) {
        if (reasonforDefaulting.contains("REASON_DEFAULTING_OTHERS_(PLS_SPECIFY)")) {
            mortalityType.setReasonforDefaulting("OthersSpecify");
        }else if (reasonforDefaulting.contains("REASON_DEFAULTING_TRANSFERRED_TO_NEW_SITE")) {
            mortalityType.setReasonforDefaulting("OthersSpecify");
        }else if (reasonforDefaulting.contains("REASON_DEFAULTING_INTENSIVE_FOLLOW-UP")) {
            mortalityType.setReasonforDefaulting("OthersSpecify");
        }
    }

    private void reasonforLosttoFollowup(String reasonforDefaulting, MortalityType mortalityType) {
        if (reasonforDefaulting.contains("DidNotAttempttoTrackPatient")) {
            mortalityType.setReasonforLosttoFollowup("DidNotAttempttoTrackPatient");
        }else if (reasonforDefaulting.contains("TrackedbutUnabletoLocate")) {
            mortalityType.setReasonforLosttoFollowup("TrackedbutUnabletoLocate");
        }
    }

    private void reasonforTermination(String reasonforTermination, MortalityType mortalityType) {
        if (reasonforTermination.contains("Treatment Stop")) {
            mortalityType.setReasonforTermination("TreatmentStop");
        }else if (reasonforTermination.contains("Self-transfer to another facility")) {
            mortalityType.setReasonforTermination("SelfTransfer");
        }else if (reasonforTermination.contains("Death")) {
            mortalityType.setReasonforTermination("Death");
        }else if (reasonforTermination.contains("Discontinued")) {
            mortalityType.setReasonforTermination("Discontinued");
        }else if (reasonforTermination.contains("DuplicateRecord")) {
            mortalityType.setReasonforTermination("DuplicateRecord");
        }else if (reasonforTermination.contains("CouldNotVerifyClient")) {
            mortalityType.setReasonforTermination("CouldNotVerifyClient");
        }else if (reasonforTermination.contains("LTFU")) {
            mortalityType.setReasonforTermination("LTFU");
        }else if (reasonforTermination.contains("Transferred Out")) {
            mortalityType.setReasonforTermination("TransferredOut");
        }
    }

    private void death(String death, MortalityType mortalityType) {
        if (death.contains("Other Cause of Death")) {
            mortalityType.setDeath("OtherCauseofDeath");
        }else  if (death.contains("Suspected ARV Side Effect")) {
            mortalityType.setDeath("SuspectedARVSideEffect");
        }else  if (death.contains("Suspected Opportunistic Infection")) {
            mortalityType.setDeath("SuspectedOpportunisticInfection");
        }else  if (death.contains("Unknown")) {
            mortalityType.setDeath("Unknown");
        }
    }

    private void vaCauseOfDeath(String causeofdeath, MortalityType mortalityType) {
        if (causeofdeath.contains("Adult Causes") || causeofdeath.contains("Adult Causes - Injuries") || causeofdeath.contains("Adult Causes -Non-communicable diseases")) {
            mortalityType.setVACauseofDeath("VAAdultCasesofDeath");
        }if (causeofdeath.contains("Child Causes") || causeofdeath.contains("Child Causes - Injuries") || causeofdeath.contains("Child Causes - Non-communicable diseases")) {
            mortalityType.setVACauseofDeath("VAChildCasesofDeath");
        }
    }

    private void causeOfDeath(String causeofdeath, MortalityType mortalityType) {
        if (causeofdeath.contains("B24 AIDS")) {
            mortalityType.setCauseOfDeath("AIDS");
        }else if (causeofdeath.contains("A09 Diarrhea/Dysentery")) {
            mortalityType.setCauseOfDeath("DiarrheaDysentery");
        }else if (causeofdeath.contains("B54 Malaria")) {
            mortalityType.setCauseOfDeath("Malaria");
        }else if (causeofdeath.contains("O95 Maternal")) {
            mortalityType.setCauseOfDeath("Maternal");
        }else if (causeofdeath.contains("B99 Other Infectious Diseases")) {
            mortalityType.setCauseOfDeath("OtherInfetiousDiseases");
        }else if (causeofdeath.contains("J22 Pneumonia")) {
            mortalityType.setCauseOfDeath("Pneumonia");
        }else if (causeofdeath.contains("A16 TB")) {
            mortalityType.setCauseOfDeath("TB");
        }else if (causeofdeath.contains("I24 Acute Myocardial Infarction")) {
            mortalityType.setCauseOfDeath("AcuteMyocardialInfection");
        }else if (causeofdeath.contains("C50 Breast Cancer")) {
            mortalityType.setCauseOfDeath("BreastCancer");
        }else if (causeofdeath.contains("J44 Chronic Respiratory Diseases")) {
            mortalityType.setCauseOfDeath("ChronicRespiratoryDiseases");
        }else if (causeofdeath.contains("C53 Cervical Cancers")) {
            mortalityType.setCauseOfDeath("CervicalCancer");
        }else if (causeofdeath.contains("K74 Cirrhosis")) {
            mortalityType.setCauseOfDeath("Cirrhosis");
        }else if (causeofdeath.contains("C18 Colorectal Cancer")) {
            mortalityType.setCauseOfDeath("ColorectalCancer");
        }else if (causeofdeath.contains("E14 Diabetes")) {
            mortalityType.setCauseOfDeath("Diabetes");
        }else if (causeofdeath.contains("C15 Esophageal Cancer")) {
            mortalityType.setCauseOfDeath("EsophagealCancer");
        }else if (causeofdeath.contains("C96 Leukemia/Lymphomas")) {
            mortalityType.setCauseOfDeath("LeukamiaLymphomas");
        }else if (causeofdeath.contains("C34 Lung Cancer")) {
            mortalityType.setCauseOfDeath("LungCancer");
        }else if (causeofdeath.contains("I99 Other Cardiovascular Diseases")) {
            mortalityType.setCauseOfDeath("OtherCardiovascularDiseases");
        }else if (causeofdeath.contains("UU1* Other Non-communicable Diseases")) {
            mortalityType.setCauseOfDeath("OtherNonCommunicableDiseases");
        }else if (causeofdeath.contains("C61 Prostate Cancer")) {
            mortalityType.setCauseOfDeath("ProstateCancer");
        }else if (causeofdeath.contains("N18 Chronic Kidney Disease")) {
            mortalityType.setCauseOfDeath("ChronicKidneyDisease");
        }else if (causeofdeath.contains("C16 Stomach Cancer")) {
            mortalityType.setCauseOfDeath("StomachCancer");
        }else if (causeofdeath.contains("I64 Stroke")) {
            mortalityType.setCauseOfDeath("Stroke");
        }else if (causeofdeath.contains("C76 Other Cancers")) {
            mortalityType.setCauseOfDeath("OtherCancers");
        }else if (causeofdeath.contains("X27 Bite of Venomous Animal")) {
            mortalityType.setCauseOfDeath("BiteofVenomousAnimal");
        }else if (causeofdeath.contains("W74 Drowning")) {
            mortalityType.setCauseOfDeath("Drowning");
        }else if (causeofdeath.contains("W19 Falls")) {
            mortalityType.setCauseOfDeath("Falls");
        }else if (causeofdeath.contains("X09 Fires")) {
            mortalityType.setCauseOfDeath("Fires");
        }else if (causeofdeath.contains("Y09 Homicide (assault)")) {
            mortalityType.setCauseOfDeath("Homicide");
        }else if (causeofdeath.contains("X58 Other Injuries")) {
            mortalityType.setCauseOfDeath("OtherInjuries");
        }else if (causeofdeath.contains("X49 Poisonings")) {
            mortalityType.setCauseOfDeath("AccidentalPoisoning");
        }else if (causeofdeath.contains("V89 Road Traffic")) {
            mortalityType.setCauseOfDeath("RoadTraffic");
        }else if (causeofdeath.contains("X84 Suicide (intentional self-harm)")) {
            mortalityType.setCauseOfDeath("SuicideByMultipleMeans");
        }else if (causeofdeath.contains("G04 Encephalitis")) {
            mortalityType.setCauseOfDeath("Encephalitis");
        }else if (causeofdeath.contains("A99 Hemorrhagic fever")) {
            mortalityType.setCauseOfDeath("HemorrhagicFever");
        }else if (causeofdeath.contains("A41 Sepsis")) {
            mortalityType.setCauseOfDeath("Sepsis");
        }else if (causeofdeath.contains("G03 Meningitis")) {
            mortalityType.setCauseOfDeath("Meningitis");
        }else if (causeofdeath.contains("B05 Measles")) {
            mortalityType.setCauseOfDeath("Measles");
        }else if (causeofdeath.contains("UU2* Other Defined Causes of Child Deaths")) {
            mortalityType.setCauseOfDeath("OtherDefinedCausesofChildDeaths");
        }else if (causeofdeath.contains("K92 Other Digestive Diseases")) {
            mortalityType.setCauseOfDeath("OtherDigestiveDiseases");
        }else if (causeofdeath.contains("P21 Birth asphyxia")) {
            mortalityType.setCauseOfDeath("BirthAsphyxia");
        }else if (causeofdeath.contains("Q89 Congenital malformation")) {
            mortalityType.setCauseOfDeath("CongenitalMalformation");
        }else if (causeofdeath.contains("P36 Neonatal Meningitis/Sepsis")) {
            mortalityType.setCauseOfDeath("NeonatalMeningitis");
        }else if (causeofdeath.contains("P23 Neonatal Pneumonia")) {
            mortalityType.setCauseOfDeath("NeonatalPneumonia");
        }else if (causeofdeath.contains("P07 Preterm Delivery")) {
            mortalityType.setCauseOfDeath("PretermDelivery");
        }else if (causeofdeath.contains("P95 Stillbirth")) {
            mortalityType.setCauseOfDeath("Stillbirth");
        }
    }

    private void discontinuedCare(String discontinuedCare, MortalityType mortalityType) {
        if (discontinuedCare.contains("Yes")) {
            mortalityType.setDiscontinuedCare("SelfDiscontinuation");
        }else if (discontinuedCare.contains("Forced Discontinuation")) {
            mortalityType.setDiscontinuedCare("ForcedDiscontinuation");
        }else if (discontinuedCare.contains("Moved Out of Area")) {
            mortalityType.setDiscontinuedCare("MovedOutofArea");
        }else if (discontinuedCare.contains("Other")) {
            mortalityType.setDiscontinuedCare("Other");
        }
    }

    private void reffferedFor(String reffferedFor, MortalityType mortalityType) {
        if (reffferedFor.contains("Adherence Counseling")) {
            mortalityType.setReffferedFor("AdherenceCounseling");
        }else if (reffferedFor.contains("Other")) {
            mortalityType.setReffferedFor("Other");
        }
    }

}
