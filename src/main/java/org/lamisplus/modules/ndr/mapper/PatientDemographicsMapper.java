package org.lamisplus.modules.ndr.mapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.service.NDRCodeSetResolverService;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@Service
public class PatientDemographicsMapper {
    private final MessageHeaderTypeMapper messageHeaderTypeMapper;
    private  final NDRCodeSetResolverService ndrCodeSetResolverService;
    private  final  BiometricTemplateMapper biometricTemplateMapper;
    private final IdentifiersTypeMapper identifiersTypeMapper;
    
    public PatientDemographicsType getPatientDemographics(PatientDemographics patientDemographics) {
        PatientDemographicsType patientDemographicsType = new PatientDemographicsType ();
            try {
                    FacilityType treatmentFacility =
                            messageHeaderTypeMapper.getTreatmentFacility (patientDemographics);
                    String identifier = patientDemographics.getDatimId() + "_" + patientDemographics.getPersonUuid();
                    patientDemographicsType.setPatientIdentifier (identifier);
                    patientDemographicsType.setTreatmentFacility (treatmentFacility);
                    processAndSetDateOFBirth (patientDemographicsType, patientDemographics.getDateOfBirth());
                    processAndSetSex (patientDemographicsType, patientDemographics.getSex());
                    processAndSetEducationLevelCode (patientDemographicsType, patientDemographics.getEducation());
                    processAndSetMaritalStatusCode (patientDemographicsType, patientDemographics.getMaritalStatus());
                    processAndSetStateOfOrigin(patientDemographicsType, patientDemographics.getResidentialState());
                    processAndSetOccupationalStatusCode(patientDemographicsType, patientDemographics.getOccupation());
                    FingerPrintType fingerPrintTypeForPatient =
                            biometricTemplateMapper.getFingerPrintTypeForPatient (patientDemographics.getPersonUuid ());
                    if(fingerPrintTypeForPatient != null){
                        patientDemographicsType.setFingerPrints (fingerPrintTypeForPatient);
                    }
                return patientDemographicsType;
            } catch (Exception e) {
               e.printStackTrace ();
            }
        return null;
    }
    
    
   
    public PatientDemographicsType getPatientDemographics(PatientDemographicDTO demographicDTO) {
        //@XmlElement(name = "PatientIdentifier", required = true)
       // @XmlElement(name = "TreatmentFacility", required = true)
       // @XmlElement(name = "PatientDateOfBirth", required = true)
        //  @XmlElement(name = "PatientSexCode", required = true)
        PatientDemographicsType patientDemographicsType = new PatientDemographicsType ();
        //log.info("patientIdentifier {}", demographicDTO.getPatientIdentifier());
        try {
            if(StringUtils.isNotBlank(demographicDTO.getPatientIdentifier())) {
                patientDemographicsType.setPatientIdentifier(demographicDTO.getPatientIdentifier());
            }else {
                throw new IllegalArgumentException("Patient Identifier cannot be null");
            }
            //identifierChange missing
            FacilityType treatmentFacility =
                    messageHeaderTypeMapper.getTreatmentFacility (demographicDTO);
           
            if(treatmentFacility != null){
                patientDemographicsType.setTreatmentFacility (treatmentFacility);
            }else {
                throw new IllegalArgumentException("Treatment facility cannot be null");
            }
            //OtherPatientIdentifiers
            processAndSetDateOFBirth (patientDemographicsType, demographicDTO.getDateOfBirth ());
            
            if(demographicDTO.getPatientSexCode()  != null) {
                log.info("state code {}", demographicDTO.getStateCode());
                patientDemographicsType.setPatientSexCode(demographicDTO.getPatientSexCode());
            }else {
                throw new IllegalArgumentException("Sex code cannot be null");
            }
            //PatientDeceasedIndicator
            //PatientDeceasedDate
            //PatientPrimaryLanguageCode
            if( demographicDTO.getPatientEducationLevelCode() != null){
               patientDemographicsType.setPatientEducationLevelCode(demographicDTO.getPatientEducationLevelCode());
           }

           if(demographicDTO.getPatientOccupationCode() != null){
               patientDemographicsType.setPatientOccupationCode(demographicDTO.getPatientOccupationCode());
           }
            if( demographicDTO.getPatientMaritalStatusCode() != null){
                patientDemographicsType.setPatientMaritalStatusCode(demographicDTO.getPatientMaritalStatusCode());
            }

            if(demographicDTO.getStateCode() != null){
               log.info("state code {}", demographicDTO.getStateCode());
                patientDemographicsType.setStateOfNigeriaOriginCode(demographicDTO.getStateCode());
            }
            //PatientNotes
            FingerPrintType fingerPrintTypeForPatient =
                    biometricTemplateMapper.getFingerPrintTypeForPatient (demographicDTO.getPersonUuid ());
            if(fingerPrintTypeForPatient != null){
                patientDemographicsType.setFingerPrints (fingerPrintTypeForPatient);
            }
            return patientDemographicsType;
        } catch (Exception e) {
            log.info("An error occurred while trying to get patient with uuid {} demographics type error: {}",
                    demographicDTO.getPersonUuid(), e.getMessage());
            throw new IllegalStateException(e.toString());
        }
    }
    
