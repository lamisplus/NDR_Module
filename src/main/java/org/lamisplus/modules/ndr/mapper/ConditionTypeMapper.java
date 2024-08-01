package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.service.NDRCodeSetResolverService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConditionTypeMapper {

    private final NDRCodeSetResolverService ndrCodeSetResolverService;

    private final AddressTypeMapper addressTypeMapper;

    private final CommonQuestionsTypeMapper commonQuestionsTypeMapper;

    private final ConditionSpecificQuestionsTypeMapper specificQuestionsTypeMapper;

    private final EncountersTypeMapper encountersTypeMapper;

    private final RegimenTypeMapper regimenTypeMapper;
    
    private final LaboratoryReportTypeMapper laboratoryReportTypeMapper;
    
    


    public ConditionType getConditionType(PatientDemographics demographics) {
       //List code Sets
        ConditionType condition = new ConditionType ();
        Optional<String> conditionCode = ndrCodeSetResolverService.getNDRCodeSetCode ("CONDITION_CODE", "HIV_CODE");
        conditionCode.ifPresent (condition::setConditionCode);
        Optional<String> programAreaCode = ndrCodeSetResolverService.getNDRCodeSetCode ("PROGRAM_AREA", "HIV");
        ProgramAreaType programArea = new ProgramAreaType ();
        programAreaCode.ifPresent (programArea::setProgramAreaCode);
        condition.setProgramArea (programArea);

        //Address
        // consider saving address somewhere
        AddressType address = addressTypeMapper.getPatientAddress (demographics);
        if (address.getStateCode () != null && address.getLGACode () != null) {
            condition.setPatientAddress (address);
        }
        
        
        //common questions
        CommonQuestionsType common = commonQuestionsTypeMapper.getPatientCommonQuestion (demographics);
        if (common != null) {
            condition.setCommonQuestions (common);
        }
        
        
        //specific questions
        ConditionSpecificQuestionsType disease =
                specificQuestionsTypeMapper.getConditionSpecificQuestionsType (demographics);
        if (disease != null) {
            condition.setConditionSpecificQuestions (disease);
            
        }
        //encounter
        EncountersType encounter = encountersTypeMapper.encounterType (demographics);
        if(encounter != null) {
            condition.setEncounters (encounter);
         }
        //
        regimenTypeMapper.regimenType (demographics, condition);
        LocalDateTime lastUpdated = LocalDateTime.of(1990, 1, 1, 0, 0);
        
        laboratoryReportTypeMapper.laboratoryReportType(demographics.getPersonUuid(), lastUpdated, condition);
        return condition;
    }
    
    public ConditionType getConditionType(
            PatientDemographics demographics,
            ArtCommencementDTO commencementDTO,
            List<ARTClinicalInfo> encounterDTOList,
            List<ArtPharmacy> pharmacies,
            List<LabDTO> labs) {
        //List code Sets
        ConditionType condition = new ConditionType();
        setProgramCodeAndArea(condition);
    
        //Address
        // consider saving address somewhere
        if (demographics != null) {
            AddressType address = addressTypeMapper.getPatientAddress(demographics);
            if (address.getStateCode() != null && address.getLGACode() != null) {
                condition.setPatientAddress(address);
            }
            
            //common questions
            CommonQuestionsType common = commonQuestionsTypeMapper.getPatientCommonQuestion(demographics);
            if (common != null) {
                condition.setCommonQuestions(common);
            }
            
        }
        
        //specific questions
        if (demographics != null && commencementDTO != null) {
            ConditionSpecificQuestionsType disease =
                    specificQuestionsTypeMapper.getConditionSpecificQuestionsType(demographics, commencementDTO);
            if (disease != null) {
                condition.setConditionSpecificQuestions(disease);
                
            }
        }
        if (demographics != null
                && (encounterDTOList != null && !encounterDTOList.isEmpty())
                && (pharmacies != null && !pharmacies.isEmpty())) {
            //encounter
            EncountersType encounter = encountersTypeMapper.encounterType(demographics, encounterDTOList, pharmacies);
            if (encounter != null) {
                condition.setEncounters(encounter);
            }
        }
        //
        if (demographics != null && (pharmacies != null && !pharmacies.isEmpty())) {
            regimenTypeMapper.regimenType(demographics, condition, pharmacies);
        }
        
        if ((labs != null && !labs.isEmpty())) {
            LocalDateTime lastUpdated = LocalDateTime.of(1990, 1, 1, 0, 0);
            laboratoryReportTypeMapper.laboratoryReportType(demographics.getPersonUuid(), lastUpdated, condition, labs);
        }
        
        return condition;
    }
    
    private static void setProgramCodeAndArea(ConditionType condition) {
        condition.setConditionCode("86406008");
        ProgramAreaType programArea = new ProgramAreaType();
        programArea.setProgramAreaCode("HIV");
        condition.setProgramArea(programArea);
    }
    
    public ConditionType getConditionType(PatientDemographics demographics, LocalDateTime lastUpdate) {
        ConditionType condition = new ConditionType();
        //List of applications code set
        setProgramCodeAndArea(condition);
        //Address
        AddressType address = addressTypeMapper.getPatientAddress(demographics);
        if (address.getStateCode() != null && address.getLGACode() != null) {
            condition.setPatientAddress(address);
        }
        //common questions
        CommonQuestionsType common = commonQuestionsTypeMapper.getPatientCommonQuestion(demographics);
        if (common != null) {
            condition.setCommonQuestions(common);
        }
        //specific questions
        ConditionSpecificQuestionsType disease = specificQuestionsTypeMapper.getConditionSpecificQuestionsType(demographics);
        if (disease != null) {
            condition.setConditionSpecificQuestions(disease);
        }
        //encounter
        EncountersType encounter = encountersTypeMapper.encounterType(demographics, lastUpdate);
        if (encounter != null) {
            condition.setEncounters(encounter);
        }
        regimenTypeMapper.regimenType(demographics, condition, lastUpdate);
        
        //Lab
        laboratoryReportTypeMapper.laboratoryReportType(demographics.getPersonUuid(), lastUpdate, condition);
        
        return condition;
    }
    
    public ConditionType getConditionType(PatientDemographicDTO demographics,
          List<EncounterDTO> patientEncounters,
           List<RegimenDTO> patientRegimens,
           List<LaboratoryEncounterDTO> patientLabEncounters) {
      
        ConditionType condition = new ConditionType();
        //List of applications code set
        setProgramCodeAndArea(condition);
        //Address
        AddressType address = addressTypeMapper.getPatientAddress(demographics);
        if (address.getStateCode() != null && address.getLGACode() != null) {
            condition.setPatientAddress(address);
        }
        //common questions
        CommonQuestionsType common = commonQuestionsTypeMapper.getPatientCommonQuestion(demographics);
        if (common != null) {
            condition.setCommonQuestions(common);
        }
        //specific questions
        ConditionSpecificQuestionsType disease = specificQuestionsTypeMapper.getConditionSpecificQuestionsType(demographics);
        if (disease != null) {
            condition.setConditionSpecificQuestions(disease);
        }
        //encounter
        EncountersType encounter = encountersTypeMapper.encounterType(patientEncounters, demographics);
        if (encounter != null) {
            condition.setEncounters(encounter);
        }
    
        regimenTypeMapper.regimenType(demographics, condition, patientRegimens);
    
        //Lab
        laboratoryReportTypeMapper.laboratoryReportType(demographics.getPersonUuid(), condition, patientLabEncounters);
        
        return condition;
    }
    
    public ConditionType getConditionType(PatientDemographicDTO demographics, boolean isHts) {
        ConditionType condition = new ConditionType();
        log.info("I am in address");
        //List of applications code set
        setProgramCodeAndArea(condition);
        //Address
        AddressType address = addressTypeMapper.getPatientAddress(demographics);
        if (address.getStateCode() != null && address.getLGACode() != null) {
            condition.setPatientAddress(address);
        }
        return condition;
    }

}
