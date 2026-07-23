package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.lamisplus.modules.hiv.domain.dto.HIVStatusDisplay;

import org.lamisplus.modules.hiv.service.StatusManagementService;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.domain.dto.ArtCommencementDTO;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.service.NDRCodeSetResolverService;
import java.util.*;
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
    private static final Map<String, String> WHO_STAGE_MAPPING = new HashMap<>();
    private static final Map<String, String> FUNCTIONAL_STATUS_MAPPING = new HashMap<>();
    private static final Map<String, String> CARE_ENTRY_POINT_MAPPING = new HashMap<>();
    private static final Map<String, String> KP_TYPOLOGY_MAPPING = new HashMap<>();
    private static final Map<String, String> PRIOR_ART_MAPPING = new HashMap<>();
    private static final Map<String, String> FIRST_HIV_TEST_MODE_MAPPING = new HashMap<>();
    private static final Map<String, String> INITIAL_TB_STATUS_MAPPING = new HashMap<>();
    private static final Map<Integer, String> TPT_MEDICATION_MAPPING = new HashMap<>();

    private final NDRCodeSetResolverService ndrCodeSetResolverService;
    
    private final NDRCodeSetRepository ndrCodeSetRepository;

    private final StatusManagementService statusManagementService;

    private final PregnancyStatus pregnancyStatus;

    public static class LogMessages {
        public static final String GENERATING_COMMON_QUESTIONS = "Generating condition specific questions for patient with uuid {}";
    }

    public static class LogErrorMessages {
        public static final String GENERATING_ERROR_MSG = "An error Generating condition specific questions for patient with uuid {}";
    }

    static {
        WHO_STAGE_MAPPING.put("CLINICAL_STAGE_STAGE_I", "I");
        WHO_STAGE_MAPPING.put("CLINICAL_STAGE_STAGE_II", "II");
        WHO_STAGE_MAPPING.put("CLINICAL_STAGE_STAGE_III", "III");
        WHO_STAGE_MAPPING.put("CLINICAL_STAGE_STAGE_IV", "IV");

        FUNCTIONAL_STATUS_MAPPING.put("W", "W");
        FUNCTIONAL_STATUS_MAPPING.put("A", "A");
        FUNCTIONAL_STATUS_MAPPING.put("B", "B");

        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_OPD", "OPD");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_IN-PATIENT", "Inpatients");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_HTS", "HTS");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_TB_DOTS", "TBDOTS");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_ANC_PMTCT", "ANC_PMTCT");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_TRANSFER-IN", "TransferIn");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_COMMUNITY", "Community");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_STI_CLINIC", "STI");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_HCT", "HCT");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_OTHERS", "Others");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_OUTREACH", "SexWorkersOutreach");
        CARE_ENTRY_POINT_MAPPING.put("POINT_ENTRY_CBO", "CBO");

        KP_TYPOLOGY_MAPPING.put("KP_TYPE_FSW", "FSW");
        KP_TYPOLOGY_MAPPING.put("KP_TYPE_MSM", "MSM");
        KP_TYPOLOGY_MAPPING.put("KP_TYPE_PWID", "PWID");
        KP_TYPOLOGY_MAPPING.put("KP_TYPE_Trans", "TG");
        KP_TYPOLOGY_MAPPING.put("KP_TYPE_PERSONS_IN_CUSTODIAL_CENTERS", "Prisoners");
        KP_TYPOLOGY_MAPPING.put("KP_TYPE_OTHERS", "OtherKP");

        PRIOR_ART_MAPPING.put("PRIOR_ART_EARLIER_ARV_BUT_NOT_A_TRANSFER_IN", "EarlierARV");
        PRIOR_ART_MAPPING.put("PRIOR_ART_TRANSFER_IN_WITHOUT_RECORDS", "TransferIn");
        PRIOR_ART_MAPPING.put("PRIOR_ART_PREP", "PREP");
        PRIOR_ART_MAPPING.put("PRIOR_ART_PEP", "PEP");

        FIRST_HIV_TEST_MODE_MAPPING.put("MODE_HIV_TEST_HIV-AB", "HIVAb");
        FIRST_HIV_TEST_MODE_MAPPING.put("MODE_HIV_TEST_PCR", "HIVPCR");

        INITIAL_TB_STATUS_MAPPING.put("TB_STATUS_NO_SIGN_OR_SYMPTOMS_OF_TB", "1");
        INITIAL_TB_STATUS_MAPPING.put("TB_STATUS_TB_SUSPECTED_AND_REFERRED_FOR_EVALUATION", "2");
        INITIAL_TB_STATUS_MAPPING.put("TB_STATUS_CURRENTLY_ON_INH_PROPHYLAXIS", "3");
        INITIAL_TB_STATUS_MAPPING.put("TB_STATUS_CURRENTLY_ON_TB_TREATMENT", "4");
        INITIAL_TB_STATUS_MAPPING.put("TB_STATUS_TB_POSITIVE_NOT_ON_TB_DRUGS", "5");

        TPT_MEDICATION_MAPPING.put(115, "SixH");
        TPT_MEDICATION_MAPPING.put(130, "SixH");
        TPT_MEDICATION_MAPPING.put(1096, "ThreeHP");
        TPT_MEDICATION_MAPPING.put(1095, "Other");