    public PatientDemographicsType getPatientDemographics(PatientDemographicDTO demographicDTO, boolean isHts) {
        //@XmlElement(name = "PatientIdentifier", required = true)
        // @XmlElement(name = "TreatmentFacility", required = true)
        // @XmlElement(name = "PatientDateOfBirth", required = true)
        //  @XmlElement(name = "PatientSexCode", required = true)
        PatientDemographicsType patientDemographicsType = new PatientDemographicsType ();
        //log.info("patientIdentifier {}", demographicDTO.getPatientIdentifier());
        try {
            if(StringUtils.isNotBlank(demographicDTO.getPatientIdentifier())) {
                patientDemographicsType.setPatientIdentifier(demographicDTO.getPatientIdentifier());
            }else {
                throw new IllegalArgumentException("Patient Identifier cannot be null");
            }
            FacilityType treatmentFacility =
                    messageHeaderTypeMapper.getTreatmentFacility (demographicDTO);
            
            if(treatmentFacility != null){
                patientDemographicsType.setTreatmentFacility (treatmentFacility);
            }else {
                throw new IllegalArgumentException("Treatment facility cannot be null");
            }

            //GET IDENTIFIERS
            IdentifiersType identifiers = new IdentifiersType();

            IdentifierType identifierType = identifiersTypeMapper.getIdentifiers(demographicDTO);

            identifiers.getIdentifier().add(identifierType);

            //OtherPatientIdentifier
            patientDemographicsType.setOtherPatientIdentifiers(identifiers);

            processAndSetDateOFBirth (patientDemographicsType, demographicDTO.getDateOfBirth ());
            
            if(demographicDTO.getPatientSexCode()  != null) {
                patientDemographicsType.setPatientSexCode(demographicDTO.getPatientSexCode());
            }else {
                throw new IllegalArgumentException("Sex code cannot be null");
            }
            if( demographicDTO.getPatientEducationLevelCode() != null){
                patientDemographicsType.setPatientEducationLevelCode(demographicDTO.getPatientEducationLevelCode());
            }
            if( demographicDTO.getPatientMaritalStatusCode() != null){
                patientDemographicsType.setPatientMaritalStatusCode(demographicDTO.getPatientMaritalStatusCode());
            }
            
            if(demographicDTO.getPatientOccupationCode() != null){
                patientDemographicsType.setPatientOccupationCode(demographicDTO.getPatientOccupationCode());
            }
            
            if(demographicDTO.getStateCode() != null){
                patientDemographicsType.setStateOfNigeriaOriginCode(demographicDTO.getStateCode());
            }
            if(demographicDTO.getLgaCode() != null){

            }
//            FingerPrintType fingerPrintTypeForPatient =
//                    biometricTemplateMapper.getFingerPrintTypeForPatient (demographicDTO.getPersonUuid ());
//            if(fingerPrintTypeForPatient != null){
//                patientDemographicsType.setFingerPrints (fingerPrintTypeForPatient);
//            }
            return patientDemographicsType;
        } catch (Exception e) {
            log.info("An error occurred while trying to get patient with uuid {} demographics type error: {}",
                    demographicDTO.getPersonUuid(), e.getMessage());
            throw new IllegalStateException(e.toString());
        }
    }
 
    private void processAndSetDateOFBirth(PatientDemographicsType patientDemographics, LocalDate dateOfBirth)
            throws DatatypeConfigurationException {
        if (dateOfBirth != null) {
            patientDemographics.setPatientDateOfBirth (DateUtil.getXmlDate (Date.valueOf (dateOfBirth)));
        }else {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
    }

    private void processAndSetSex(PatientDemographicsType patientDemographics, String sex) {
        if(sex.equalsIgnoreCase("Female")){
            sex = "Female";
        }else {
            sex = "Male";
        }
            Optional<String> sexCode = ndrCodeSetResolverService.getNDRCodeSetCode ("SEX", sex);
            sexCode.ifPresent (patientDemographics::setPatientSexCode);
    }


    private void processAndSetEducationLevelCode(PatientDemographicsType demographicsType, String education) {
        if (education != null) {
            Optional<String> educationalLevelCode = ndrCodeSetResolverService.getNDRCodeSetCode ("EDUCATIONAL_LEVEL", education);
            educationalLevelCode.ifPresent (demographicsType::setPatientEducationLevelCode);
        }
    }

    private void processAndSetMaritalStatusCode(PatientDemographicsType demographicsType, String maritalStatus) {
        if (maritalStatus != null ) {
            Optional<String> maritalStatusCode = ndrCodeSetResolverService.getNDRCodeSetCode ("MARITAL_STATUS", maritalStatus);
            maritalStatusCode.ifPresent (demographicsType::setPatientMaritalStatusCode);
        }
    }

    private void processAndSetStateOfOrigin(PatientDemographicsType demographicsType, String currentState) {
        if (currentState != null) {
                    Optional<String> state = ndrCodeSetResolverService.getNDRCodeSetCode ("STATES", currentState);
                    state.ifPresent (demographicsType::setStateOfNigeriaOriginCode);
                }
        
    }

    private void processAndSetOccupationalStatusCode(PatientDemographicsType demographicsType, String occupation) {
        if (occupation != null) {
            Optional<String> occupationCode = ndrCodeSetResolverService.getNDRCodeSetCode ("OCCUPATION_STATUS", occupation);
            occupationCode.ifPresent (demographicsType::setPatientOccupationCode);
        }
    }


}
