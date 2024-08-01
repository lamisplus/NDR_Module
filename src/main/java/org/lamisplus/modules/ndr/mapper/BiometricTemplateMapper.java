package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.module.ModuleService;
import org.lamisplus.modules.ndr.domain.dto.BiometricDto;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.schema.FingerPrintType;
import org.lamisplus.modules.ndr.schema.LeftHandType;
import org.lamisplus.modules.ndr.schema.RightHandType;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BiometricTemplateMapper {

    private final ModuleService moduleService;

    private final NDRCodeSetRepository ndrCodeSetRepository;


    public FingerPrintType getFingerPrintTypeForPatient(String patientUuid) {
        log.info ("Generating FingerPrint of patient with uuid {} ", patientUuid);
        boolean biometricModule = moduleService.exist ("biometricModule");
        log.info ("biometric install {}", biometricModule);
        try {
            if (biometricModule) {
                List<BiometricDto> biometrics = ndrCodeSetRepository.getPatientBiometricByPatientUuid(patientUuid);
                if (biometrics.size() > 2) {
                    FingerPrintType fingerPrintType = new FingerPrintType();
                    RightHandType rightHandType = new RightHandType();
                    LeftHandType leftHandType = new LeftHandType();
                    List<BiometricDto> rights = biometrics.stream().filter(finger -> finger.getTemplateType().contains("Right"))
                            .collect(Collectors.toList());
                    List<BiometricDto> lefts = biometrics.stream().filter(finger -> finger.getTemplateType().contains("Left"))
                            .collect(Collectors.toList());
                    
                    if(rights.isEmpty() || lefts.isEmpty()){
                         throw new IllegalArgumentException("At least one right finger or left finger must be captured");
                    }
                    biometrics.forEach(biometricDto -> {
                        setEnrollmentDate(biometricDto, fingerPrintType);
//                        if (biometricDto.getReplaceDate() != null && biometricDto.getReplacePrintCount() != null) {
//                            log.info(biometricDto.getReplaceDate().toString() + " " + biometricDto.getReplacePrintCount());
//                            fingerPrintType.setReplacePrint(biometricDto.getReplacePrintCount());
//                        }
                        String type = biometricDto.getTemplateType();
                        String template = Base64.getEncoder().encodeToString(biometricDto.getTemplate());
                        if (StringUtils.containsIgnoreCase(type, "RIGHT")) {
                            if (StringUtils.containsIgnoreCase(type, "Thumb")) {
                                rightHandType.setRightThumb(template);
                                rightHandType.setRightIndexQuality(biometricDto.getQuality());
                            } else if (StringUtils.containsIgnoreCase(type, "Index")) {
                                rightHandType.setRightIndex(template);
                                rightHandType.setRightIndexQuality(biometricDto.getQuality());
                            } else if (StringUtils.containsIgnoreCase(type, "Middle")) {
                                rightHandType.setRightMiddle(template);
                                rightHandType.setRightMiddleQuality(biometricDto.getQuality());
                            } else if (StringUtils.containsIgnoreCase(type, "Little")) {
                                rightHandType.setRightSmall(template);
                                rightHandType.setRightSmallQuality(biometricDto.getQuality());
                            } else {
                                rightHandType.setRightWedding(template);
                                rightHandType.setRightWeddingQuality(biometricDto.getQuality());
                            }
                            fingerPrintType.setRightHand(rightHandType);
                        } else {
                            if (StringUtils.containsIgnoreCase(type, "Thumb")) {
                                leftHandType.setLeftThumb(template);
                                leftHandType.setLeftThumbQuality(biometricDto.getQuality());
                            } else if (StringUtils.containsIgnoreCase(type, "Index")) {
                                leftHandType.setLeftIndex(template);
                                leftHandType.setLeftIndexQuality(biometricDto.getQuality());
                            } else if (StringUtils.containsIgnoreCase(type, "Middle")) {
                                leftHandType.setLeftMiddle(template);
                                leftHandType.setLeftMiddleQuality(biometricDto.getQuality());
                            } else if (StringUtils.containsIgnoreCase(type, "Little")) {
                                leftHandType.setLeftSmall(template);
                                leftHandType.setLeftSmallQuality(biometricDto.getQuality());
                            } else {
                                leftHandType.setLeftWedding(template);
                                leftHandType.setLeftWeddingQuality(biometricDto.getQuality());
                            }
                            fingerPrintType.setLeftHand(leftHandType);
                        }
                    });
                    return fingerPrintType;
                } else {
                    return null;
                }
            }
        }catch (Exception e) {
          log.error("An error occurred while creating the fingerPrint");
          log.error("Error message {}",e.getMessage());
          throw new IllegalArgumentException(e.toString());
        }
        return null;
    }

    private void setEnrollmentDate(BiometricDto biometricDto, FingerPrintType fingerPrintType) {
        try {
            fingerPrintType.setDateCaptured (DateUtil.getXmlDate (Date.valueOf (biometricDto.getEnrollmentDate ())));
        } catch (DatatypeConfigurationException e) {
           e.printStackTrace ();
        }
    }
}