//        TPT_MEDICATION_MAPPING.put("Isoniazid + Rifapentine", "ThreeHP");
//        TPT_MEDICATION_MAPPING.put("Isoniazid + Rifampicin", "ThreeHR");

    }


    public ConditionSpecificQuestionsType getConditionSpecificQuestionsType(PatientDemographics demographics) {
        log.info(LogMessages.GENERATING_COMMON_QUESTIONS, demographics.getPersonUuid());
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
                     Optional<RegimenCodedSimpleType> simpleCodeSet = ndrCodeSetResolverService.getRegimen(regimen);
                     log.info("ndrRegimen: " + regimen);
                     simpleCodeSet.ifPresent(hiv::setFirstARTRegimen);
                     }
                    processAndSetCD4 (hiv, demographics.getAge(), artCommencement.get());
                }
                hivQuestions.setHIVQuestions (hiv);
            return hivQuestions;
        } catch (Exception e) {
            log.error(LogErrorMessages.GENERATING_ERROR_MSG,
                    demographics.getPersonUuid());
           log.error("Error Message:" + e.getMessage());
        }
        return null;

    }
    public ConditionSpecificQuestionsType getConditionSpecificQuestionsType(PatientDemographicDTO demographics) {
        log.info("updated part 4  --- A3");
        //@XmlElement(name = "EnrolledInHIVCareDate", required = true)
        log.info(LogMessages.GENERATING_COMMON_QUESTIONS, demographics.getPersonUuid());
        try {
            ConditionSpecificQuestionsType hivQuestions = new ConditionSpecificQuestionsType ();
            HIVQuestionsType hiv = new HIVQuestionsType ();
            if (demographics.getBiometricCaptured()) {
                hiv.setBiometricCaptured(YNCodeType.valueOf("YES"));
            } else {
                hiv.setBiometricCaptured(YNCodeType.valueOf("NO"));
            }

            if(demographics.getCareEntryPoint() != null){
                String mappedValue = CARE_ENTRY_POINT_MAPPING.get(demographics.getCareEntryPoint().trim());
                hiv.setCareEntryPoint(mappedValue);
            }

            if(demographics.getFirstHIVTestMode() != null){
                String mappedValue = FIRST_HIV_TEST_MODE_MAPPING.get(demographics.getFirstHIVTestMode().trim());
                hiv.setFirstHIVTestMode(mappedValue);
            }

            if(demographics.getPriorArt() != null){
                String mappedValue = PRIOR_ART_MAPPING.get(demographics.getPriorArt().trim());
                hiv.setPriorArt(mappedValue);
            }

            if(demographics.getKpTypology() != null){
                String mappedValue = KP_TYPOLOGY_MAPPING.get(demographics.getKpTypology().trim());
                hiv.setKPTypology(mappedValue);
            }

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
                        hiv.setPatientTransferredIn(true);
                        if (demographics.getTransferredInDate() != null) {
                            hiv.setTransferredInDate (getXmlDate (Date.valueOf (demographics.getTransferredInDate())));
                        }else {
                            hiv.setTransferredInDate(getXmlDate(Date.valueOf(inHIVCareDate)));
                        }
                        //TransferredInFrom
                    }
                    String tbStatus = demographics.getTbStatus();
                    //log.info("initial tb status {}", tbStatus);
                    if(tbStatus != null){
                        hiv.setInitialTBStatus(demographics.getTbStatus());
                    }else if (demographics.getTbStatusNew() != null) {
                        String mappedValue = INITIAL_TB_STATUS_MAPPING.get(demographics.getTbStatusNew());
                        hiv.setInitialTBStatus(mappedValue);
                    }
                    processAndHandleARTStatus (hiv, demographics.getPersonId(), statusAtRegistration);
                }
            }else {
                throw new IllegalArgumentException(" Enrolled In HIVCareDate cannot be null");
            }

            //log.info("art start date {}", demographics.getArtStartDate());
           
            if (demographics.getArtStartDate() != null) {
                hiv.setARTStartDate (getXmlDate (Date.valueOf ((demographics.getArtStartDate()))));
            }
            //log.info("condition specific questions " + demographics.getFirstARTRegimenCode() + " " + demographics.getFirstARTRegimenCodeDescTxt() + " " + demographics.getNdrCode());
            if(demographics.getFirstARTRegimenCode() != null && demographics.getFirstARTRegimenCodeDescTxt() != null
                    && demographics.getNdrCode() != null) {
                RegimenCodedSimpleType codedSimpleType = new RegimenCodedSimpleType();
                codedSimpleType.setCode(demographics.getFirstARTRegimenCode());
                codedSimpleType.setCodeDescTxt(demographics.getFirstARTRegimenCodeDescTxt());
                codedSimpleType.setNDRCode(demographics.getNdrCode());
                hiv.setFirstARTRegimen(codedSimpleType);
            }
            if(demographics.getFunctionalStatusStartART() != null){
                String mappedValue = FUNCTIONAL_STATUS_MAPPING.get(demographics.getFunctionalStatusStartART());
                hiv.setFunctionalStatusStartART(mappedValue);
            }

            if(demographics.getWHOClinicalStageART() != null){
                String mappedValue = WHO_STAGE_MAPPING.get(demographics.getWHOClinicalStageART());
                hiv.setWHOClinicalStageARTStart(mappedValue);
            }

            if(demographics.getWeightAtARTStart() != null){
                if (demographics.getWeightAtARTStart() > 200) {
                    hiv.setWeightAtARTStart(200);
                }
                hiv.setWeightAtARTStart(demographics.getWeightAtARTStart());
            }

            if(demographics.getHeightAtARTStart() != null){
                hiv.setHeightAtARTStart(demographics.getHeightAtARTStart());
            }
            //log.info("height {}", demographics.getHeightAtARTStart());
            if(demographics.getBmimuacAtARTStart() != null){
                hiv.setBMIMUACAtARTStart(demographics.getBmimuacAtARTStart());
            }

            if(demographics.getCd4AtStartOfART() != null){
                hiv.setCD4AtStartOfART(demographics.getCd4AtStartOfART());
            }

            String cd4CellCount = demographics.getCd4AtStartOfART();
            if (cd4CellCount != null && !cd4CellCount.trim().isEmpty()) {
                try {
                    int cd4 = Integer.parseInt(cd4CellCount.trim());

                    if (cd4 < 200) {
                        hiv.setCD4LFA("LessThan200");
                    } else {
                        hiv.setCD4LFA("GTEqual200");
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid CD4 Cell Count: {}", cd4CellCount);
                }
            }
            //log.info("cd4 date {}", demographics.getCd4AtStartOfART());
            String tptMedication = demographics.getTptMedication();
            if(tptMedication != null){
                try{
                    Integer key = Integer.parseInt(demographics.getTptMedication());
                    String mappedValue = TPT_MEDICATION_MAPPING.get(key);
                    hiv.setTPTMedication(mappedValue);
                }catch(NumberFormatException e){
                    log.warn("Invalid TPT medication value: {}", tptMedication);
                }
            }
            if(demographics.getTptDose() != null){
                hiv.setTPTDose(demographics.getTptDose());
            }

            if(demographics.getTptCompletionDate() != null){
                hiv.setTPTCompletionDate(getXmlDate (Date.valueOf ((demographics.getTptCompletionDate()))));
            }

            if(demographics.getTbTreatmentStartDate() != null){
                hiv.setTBTreatmentStartDate(getXmlDate (Date.valueOf ((demographics.getTbTreatmentStartDate()))));
            }

            Map<String, Object> status =
                    pregnancyStatus.getPregnancyBFStatusStatus(demographics.getPersonUuid());
            if (demographics.getPatientSexCode() != null && demographics.getPatientSexCode().contains("F")) {
                hiv.setPregnancyBFStatusAtStart((String) status.get("status"));
            }
            //log.info("TB start date {}", demographics.getTbTreatmentStartDate());
            hivQuestions.setHIVQuestions (hiv);
            return hivQuestions;
        } catch (Exception e) {
            log.error(LogErrorMessages.GENERATING_ERROR_MSG,
                    demographics.getPersonUuid());
            log.error("Error Message:" + e.getMessage());
        }
        return null;
        
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

    private void processAndHandleARTStatus(HIVQuestionsType hiv, Long personId, String enrollmentStatus) {
        try {
            
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
            cd4 = 0L;
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
