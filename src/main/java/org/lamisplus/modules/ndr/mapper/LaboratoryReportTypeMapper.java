/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lamisplus.modules.ndr.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.LabDTO;
import org.lamisplus.modules.ndr.domain.dto.LaboratoryEncounterDTO;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.service.NDRCodeSetResolverService;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.lamisplus.modules.ndr.utility.NumericUtils;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class LaboratoryReportTypeMapper {
    private static final Map<Integer, String> VIRAL_LOAD_INDICATION_CODE_MAPPING = new HashMap<>();
    private static final Map<Integer, String> DRUG_REGIMEN_CODE_TYPE = new HashMap<>();

    static {
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(300, "Baseline");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(301, "Routine");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(302, "Confirmation");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(303, "Routine");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(304, "ClinicalFailure");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(305, "ImmunologicFailure");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(306, "Gestation3236Weeks");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(719, "RecentInfection");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(1394, "Baseline");
        VIRAL_LOAD_INDICATION_CODE_MAPPING.put(2216, "EarlyHIVDetection");

        DRUG_REGIMEN_CODE_TYPE.put(1, "FirstLine");
        DRUG_REGIMEN_CODE_TYPE.put(2, "SecondLine");
        DRUG_REGIMEN_CODE_TYPE.put(3, "FirstLine");
        DRUG_REGIMEN_CODE_TYPE.put(4, "SecondLine");
        DRUG_REGIMEN_CODE_TYPE.put(14, "ThirdLine");
        DRUG_REGIMEN_CODE_TYPE.put(16, "ThirdLine");
    }
    private final NdrXmlStatusRepository ndrXmlStatusRepository;

    private final NDRCodeSetResolverService ndrCodeSetResolverService;

    private static final String VIRALLOAD = "Viral Load";
    private static final String VISITMESSAGE = "visitId cannot be null";
    private static final String LESSTE200 = "LessTE200";
    private static final String POSITIVE = "Positive";
    private static final String NEGATIVE = "Negative";

    public void laboratoryReportType(String patientUuid, LocalDateTime lastGenerateTime, ConditionType condition) {

        List<LabDTO> labDTOS = ndrXmlStatusRepository.getLabInfoByPersonUuid(patientUuid, lastGenerateTime);
        labDTOS.forEach(labDTO -> {
            try {

                LaboratoryReportType laboratory = new LaboratoryReportType();
                laboratory.setVisitID(labDTO.getVisitId());
                LocalDateTime dateSampleCollected = labDTO.getDateSampleCollected();
                log.info("dateSampleCollected {}", dateSampleCollected);
                if (dateSampleCollected != null) {
                    Date dateCollected = java.sql.Date.valueOf(dateSampleCollected.toLocalDate());
                    laboratory.setVisitDate(DateUtil.getXmlDate(dateCollected));
                    laboratory.setCollectionDate(DateUtil.getXmlDate(dateCollected));
                    laboratory.setLaboratoryTestIdentifier("0000001");
                    //laboratoryTestTypeCode
                    String description = labDTO.getLabTestName();
                    log.info(" lab test name {}", description);
                    LaboratoryOrderAndResult labResult = new LaboratoryOrderAndResult();


                    Optional<CodedSimpleType> labCode =
                            ndrCodeSetResolverService.getCodeSet("LAB_RESULTED_TEST", description);
                    if (labCode.isPresent()) {
                        CodedSimpleType ndrCodeSet = labCode.get();
                        log.info(" lab  code {}", ndrCodeSet.getCode());
                        labResult.setLaboratoryResultedTest(ndrCodeSet);
                    }
                    String result = StringUtils.trimToEmpty(labDTO.getResultReported());

                    if (StringUtils.isNotEmpty(result)) {
                        //Set the NDR code & description for this lab test
                        log.info(" result is available: {}", result);
                        LocalDateTime dateAssayed = labDTO.getDateAssayed();
                        if (dateAssayed != null) {
                            Date dateAssayedDate = java.sql.Date.valueOf(dateAssayed.toLocalDate());
                            labResult.setOrderedTestDate(DateUtil.getXmlDate(dateAssayedDate));

                        }
                        LocalDateTime dateReportedTime = labDTO.getResultDate();
                        if (dateReportedTime != null) {
                            Date dateReported = java.sql.Date.valueOf(dateReportedTime.toLocalDate());
                            labResult.setResultedTestDate(DateUtil.getXmlDate(dateReported));
                        }
                        //Set the lab test result values either numeric or text
                        AnswerType answer = new AnswerType();
                        NumericType numeric = new NumericType();
                        if (NumericUtils.isNumeric(StringUtils.replace(result, ",", ""))) {
                            double d = Double.parseDouble(StringUtils.replace(result, ",", ""));
                            numeric.setValue1((int) d);
                            answer.setAnswerNumeric(numeric);
                        } else {
                            if (labDTO.getLabTestName().equals(VIRALLOAD)) {
                                numeric.setValue1(0);   //if lab test is a viralLoad set the value to 0
                                answer.setAnswerNumeric(numeric);
                            } else {
                                answer.setAnswerText(result);
                            }
                        }
                        labResult.setLaboratoryResult(answer);
                        labResult.setLaboratoryTestTypeCode("00001");
                        laboratory.getLaboratoryOrderAndResult().add(labResult);
                        if (laboratory.getVisitDate() != null && laboratory.getLaboratoryOrderAndResult() != null) {
                            condition.getLaboratoryReport().add(laboratory);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void laboratoryReportType(
            String patientUuid,
            ConditionType condition,
            List<LaboratoryEncounterDTO> labDTOS) {
        List<LaboratoryReportType> laboratoryReport = condition.getLaboratoryReport();
        //log.info("mapping lab encounters ...");
        if (labDTOS != null) {
            labDTOS.forEach(labDTO -> {
                log.info("mapping lab for patient " + patientUuid);
                try {
                    LaboratoryReportType laboratory = new LaboratoryReportType();

                    if (labDTO.getVisitId() != null) {
                        laboratory.setVisitID(labDTO.getVisitId());
                    } else {
                        throw new IllegalArgumentException(VISITMESSAGE);
                    }

                    String visitDate = labDTO.getVisitDate();
                    if (StringUtils.isNotBlank(visitDate)) {
                        LocalDate localDate = LocalDate.parse(visitDate);
                        try {
                            laboratory.setVisitDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                        } catch (DatatypeConfigurationException e) {
                            throw new IllegalArgumentException(e);
                        }
                    } else {
                        throw new IllegalArgumentException(VISITMESSAGE);
                    }

                    String collectionDate = labDTO.getCollectionDate();
                    if (StringUtils.isNotBlank(collectionDate)) {
                        LocalDate localDate = LocalDate.parse(collectionDate);
                        try {
                            laboratory.setCollectionDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                        } catch (DatatypeConfigurationException e) {
                            throw new IllegalArgumentException(e);
                        }
                    } else {
                        throw new IllegalArgumentException(VISITMESSAGE);
                    }
                    laboratory.setLaboratoryTestIdentifier(labDTO.getLaboratoryTestIdentifier());

                    //baselineRepeatCode TODO: provide the logic
                    if (labDTO.getViralLoadIndicationCode() != null && (labDTO.getViralLoadIndicationCode() == 300
                    || labDTO.getViralLoadIndicationCode() == 1394)) {
                        laboratory.setBaselineRepeatCode("B");
                    }else {
                        laboratory.setBaselineRepeatCode("R");
                    }
                    //artStatusCode TODO: provide the logic
                    if (labDTO.getArtStatusCode() != null) {
                        laboratory.setBaselineRepeatCode("POSITIVE");
                    }
                    if (labDTO.getReportedBy() != null) {
                        laboratory.setReportedBy(labDTO.getReportedBy());
                    }
                    if (labDTO.getCheckedBy() != null) {
                        laboratory.setCheckedBy(labDTO.getCheckedBy());
                    }

                    String result =
                            labDTO.getLaboratoryResultAnswerNumeric();

                    if (result != null) {
                        LaboratoryOrderAndResult labResult = new LaboratoryOrderAndResult();
                        String orderedTestDate = labDTO.getOrderedTestDate();
                        if (StringUtils.isNotBlank(orderedTestDate)) {
                            LocalDate localDate = LocalDate.parse(orderedTestDate);
                            try {
                                labResult.setOrderedTestDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                            } catch (DatatypeConfigurationException e) {
                                throw new IllegalArgumentException(e);
                            }
                        } else {
                            LocalDate localDate = LocalDate.parse(labDTO.getCollectionDate());
                            try {
                                labResult.setOrderedTestDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                            } catch (DatatypeConfigurationException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                        // laboratoryOrderedTest
                        CodedSimpleType codedOrderType = new CodedSimpleType();
                        if (labDTO.getLaboratoryTestTypeCode() != null
                                && labDTO.getLaboratoryResultedTestCodeDescTxt() != null) {
                            codedOrderType.setCode(labDTO.getLaboratoryTestTypeCode());
                            codedOrderType.setCodeDescTxt(labDTO.getLaboratoryResultedTestCodeDescTxt());
                            labResult.setLaboratoryOrderedTest(codedOrderType);
                        }

                        String resultedTestDate = labDTO.getResultedTestDate();
                        if (StringUtils.isNotBlank(resultedTestDate)) {
                            LocalDate localDate = LocalDate.parse(resultedTestDate);
                            try {
                                labResult.setResultedTestDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                            } catch (DatatypeConfigurationException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }

                        CodedSimpleType codedSimpleType = new CodedSimpleType();
                        if (labDTO.getLaboratoryResultedTestCode() != null
                                && labDTO.getLaboratoryResultedTestCodeDescTxt() != null) {
                            codedSimpleType.setCode(labDTO.getLaboratoryResultedTestCode());
                            codedSimpleType.setCodeDescTxt(labDTO.getLaboratoryResultedTestCodeDescTxt());
                            labResult.setLaboratoryResultedTest(codedSimpleType);
                        } else {
                            throw new IllegalArgumentException("Result code cannot be null");
                        }
                        AnswerType answer = new AnswerType();
                        NumericType numeric = new NumericType();
                        if (NumericUtils.isNumeric(StringUtils.replace(result, ",", ""))) {
                            double d = Double.parseDouble(StringUtils.replace(result, ",", ""));
                            numeric.setValue1((int) d);
                            answer.setAnswerNumeric(numeric);
                        } else {
                            if (labDTO.getLaboratoryResultedTestCodeDescTxt().equalsIgnoreCase(VIRALLOAD)) {
                                numeric.setValue1(0);  //if lab test is a viralLoad set the value to 0
                                answer.setAnswerNumeric(numeric);
                            } else {
                                CodedType answerCode = new CodedType();
                                if (result.equalsIgnoreCase("<200") || result.equalsIgnoreCase(">=200")) {
                                    codedSimpleType.setCode("83");
                                    codedSimpleType.setCodeDescTxt("CD4 LFA RESULT");
                                    labResult.setLaboratoryResultedTest(codedSimpleType);
                                    labResult.setLaboratoryTestTypeCode("83");
                                    if (result.equalsIgnoreCase("<200")) {
                                        answerCode.setCode(LESSTE200);
                                        answerCode.setCodeSystemCode(LESSTE200);
                                        answerCode.setCodeDescTxt(LESSTE200);
                                    } else {
                                        answerCode.setCode("GreaterTE200");
                                        answerCode.setCodeSystemCode("GreaterTE200");
                                        answerCode.setCodeDescTxt("GreaterTE200");
                                    }
                                    answer.setAnswerCode(answerCode);
                                } else {
                                    if (result.contains("+")
                                            || result.contains("Pos")
                                            || result.contains("pos") || result.equalsIgnoreCase(POSITIVE)) {
                                        answerCode.setCode(POSITIVE);
                                        answerCode.setCodeSystemCode(POSITIVE);
                                        answerCode.setCodeDescTxt(POSITIVE);
                                    } else {
                                        answerCode.setCode(NEGATIVE);
                                        answerCode.setCodeSystemCode(NEGATIVE);
                                        answerCode.setCodeDescTxt(NEGATIVE);
                                    }
                                    answer.setAnswerCode(answerCode);
                                }
                            }
                        }
                        labResult.setLaboratoryResult(answer);
                        labResult.setLaboratoryTestTypeCode(labDTO.getLaboratoryTestTypeCode());

                        if (labDTO.getPcrpocLabSampleNumber() != null) {
                            labResult.setSampleLoggedRemotely(YNCodeType.valueOf("YES"));
                        }else{
                            labResult.setSampleLoggedRemotely(YNCodeType.valueOf("NO"));
                        }

                        //sampleReceivedAtLabDate
                        //pcrpocLabName
                        if(labDTO.getPcrpocLabName() != null) {
                            labResult.setPCRPOCLabName(labDTO.getPcrpocLabName());
                        }
                        //pcrpocLabSampleNumber
                        if(labDTO.getPcrpocLabSampleNumber() != null) {
                            labResult.setPCRPOCLabSampleNumber(labDTO.getPcrpocLabSampleNumber());
                        }
                        //viralLoadIndicationCode
                        if(labDTO.getViralLoadIndicationCode() != null){
                            String mappedValue = VIRAL_LOAD_INDICATION_CODE_MAPPING.get(labDTO.getViralLoadIndicationCode());
                            labResult.setViralLoadIndicationCode(mappedValue);
                        }
                        //viralLoadResult
                        if (labDTO.getLaboratoryResultAnswerNumeric() != null
                                && !labDTO.getLaboratoryResultAnswerNumeric().trim().isEmpty()) {

                            labResult.setViralLoadResult(
                                    new BigDecimal(labDTO.getLaboratoryResultAnswerNumeric().trim())
                            );
                        }
                        //viralLoadResultDate
                        String viralLoadResultDate = labDTO.getResultedTestDate();
                        if (StringUtils.isNotBlank(viralLoadResultDate)) {
                            LocalDate localDate = LocalDate.parse(viralLoadResultDate);
                            try {
                                labResult.setViralLoadResultDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                            } catch (DatatypeConfigurationException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                        // cd4CellCount
                        if(labDTO.getCd4CellCount() != null && !labDTO.getCd4CellCount().trim().isEmpty()) {
                            labResult.setCD4CellCount(
                                    new BigDecimal(labDTO.getCd4CellCount().trim())
                            );
                            //labResult.setCD4CellCount(BigDecimal.valueOf(Long.parseLong(labDTO.getCd4CellCount())));
                        }

                        if(labDTO.getCd4Percentage() != null && !labDTO.getCd4Percentage().trim().isEmpty()) {
                            labResult.setCD4Percentage(
                                    new BigDecimal(labDTO.getCd4Percentage().trim())
                            );
                            //labResult.setCD4Percentage(BigDecimal.valueOf(Long.parseLong(labDTO.getCd4Percentage())));
                        }

                        String cd4CellCount = labDTO.getCd4CellCount();
                        if (cd4CellCount != null && !cd4CellCount.trim().isEmpty()) {
                            try {
                                int cd4 = Integer.parseInt(cd4CellCount.trim());

                                if (cd4 < 200) {
                                    labResult.setCD4LFAResultCode("LessThan200");
                                } else {
                                    labResult.setCD4LFAResultCode("GTEqual200");
                                }
                            } catch (NumberFormatException e) {
                                log.warn("Invalid CD4 Cell Count: {}", cd4CellCount);
                            }
                        }

                        // artStartDate
                        String artStartDate = labDTO.getArtStartDate();
                        if (StringUtils.isNotBlank(artStartDate)) {
                            LocalDate localDate = LocalDate.parse(artStartDate);
                            try {
                                labResult.setARTStartDate(DateUtil.getXmlDate(Date.valueOf(localDate)));
                            } catch (DatatypeConfigurationException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }

                        if(labDTO.getDrugRegimenLineCode() != null){
                            String mappedValue = DRUG_REGIMEN_CODE_TYPE.get(Integer.parseInt(labDTO.getDrugRegimenLineCode()));
                            labResult.setDrugRegimenLineCode(mappedValue);
                        }

                        laboratory.getLaboratoryOrderAndResult().add(labResult);
                        laboratoryReport.add(laboratory);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw  e;
                }
            });
        }
        log.info("lab map size: " + laboratoryReport.size());
    }

}
