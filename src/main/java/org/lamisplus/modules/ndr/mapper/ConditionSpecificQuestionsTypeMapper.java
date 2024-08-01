package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.service.ApplicationCodesetService;
import org.lamisplus.modules.hiv.domain.dto.HIVStatusDisplay;
import org.lamisplus.modules.hiv.repositories.RegimenRepository;
import org.lamisplus.modules.hiv.service.StatusManagementService;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.domain.dto.ArtCommencementDTO;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.schema.CodedSimpleType;
import org.lamisplus.modules.ndr.schema.ConditionSpecificQuestionsType;
import org.lamisplus.modules.ndr.schema.HIVQuestionsType;
import org.lamisplus.modules.ndr.service.NDRCodeSetResolverService;
import org.lamisplus.modules.triage.domain.entity.VitalSign;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.lamisplus.modules.ndr.utility.DateUtil.getXmlDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConditionSpecificQuestionsTypeMapper {

    private final NDRCodeSetResolverService ndrCodeSetResolverService;
    
    private final NDRCodeSetRepository ndrCodeSetRepository;

    private final ApplicationCodesetService applicationCodesetService;
    
    private final RegimenRepository regimenRepository;
    private final StatusManagementService statusManagementService;


    public ConditionSpecificQuestionsType getConditionSpecificQuestionsType(PatientDemographics demographics) {
        log.info("Generating condition specific questions for patient with uuid {}", demographics.getPersonUuid());
        try {
            ConditionSpecificQuestionsType hivQuestions = new ConditionSpecificQuestionsType ();
            HIVQuestionsType hiv = new HIVQuestionsType ();
            processAndSetDateOfRegistration (hiv, demographics.getDateOfRegistration(), demographics.getStatusAtRegistration());
            processAndSetCareEntryPoint (hiv, demographics.getCareEntryPoint());
            if (demographics.getDateOfRegistration() != null) {
                                String enrollmentStatus = demographics.getStatusAtRegistration();
                                processAndHandleARTStatus (hiv, demographics.getId (), enrollmentStatus);
            }
            
               Optional<ArtCommencementDTO> artCommencement =
                       ndrCodeSetRepository.getArtCommencementByPatientUuid(demographics.getPersonUuid());
            
                log.info("ART Commencement: {}", artCommencement);
                if (artCommencement.isPresent()) {
                    processAndSetArtStartDate (hiv, artCommencement.get().getArtStartDate());
                    processAndSetWHOStagingAndFunctionalStatus (hiv, artCommencement.get().getWhoStage(), artCommencement.get().getFunctionStatus());
                    String regimen = artCommencement.get().getRegimen();
                    if(regimen != null) {
                     Optional<CodedSimpleType> simpleCodeSet = ndrCodeSetResolverService.getRegimen(regimen);
                     log.info("ndrRegimen: " + regimen);
                     simpleCodeSet.ifPresent(hiv::setFirstARTRegimen);
                     }
                    processAndSetCD4 (hiv, demographics.getAge(), artCommencement.get());
                }
                hivQuestions.setHIVQuestions (hiv);
            return hivQuestions;
        } catch (Exception e) {
            log.error("An error Generating condition specific questions for patient with uuid {}",
                    demographics.getPersonUuid());
           log.error("Error Message: {} " + e.getMessage());
        }
        return null;

    }
    
    
    public ConditionSpecificQuestionsType getConditionSpecificQuestionsType(
            PatientDemographics demographics,
            ArtCommencementDTO artCommencement) {
        log.info("Generating condition specific questions for patient with uuid {}", demographics.getPersonUuid());
        try {
            ConditionSpecificQuestionsType hivQuestions = new ConditionSpecificQuestionsType ();
            HIVQuestionsType hiv = new HIVQuestionsType ();
            processAndSetDateOfRegistration (hiv, demographics.getDateOfRegistration(), demographics.getStatusAtRegistration());
            processAndSetCareEntryPoint (hiv, demographics.getCareEntryPoint());
            if (demographics.getDateOfRegistration() != null) {
                String enrollmentStatus = demographics.getStatusAtRegistration();
                processAndHandleARTStatus (hiv, demographics.getId (), enrollmentStatus);
            }
             log.info("ART Commencement: {}", artCommencement);
                processAndSetArtStartDate (hiv, artCommencement.getArtStartDate());
                processAndSetWHOStagingAndFunctionalStatus (hiv, artCommencement.getWhoStage(), artCommencement.getFunctionStatus());
                String regimen = artCommencement.getRegimen();
                if(regimen != null) {
                    Optional<CodedSimpleType> simpleCodeSet = ndrCodeSetResolverService.getRegimen(regimen);
                    log.info("First ndrRegimen: " + regimen);
                    simpleCodeSet.ifPresent(hiv::setFirstARTRegimen);
                }
                processAndSetCD4 (hiv, demographics.getAge(), artCommencement);
            
            hivQuestions.setHIVQuestions (hiv);
            return hivQuestions;
        } catch (Exception e) {
            log.error("An error Generating condition specific questions for patient with uuid {}",
                    demographics.getPersonUuid());
            log.error("Error Message: {} " + e.getMessage());
        }
        return null;
        
    }
    
    public ConditionSpecificQuestionsType getConditionSpecificQuestionsType(PatientDemographicDTO demographics) {
        //@XmlElement(name = "EnrolledInHIVCareDate", required = true)
        log.info("Generating condition specific questions for patient with uuid {}", demographics.getPersonUuid());
        try {
            ConditionSpecificQuestionsType hivQuestions = new ConditionSpecificQuestionsType ();
            HIVQuestionsType hiv = new HIVQuestionsType ();
            //LocalDate inHIVCareDate = demographics.getEnrolledInHIVCareDate();
            LocalDate inHIVCareDate = (demographics.getEnrolledInHIVCareDate() != null ? demographics.getEnrolledInHIVCareDate() : demographics.getArtStartDate());
            if(inHIVCareDate != null){
                hiv.setEnrolledInHIVCareDate(getXmlDate (Date.valueOf (inHIVCareDate)));
                String statusAtRegistration = demographics.getStatusAtRegistration();
                String causeOfDeath = demographics.getCauseOfDeath();
                if(causeOfDeath != null) {
                    if(causeOfDeath.toUpperCase().contains("HIV")) {
                        hiv.setCauseOfDeathHIVRelated("Y");
                    } else if(causeOfDeath.toUpperCase().contains("UNKNOWN")) {
                        hiv.setCauseOfDeathHIVRelated("U");
                    } else {
                        hiv.setCauseOfDeathHIVRelated("N");
                    }
                }
                if (statusAtRegistration != null) {
                    if (statusAtRegistration.equalsIgnoreCase ("HIV+ non ART")) {
                        hiv.setFirstConfirmedHIVTestDate (getXmlDate (Date.valueOf (inHIVCareDate)));
                    }
                    if (statusAtRegistration.equalsIgnoreCase ("ART Transfer In")) {
                        hiv.setTransferredInDate (getXmlDate (Date.valueOf (inHIVCareDate)));
                    }
                    String tbStatus = demographics.getTbStatus();
                    log.info("initial tb status {}", tbStatus);
                    if(tbStatus != null){
                        hiv.setInitialTBStatus(demographics.getTbStatus());
                    }
                    processAndHandleARTStatus (hiv, demographics.getPersonId(), statusAtRegistration);
                }
            }else {
                throw new IllegalArgumentException(" Enrolled In HIVCareDate cannot be null");
            }
            if(demographics.getCareEntryPoint() != null){
                hiv.setCareEntryPoint(demographics.getCareEntryPoint());
            }
            log.info("art start date {}", demographics.getArtStartDate());
           
            if (demographics.getArtStartDate() != null) {
                hiv.setARTStartDate (getXmlDate (Date.valueOf ((demographics.getArtStartDate()))));
            }
            if(demographics.getFirstARTRegimenCode() != null && demographics.getFirstARTRegimenCodeDescTxt() != null) {
                CodedSimpleType codedSimpleType = new CodedSimpleType();
                codedSimpleType.setCode(demographics.getFirstARTRegimenCode());
                codedSimpleType.setCodeDescTxt(demographics.getFirstARTRegimenCodeDescTxt());
                hiv.setFirstARTRegimen(codedSimpleType);
            }
            if(demographics.getFunctionalStatusStartART() != null){
                hiv.setFunctionalStatusStartART(demographics.getFunctionalStatusStartART());
            }
            if(demographics.getWHOClinicalStageART() != null){
                hiv.setWHOClinicalStageARTStart(demographics.getWHOClinicalStageART());
            }
            // need more clarity on CD4
            hivQuestions.setHIVQuestions (hiv);
            return hivQuestions;
        } catch (Exception e) {
            log.error("An error Generating condition specific questions for patient with uuid {}",
                    demographics.getPersonUuid());
            log.error("Error Message: {} " + e.getMessage());
        }
        return null;
        
    }

    
    
    private void processAndSetHeightAndWeight(HIVQuestionsType hiv, VitalSign vitalSign) {
        Double bodyWeight = vitalSign.getBodyWeight ();
        if (bodyWeight > 0) {
            hiv.setWeightAtARTStart (bodyWeight.intValue ());
        }
        if (bodyWeight.intValue () > 200) {
            int weight = bodyWeight.intValue () / 10;
            hiv.setWeightAtARTStart (weight);
        }
        Double height = vitalSign.getHeight ();
        if (height > 0) {
            int heightInCm = (int) (height * 100);
            if (heightInCm > 200) {
                heightInCm = heightInCm / 10;
            }
            hiv.setChildHeightAtARTStart (heightInCm);
        }
    }

    private void processAndSetWHOStagingAndFunctionalStatus(HIVQuestionsType hiv, String whoStage, String functionalStatus) {
        if(whoStage != null) {
                Optional<String> whoStageCodeSet =
                        ndrCodeSetResolverService.getNDRCodeSetCode("WHO_STAGE",whoStage);
                whoStageCodeSet.ifPresent(hiv::setWHOClinicalStageARTStart);
        }
        if(functionalStatus != null) {
                Optional<String> functionalStatusCodeSet =
                        ndrCodeSetResolverService.getNDRCodeSetCode("FUNCTIONAL_STATUS", functionalStatus);
                functionalStatusCodeSet.ifPresent(hiv::setFunctionalStatusStartART);
        }
    }

    private void processAndSetDateOfRegistration(HIVQuestionsType hiv, LocalDate dateOfRegistration, String statusAtRepresentation) {
        try {
            if (dateOfRegistration != null) {
                hiv.setEnrolledInHIVCareDate (getXmlDate (Date.valueOf (dateOfRegistration)));
                if (statusAtRepresentation != null) {
                    if (statusAtRepresentation.equalsIgnoreCase ("HIV+ non ART")) {
                        hiv.setFirstConfirmedHIVTestDate (getXmlDate (Date.valueOf (dateOfRegistration)));
                    }
                    if (statusAtRepresentation.equalsIgnoreCase ("ART Transfer In")) {
                        hiv.setTransferredInDate (getXmlDate (Date.valueOf (dateOfRegistration)));
                    }
                }
            }
        } catch (Exception ignore) {
        
        }
    }

    private void processAndSetCareEntryPoint(HIVQuestionsType hiv, String careEntryPoint) {
       if(careEntryPoint != null) {
           Optional<String> careEntryPointNdrCodeSet = ndrCodeSetResolverService.getNDRCodeSetCode("CARE_ENTRY_POINT", careEntryPoint);
           careEntryPointNdrCodeSet.ifPresent(hiv::setCareEntryPoint);
       }
    }

    private void processAndSetArtStartDate(HIVQuestionsType hiv,  LocalDate visitDate) {
        try {
            if (visitDate != null) {
                hiv.setARTStartDate (getXmlDate (Date.valueOf (visitDate)));
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

//    private void processAndHandleARTStatus(HIVQuestionsType hiv, Long personId, String enrollmentStatus) {
//        try {
//            String ndrARTStatus = enrollmentStatus == null ? "Pre-ART" : "ART";
//            String status = statusManagementService.getCurrentStatus (personId);
//            handlePatientTransferOut (hiv, personId, ndrARTStatus, status);
//            handlePatientDeathStatus (hiv, personId, ndrARTStatus, status);
//        } catch (Exception e) {
//           log.error ("An error occurred while processing client status message {}", e.getMessage());
//        }
//
//    }
//
//    private void handlePatientDeathStatus(HIVQuestionsType hiv, Long personId, String ndrARTStatus, String status) {
//        try {
//            log.info("status of the current patient {}", status);
//        if (status.contains("Died") || status.contains("DEATH") || status.contains("DIED")) {
//            //Optional<HIVStatusTrackerDto> patientStatus = getPatientStatus (personId, status);
//            HIVStatusDisplay clientReportingStatus = statusManagementService.getClientReportingStatus(personId);
//            log.info("current status handling death {} ", clientReportingStatus.getDescription());
//            Optional<String> artStatus = ndrCodeSetResolverService.getNDRCodeSetCode("ART_STATUS", ndrARTStatus);
//            artStatus.ifPresent(hiv::setStatusAtDeath);
//            log.info("art status at the death of the patient {}", artStatus);
//            log.info("ndr art status at the death of the patient {}", ndrARTStatus);
//
//            if (!artStatus.isPresent()) {
//                Optional<String> ndrART = Optional.of(ndrARTStatus);
//                String artStatusInitial = ndrART.map(statusInitial -> statusInitial.equalsIgnoreCase("ART") ? "A" : "P").orElse("P");
//                hiv.setStatusAtDeath(artStatusInitial);
//            }
//            hiv.setDeathDate(getXmlDate(Date.valueOf(clientReportingStatus.getDate())));
//            hiv.setPatientHasDied(true);
//        }
//    }catch (DatatypeConfigurationException e) {
//            log.error("An error occurred while handling Death status msg {}", e.getMessage());
//        }
//    }
//
//    private void handlePatientTransferOut(HIVQuestionsType hiv, Long personId, String ndrARTStatus, String status) {
//        try {
//            if (status.contains("Out")) {
//                //  Optional<HIVStatusTrackerDto> patientStatus = getPatientStatus (personId, status);
//                HIVStatusDisplay clientReportingStatus = statusManagementService.getClientReportingStatus(personId);
//                Optional<String> artStatus = ndrCodeSetResolverService.getNDRCodeSetCode("ART_STATUS", ndrARTStatus);
//                artStatus.ifPresent(hiv::setTransferredOutStatus);
//                hiv.setTransferredOutDate(getXmlDate(Date.valueOf(clientReportingStatus.getDate())));
//                hiv.setPatientTransferredOut(true);
//
//            } else {
//                hiv.setPatientTransferredOut(false);
//            }
//        } catch (Exception e) {
//            log.error("An error occurred while processing transfer-out client status msg {}", e.getMessage());
//        }
//    }

//    @NotNull
//    private Optional<HIVStatusTrackerDto> getPatientStatus(Long personId, String status) {
//        return hivStatusTrackerService.getPersonHIVStatusByPersonId (personId)
//                .stream ()
//                .filter (s -> s.getHivStatus ().equals (status))
//                .findFirst ();
//    }

    private void processAndHandleARTStatus(HIVQuestionsType hiv, Long personId, String enrollmentStatus) {
        try {
            String ndrARTStatus = enrollmentStatus == null ? "Pre-ART" : "ART";
            String status = statusManagementService.getCurrentStatus (personId);
            handlePatientTransferOut (hiv, personId, status);
            handlePatientDeathStatus (hiv, personId, status);
        } catch (Exception e) {
            log.error ("An error occurred while processing client status message {}", e.getMessage());
        }

    }

    private void handlePatientDeathStatus(HIVQuestionsType hiv, Long personId, String status) {
        try {
            if (status.contains("DIED") || status.contains("DEATH")) {
                HIVStatusDisplay clientReportingStatus = statusManagementService.getClientReportingStatus(personId);
                log.info("current status handling death {} ", clientReportingStatus.getDescription());
                hiv.setDeathDate(getXmlDate(Date.valueOf(clientReportingStatus.getDate())));
                hiv.setPatientHasDied(true);
            }
        }catch (DatatypeConfigurationException e) {
            log.error("An error occurred while handling Death status msg {}", e.getMessage());
        }
    }

    private void handlePatientTransferOut(HIVQuestionsType hiv, Long personId, String status) {
        try {
            if (status.contains("Out")) {
                HIVStatusDisplay clientReportingStatus = statusManagementService.getClientReportingStatus(personId);
                hiv.setTransferredOutStatus("TO");
                hiv.setTransferredOutDate(getXmlDate(Date.valueOf(clientReportingStatus.getDate())));
                hiv.setPatientTransferredOut(true);

            } else {
                hiv.setPatientTransferredOut(false);
            }
        } catch (Exception e) {
            log.error("An error occurred while processing transfer-out client status msg {}", e.getMessage());
        }
    }
    private void processAndSetCD4(HIVQuestionsType hiv, int age, ArtCommencementDTO artCommence) {
        Long cd4 = artCommence.getCd4 ();
        Long cd4p = artCommence.getCd4Percentage ();
        String clinicalStage = null;
        String eligible = null;
        if (cd4 == null) {
            cd4 = 0l;
        }
        if (cd4 == null) {
            cd4p = 0l;
        }
        String whyEligible = "WHY_ELIGIBLE";
        if (age >= 15) {
            if (cd4 < 350) {
                Optional<String> ndrCodeSet = ndrCodeSetResolverService.getNDRCodeSetCode (whyEligible, "CD4");
                if (ndrCodeSet.isPresent ()) eligible = ndrCodeSet.get ();
            } else {
                if (artCommence.getWhoStage() != null) {
                    clinicalStage = artCommence.getWhoStage();
                    if (clinicalStage.equalsIgnoreCase ("Stage III") ||
                            clinicalStage.equalsIgnoreCase ("Stage IV")) {
                        Optional<String> ndrCodeSet = ndrCodeSetResolverService.getNDRCodeSetCode (whyEligible, "Staging");
                        if (ndrCodeSet.isPresent ()) eligible = ndrCodeSet.get ();
                    }
                }
            }
        } else {
            if (cd4 < 750 || cd4p < 25) {
                Optional<String> ndrCodeSet;
                if (cd4 < 25) {
                    ndrCodeSet = ndrCodeSetResolverService.getNDRCodeSetCode (whyEligible, "CD4p");
                } else {
                    ndrCodeSet = ndrCodeSetResolverService.getNDRCodeSetCode (whyEligible, "CD4");
                }
                if (ndrCodeSet.isPresent ()) eligible = ndrCodeSet.get ();
            } else {
                if (clinicalStage.equalsIgnoreCase ("Stage III") ||
                        clinicalStage.equalsIgnoreCase ("Stage IV")) {
                    Optional<String> ndrCodeSet = ndrCodeSetResolverService.getNDRCodeSetCode (whyEligible, "Staging");
                    if (ndrCodeSet.isPresent ()) eligible = ndrCodeSet.get ();
                }
            }
        }
        try {
            hiv.setARTStartDate (getXmlDate (Date.valueOf (artCommence.getArtStartDate())));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace ();
        }
        try {
            hiv.setMedicallyEligibleDate (getXmlDate (Date.valueOf (artCommence.getArtStartDate())));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace ();
        }
        if (eligible != null && ! eligible.isEmpty ()) hiv.setReasonMedicallyEligible (eligible);
    }


}
