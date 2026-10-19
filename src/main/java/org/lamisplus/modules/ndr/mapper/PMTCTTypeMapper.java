package org.lamisplus.modules.ndr.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PmtctDTO;
import org.lamisplus.modules.ndr.domain.dto.pmtct.DeliveryChildrenDetailsProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.InfantCohortRegistrationProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.MotherInfantPairVisitProjection;
import org.lamisplus.modules.ndr.domain.dto.pmtct.PMTCTRegisterProjection;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.DateUtil;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PMTCTTypeMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public boolean getPMTCTType(
            IndividualReportType individualReportType,
            ObjectFactory objectFactory,
            PmtctDTO pmtctDTO,
            List<NDRErrorDTO> ndrErrors,
            String patientIdentifier) {

        if (pmtctDTO == null || pmtctDTO.isEmpty()) {
            return false;
        }

        PMTCTType pmtctType = objectFactory.createPMTCTType();
        boolean anyMapped = false;

        if (pmtctDTO.getPmtctRegisters() != null) {
            for (PMTCTRegisterProjection row : pmtctDTO.getPmtctRegisters()) {
                try {
                    PMTCTRegisterType reg = mapPmtctRegister(objectFactory, row);
                    if (reg != null) {
                        pmtctType.getPMTCTRegister().add(reg);
                        anyMapped = true;
                    }
                } catch (Exception e) {
                    log.error("Error mapping PMTCTRegister for patient {}: {}", patientIdentifier, e.getMessage(), e);
                    ndrErrors.add(new NDRErrorDTO(patientIdentifier, row.getVisitId(), Arrays.toString(e.getStackTrace())));
                }
            }
        }

        if (pmtctDTO.getMotherInfantPairVisits() != null) {
            for (MotherInfantPairVisitProjection row : pmtctDTO.getMotherInfantPairVisits()) {
                try {
                    MotherInfantPairVisitType visit = mapMotherInfantPairVisit(objectFactory, row);
                    if (visit != null) {
                        pmtctType.getMotherInfantPairVisit().add(visit);
                        anyMapped = true;
                    }
                } catch (Exception e) {
                    log.error("Error mapping MotherInfantPairVisit for patient {}: {}", patientIdentifier, e.getMessage(), e);
                    ndrErrors.add(new NDRErrorDTO(patientIdentifier, row.getVisitId(), Arrays.toString(e.getStackTrace())));
                }
            }
        }

        if (pmtctDTO.getDeliveryChildrenDetails() != null) {
            for (DeliveryChildrenDetailsProjection row : pmtctDTO.getDeliveryChildrenDetails()) {
                try {
                    DeliveryChildrenDetailsType delivery = mapDeliveryChildrenDetails(objectFactory, row);
                    if (delivery != null) {
                        pmtctType.getDeliveryChildrenDetails().add(delivery);
                        anyMapped = true;
                    }
                } catch (Exception e) {
                    log.error("Error mapping DeliveryChildrenDetails for patient {}: {}", patientIdentifier, e.getMessage(), e);
                    ndrErrors.add(new NDRErrorDTO(patientIdentifier, row.getDeliveryUuid(), Arrays.toString(e.getStackTrace())));
                }
            }
        }

        if (pmtctDTO.getInfantCohortRegistrations() != null) {
            for (InfantCohortRegistrationProjection row : pmtctDTO.getInfantCohortRegistrations()) {
                try {
                    InfantCohortRegistrationType cohort = mapInfantCohortRegistration(objectFactory, row);
                    if (cohort != null) {
                        pmtctType.getInfantCohortRegistration().add(cohort);
                        anyMapped = true;
                    }
                } catch (Exception e) {
                    log.error("Error mapping InfantCohortRegistration for patient {}: {}", patientIdentifier, e.getMessage(), e);
                    ndrErrors.add(new NDRErrorDTO(patientIdentifier, row.getInfantUuid(), Arrays.toString(e.getStackTrace())));
                }
            }
        }

        if (anyMapped) {
            individualReportType.getPMTCT().add(pmtctType);
        }
        return anyMapped;
    }

    private PMTCTRegisterType mapPmtctRegister(ObjectFactory objectFactory, PMTCTRegisterProjection row) throws Exception {
        if (StringUtils.isBlank(row.getVisitId()) || row.getVisitDate() == null) {
            return null;
        }

        PMTCTRegisterType reg = objectFactory.createPMTCTRegisterType();
        reg.setVisitID(row.getVisitId().trim());
        reg.setVisitDate(toXmlDate(row.getVisitDate()));

        if (StringUtils.isNotBlank(row.getHospitalNo())) {
            reg.setHospitalNo(row.getHospitalNo().trim());
        }
        if (StringUtils.isNotBlank(row.getAncNumber())) {
            reg.setANCNumber(row.getAncNumber().trim());
        }

        PregnancyBreastfeedingStatusType pregBfStatus = mapPregnancyBreastfeedingStatus(row.getPregnancyBreastfeedingStatus());
        if (pregBfStatus != null) {
            reg.setPregnancyBreastfeedingStatus(pregBfStatus);
        }

        if (row.getGestationalAgeAtBooking() != null && row.getGestationalAgeAtBooking() >= 0) {
            reg.setGestationalAgeAtBooking(BigInteger.valueOf(row.getGestationalAgeAtBooking()));
        }

        KnownHIVPositiveStatusType knownHiv = mapKnownHivPositive(row.getPreviouslyKnownHivStatus(), row.getCurrentlyOnArt());
        if (knownHiv != null) {
            reg.setKnownHIVPositive(knownHiv);
        }

        String hivEarlyAcute = mapHivEarlyAcute(row.getHivEarlyAcute());
        if (hivEarlyAcute != null) {
            reg.setHIVEarlyAcute(hivEarlyAcute);
        }

        String hivEarlyVl = mapHivEarlyViralLoad(row.getHivEarlyViralLoad());
        if (hivEarlyVl != null) {
            reg.setHIVEarlyViralLoad(hivEarlyVl);
        }

        reg.setHIVTRANC(mapPosNeg(row.getHivTrAnc()));
        reg.setHIVTRLD(mapPosNeg(row.getHivTrLd()));
        reg.setHIVTRBF(mapPosNeg(row.getHivTrBf()));
        reg.setHIVRTANC(mapPosNeg(row.getHivRtAnc()));
        reg.setHIVRTLD(mapPosNeg(row.getHivRtLd()));
        reg.setHIVRTBF(mapPosNeg(row.getHivRtBf()));

        if (row.getDateOfInitiation() != null) {
            reg.setDateOfInitiation(toXmlDate(row.getDateOfInitiation()));
        }

        String timingArt = mapRegisterArtTiming(row.getTimingOfArtInitiation());
        if (timingArt != null) {
            reg.setTimingOfArtInitiation(timingArt);
        }

        // Syphilis
        String syphResult = mapPosNeg(row.getSyphilisTestResult());
        String syphReferral = mapRegisterSyphilisReferral(row.getSyphilisTreated(), row.getSyphilisReferred());
        if (syphResult != null || syphReferral != null) {
            RegisterSyphilisType syph = objectFactory.createRegisterSyphilisType();
            syph.setTestResult(syphResult);
            syph.setTreatmentReferral(syphReferral);
            reg.setSyphilis(syph);
        }

        // HepatitisB
        Boolean hbvKnownPos = parseBoolean(row.getHbvKnownPositive());
        String hbvResult = mapPosNeg(row.getHbvTestResult());
        String hbvReferral = mapRegisterHbvReferral(row.getHbvTreated(), row.getHbvReferred(), row.getHbvTestResult());
        if (hbvKnownPos != null || hbvResult != null || hbvReferral != null) {
            RegisterHBVType hbv = objectFactory.createRegisterHBVType();
            hbv.setKnownPositive(hbvKnownPos);
            hbv.setTestResult(hbvResult);
            hbv.setTreatmentReferral(hbvReferral);
            reg.setHepatitisB(hbv);
        }

        String initProphylaxis = mapInitiatedOnProphylaxis(row.getInitiatedOnProphylaxis());
        if (initProphylaxis != null) {
            reg.setInitiatedOnProphylaxis(initProphylaxis);
        }

        // TBScreening
        String tbStatus = mapRegisterTbStatus(row.getTbScreeningStatus());
        String tbReferral = mapRegisterTbReferral(row.getTbTreatmentReferral());
        if (tbStatus != null || tbReferral != null) {
            RegisterTBScreeningType tb = objectFactory.createRegisterTBScreeningType();
            tb.setStatus(tbStatus);
            tb.setTreatmentReferral(tbReferral);
            reg.setTBScreening(tb);
        }

        // ViralLoad
        VLMeasurementType vlLt32 = buildVlMeasurement(objectFactory, row.getVlLessThan32WeeksDate(), row.getVlLessThan32WeeksResult());
        VLMeasurementType vl32To36 = buildVlMeasurement(objectFactory, row.getVl32To36WeeksDate(), row.getVl32To36WeeksResult());
        VLMeasurementType vlBf = buildVlMeasurement(objectFactory, row.getVlBreastfeedingDate(), row.getVlBreastfeedingResult());
        if (vlLt32 != null || vl32To36 != null || vlBf != null) {
            RegisterVLType regVl = objectFactory.createRegisterVLType();
            regVl.setVLLessThan32Weeks(vlLt32);
            regVl.setVL32To36Weeks(vl32To36);
            regVl.setVLBreastfeedingPeriod(vlBf);
            reg.setViralLoad(regVl);
        }

        // PartnerNotification
        PartnerNotificationType partner = buildPartnerNotification(objectFactory, row);
        if (partner != null) {
            reg.setPartnerNotification(partner);
        }

        return reg;
    }

    private MotherInfantPairVisitType mapMotherInfantPairVisit(ObjectFactory objectFactory, MotherInfantPairVisitProjection row) throws Exception {
        if (StringUtils.isBlank(row.getVisitId()) || row.getVisitDate() == null) {
            return null;
        }

        MotherInfantPairVisitType visit = objectFactory.createMotherInfantPairVisitType();
        visit.setVisitId(row.getVisitId().trim());
        visit.setVisitDate(toXmlDate(row.getVisitDate()));

        if (row.getScheduled() != null) {
            visit.setScheduled(row.getScheduled());
        }
        if (row.getGestationalAgeWeeks() != null && row.getGestationalAgeWeeks() >= 0) {
            visit.setGestationalAgeWeeks(BigInteger.valueOf(row.getGestationalAgeWeeks()));
        }
        if (row.getMotherWeightKg() != null) {
            visit.setMotherWeightKg(row.getMotherWeightKg());
        }
        if (row.getSfhLengthCm() != null) {
            visit.setSFHLenghtCm(row.getSfhLengthCm());
        }

        PregnancyBreastfeedingStatusType currentStatus = mapPregnancyBreastfeedingStatus(row.getMotherCurrentStatus());
        if (currentStatus != null) {
            visit.setMotherCurrentStatus(currentStatus);
        }

        String artStatus = mapMotherArtStatus(row.getMotherCurrentArtStatus());
        if (artStatus != null) {
            visit.setMotherCurrentARTStatus(artStatus);
        }

        MotherCurrentARTRegimenType regimen = buildMotherRegimen(
                objectFactory,
                row.getRegimenCode(),
                row.getRegimenDescription(),
                row.getRegimenNdrCode());
        if (regimen != null) {
            visit.setMotherCurrentARTRegimen(regimen);
        }

        String hbvStatus = mapMotherPairHbvStatus(row.getMotherCurrentHbvStatus());
        if (hbvStatus != null) {
            visit.setMotherCurrentHBVStatus(hbvStatus);
        }
        if (StringUtils.isNotBlank(row.getMotherCurrentHbvDrugName())) {
            visit.setMotherCurrentHBVDrugName(row.getMotherCurrentHbvDrugName().trim());
        }

        String syphilisStatus = mapMotherPairSyphilisStatus(row.getMotherCurrentSyphilisStatus());
        if (syphilisStatus != null) {
            visit.setMotherCurrentSyphilisStatus(syphilisStatus);
        }
        if (StringUtils.isNotBlank(row.getSyphilisDrugAdministered())) {
            visit.setSyphilisDrugAdministered(row.getSyphilisDrugAdministered().trim());
        }

        if (row.getVlDateSampleCollected() != null || row.getVlDateResultReceived() != null || row.getVlResultCopiesPerMl() != null) {
            PairCardVisitVLType vl = objectFactory.createPairCardVisitVLType();
            if (row.getVlDateSampleCollected() != null) {
                vl.setDateSampleCollected(toXmlDate(row.getVlDateSampleCollected()));
            }
            if (row.getVlDateResultReceived() != null) {
                vl.setDateResultReceived(toXmlDate(row.getVlDateResultReceived()));
            }
            if (row.getVlResultCopiesPerMl() != null) {
                vl.setResultCopiesPerML(row.getVlResultCopiesPerMl());
            }
            visit.setViralLoad(vl);
        }

        String feedingPractice = mapInfantFeedingPractice(row.getInfantFeedingPractice());
        if (feedingPractice != null) {
            visit.setInfantFeedingPractice(feedingPractice);
        }

        String infantCtx = mapYN(row.getInfantOnCtx());
        if (infantCtx != null) {
            visit.setInfantOnCTX(infantCtx);
        }

        String referredTo = mapReferredToTreatment(row.getReferredToTreatment());
        if (referredTo != null) {
            visit.setReferredToTreatment(referredTo);
        }

        if (row.getNextAppointmentDate() != null) {
            visit.setNextAppointmentDate(toXmlDate(row.getNextAppointmentDate()));
        }

        return visit;
    }

    private DeliveryChildrenDetailsType mapDeliveryChildrenDetails(ObjectFactory objectFactory, DeliveryChildrenDetailsProjection row) throws Exception {
        if (row.getDateOfDelivery() == null
                && StringUtils.isBlank(row.getDeliveryUuid())
                && StringUtils.isBlank(row.getInfantUuid())) {
            return null;
        }

        DeliveryChildrenDetailsType delivery = objectFactory.createDeliveryChildrenDetailsType();

        if (row.getDateOfDelivery() != null) {
            delivery.setDateOfDelivery(toXmlDate(row.getDateOfDelivery()));
        }

        String mode = mapModeOfDelivery(row.getModeOfDelivery());
        if (mode != null) {
            delivery.setModeOfDelivery(mode);
        }

        BigDecimal birthWeight = parseDecimal(row.getBirthWeightKg());
        if (birthWeight != null) {
            delivery.setBirthWeightKg(birthWeight);
        }

        if (row.getBirthLengthCm() != null) {
            delivery.setBirthLengthCm(row.getBirthLengthCm());
        }

        String birthOutcome = mapBirthOutcome(row.getBirthOutcome());
        if (birthOutcome != null) {
            delivery.setBirthOutcome(birthOutcome);
        }

        if (StringUtils.isNotBlank(row.getChildHospitalNumber())) {
            delivery.setChildHospitalNumber(row.getChildHospitalNumber().trim());
        }

        String entryPoint = mapChildEntryPoint(row.getChildEntryPoint());
        if (entryPoint != null) {
            delivery.setChildEntryPoint(entryPoint);
        }

        String sex = mapSex(row.getSex());
        if (sex != null) {
            delivery.setSex(sex);
        }

        // HIVProphylaxis & CTX from infant_arv_data JSONB or relational columns
        JsonNode arvNode = parseJson(row.getInfantArvDataJson());
        InfantHIVProphylaxisType hivProph = buildInfantHivProphylaxis(objectFactory, arvNode, row);
        if (hivProph != null) {
            delivery.setHIVProphylaxis(hivProph);
        }

        InfantCTXType ctx = buildInfantCtx(objectFactory, arvNode, row);
        if (ctx != null) {
            delivery.setCTX(ctx);
        }

        // SyphilisProphylaxis
        JsonNode syphNode = parseJson(row.getSyphilisProphylaxisJson());
        InfantSyphilisProphylaxisType syphProph = buildInfantSyphilisProphylaxis(objectFactory, syphNode);
        if (syphProph != null) {
            delivery.setSyphilisProphylaxis(syphProph);
        }

        // HBVVaccination
        InfantHBVVaccinationType hbvVac = buildInfantHbvVaccination(
                objectFactory,
                parseJson(row.getHbvVaccinationsJson()),
                parseJson(row.getVisitHbvVaccinationDataJson()));
        if (hbvVac != null) {
            delivery.setHBVVaccination(hbvVac);
        }

        // DNAPCR (up to 3 where testType != confirmatory) & RapidTestAt18Months
        List<JsonNode> pcrNodes = collectPcrNodes(row.getInfantPcrDataJson(), row.getRelPcrListJson());
        for (JsonNode pcrNode : pcrNodes) {
            String testType = getJsonText(pcrNode, "testType");
            boolean isConfirmatory = testType != null && testType.toUpperCase().contains("CONFIRMATORY");
            LocalDate sampleDate = getJsonDate(pcrNode, "dateSampleCollected");
            PCRRapidTestResultType result = mapPcrRapidResult(getJsonText(pcrNode, "results"));
            LocalDate receivedDate = getJsonDate(pcrNode, "dateResultReceivedByCaregiver");
            if (receivedDate == null) {
                receivedDate = getJsonDate(pcrNode, "dateResultReceivedAtFacility");
            }

            if (!isConfirmatory && delivery.getDNAPCR().size() < 3 && (sampleDate != null || result != null || receivedDate != null)) {
                DPCRTestType dpcr = objectFactory.createDPCRTestType();
                if (sampleDate != null) {
                    dpcr.setDateSampleCollected(toXmlDate(sampleDate));
                }
                if (result != null) {
                    dpcr.setResult(result);
                }
                if (receivedDate != null) {
                    dpcr.setDateResultReceived(toXmlDate(receivedDate));
                }
                delivery.getDNAPCR().add(dpcr);
            } else if (isConfirmatory && delivery.getRapidTestAt18Months() == null && (sampleDate != null || result != null)) {
                ConfirmatoryPCRType conf = objectFactory.createConfirmatoryPCRType();
                if (sampleDate != null) {
                    conf.setDateSampleCollected(toXmlDate(sampleDate));
                }
                if (result != null) {
                    conf.setResult(result);
                }
                delivery.setRapidTestAt18Months(conf);
            }
        }

        // Fallback for RapidTestAt18Months from rapid_test_data if not set above
        if (delivery.getRapidTestAt18Months() == null) {
            JsonNode rapidNode = firstNonNullJson(row.getRapidTestDataJson(), row.getRelRapidTestJson());
            if (rapidNode != null) {
                LocalDate testDate = getJsonDate(rapidNode, "dateOfTest");
                PCRRapidTestResultType rapidRes = mapPcrRapidResult(getJsonText(rapidNode, "result"));
                if (testDate != null || rapidRes != null) {
                    ConfirmatoryPCRType rapid18 = objectFactory.createConfirmatoryPCRType();
                    if (testDate != null) {
                        rapid18.setDateSampleCollected(toXmlDate(testDate));
                    }
                    if (rapidRes != null) {
                        rapid18.setResult(rapidRes);
                    }
                    delivery.setRapidTestAt18Months(rapid18);
                }
            }
        }

        InfantOutcomeStatusType outcome = mapInfantOutcomeStatus(row.getOutcomeAt18Months());
        if (outcome != null) {
            delivery.setOutcomeAt18Months(outcome);
        }

        return delivery;
    }

    private InfantCohortRegistrationType mapInfantCohortRegistration(ObjectFactory objectFactory, InfantCohortRegistrationProjection row) throws Exception {
        if (StringUtils.isBlank(row.getInfantUuid())
                && row.getDateOfBirth() == null
                && row.getDateOfFirstVisit() == null
                && StringUtils.isBlank(row.getChildHospitalRegNo())) {
            return null;
        }

        InfantCohortRegistrationType cohort = objectFactory.createInfantCohortRegistrationType();

        if (row.getDateOfBirth() != null) {
            cohort.setDateOfBirth(toXmlDate(row.getDateOfBirth()));
        }
        if (row.getDateOfFirstVisit() != null) {
            cohort.setDateOfFirstVisit(toXmlDate(row.getDateOfFirstVisit()));
        }
        if (row.getAgeAtFirstVisit() != null && row.getAgeAtFirstVisit() >= 0) {
            cohort.setAgeAtFirstVisit(row.getAgeAtFirstVisit());
        }
        if (StringUtils.isNotBlank(row.getChildHospitalRegNo())) {
            cohort.setChildHospitalRegNo(row.getChildHospitalRegNo().trim());
        }

        String infantSex = mapSex(row.getInfantSex());
        if (infantSex != null) {
            cohort.setInfantSex(infantSex);
        }

        String entryPoint = mapChildEntryPoint(row.getChildEntryPoint());
        if (entryPoint != null) {
            cohort.setChildEntryPoint(entryPoint);
        }

        if (StringUtils.isNotBlank(row.getMotherHospitalNumber())) {
            cohort.setMotherHospitalNumber(row.getMotherHospitalNumber().trim());
        }
        if (StringUtils.isNotBlank(row.getMotherAncNumber())) {
            cohort.setMotherANCNumber(row.getMotherAncNumber().trim());
        }

        String motherStatus = mapMotherCohortStatus(row.getMotherStatus());
        if (motherStatus != null) {
            cohort.setMotherStatus(motherStatus);
        }

        String motherArtTiming = mapMotherCohortArtTiming(row.getMotherArtInitiationTiming());
        if (motherArtTiming != null) {
            cohort.setMotherARTInitiationTiming(motherArtTiming);
        }

        MotherCurrentARTRegimenType motherRegimen = buildMotherRegimen(
                objectFactory,
                row.getMotherRegimenCode(),
                row.getMotherRegimenDescription(),
                row.getMotherRegimenNdrCode());
        if (motherRegimen != null) {
            cohort.setMotherHIVARTRegimen(motherRegimen);
        }

        LocalDate syphStartDate = parseFlexibleDate(row.getMotherSyphilisTreatmentStartDate());
        if (syphStartDate != null) {
            cohort.setMotherSyphilisTreatmentStartDate(toXmlDate(syphStartDate));
        }

        LocalDate hbvStartDate = parseFlexibleDate(row.getMotherHepatitisBTreatmentStartDate());
        if (hbvStartDate != null) {
            cohort.setMotherHepatitisBTreatmentStartDate(toXmlDate(hbvStartDate));
        }

        // InfantHBVProphylaxis
        InfantHBVProphylaxisType hbvProph = buildInfantHbvProphylaxis(
                objectFactory,
                row.getHivExposedInfantGivenHbWithin24hrs(),
                row.getNonHbvExposedInfantGivenHbWithin24hrs(),
                parseJson(row.getHbvVaccinationsJson()),
                parseJson(row.getVisitHbvVaccinationDataJson()));
        if (hbvProph != null) {
            cohort.setInfantHBVProphylaxis(hbvProph);
        }

        // InfantARVProphylaxisType, InfantARVProphylaxisTiming, CTXAgeCategory
        JsonNode arvNode = parseJson(row.getInfantArvDataJson());
        String rawArvType = firstNonBlank(getJsonText(arvNode, "infantArvType"), row.getRelArvType());
        InfantARVProphylaxisType arvProphType = mapCohortInfantArvType(rawArvType);
        if (arvProphType != null) {
            cohort.setInfantARVProphylaxisType(arvProphType);
        }

        String rawArvTime = firstNonBlank(getJsonText(arvNode, "infantArvTime"), row.getRelArvTime());
        String rawDeliveryPoint = firstNonBlank(getJsonText(arvNode, "arvDeliveryPoint"), row.getRelArvDeliveryPoint());
        String rawWithin72 = firstNonBlank(getJsonText(arvNode, "timingOfAvrWithin72Hours"), row.getRelTimingWithin72());
        String rawAfter72 = firstNonBlank(getJsonText(arvNode, "timingOfAvrAfter72Hours"), row.getRelTimingAfter72());
        InfantARVProphylaxisTimingType arvTiming = mapCohortInfantArvTiming(rawArvTime, rawDeliveryPoint, rawWithin72, rawAfter72);
        if (arvTiming != null) {
            cohort.setInfantARVProphylaxisTiming(arvTiming);
        }

        String rawCtxAge = firstNonBlank(getJsonText(arvNode, "ageAtCtx"), row.getRelAgeAtCtx());
        CTXAgeCategoryType ctxAgeCategory = mapCtxAgeCategory(rawCtxAge);
        if (ctxAgeCategory != null) {
            cohort.setCTXAgeCategory(ctxAgeCategory);
        }

        // FirstPCR, SecondPCR, ThirdPCR, ConfirmatoryPCR
        List<JsonNode> pcrNodes = collectPcrNodes(row.getInfantPcrDataJson(), row.getRelPcrListJson());
        for (JsonNode pcrNode : pcrNodes) {
            String testType = StringUtils.defaultString(getJsonText(pcrNode, "testType")).toUpperCase();
            LocalDate sampleDate = getJsonDate(pcrNode, "dateSampleCollected");
            PCRRapidTestResultType result = mapPcrRapidResult(getJsonText(pcrNode, "results"));
            LocalDate receivedDate = getJsonDate(pcrNode, "dateResultReceivedByCaregiver");
            if (receivedDate == null) {
                receivedDate = getJsonDate(pcrNode, "dateResultReceivedAtFacility");
            }
            PCRAgeAtTestType ageAtTest = mapPcrAgeAtTest(getJsonText(pcrNode, "ageAtTest"));

            if (sampleDate == null && result == null && receivedDate == null && ageAtTest == null) {
                continue;
            }

            if (testType.contains("CONFIRMATORY")) {
                if (cohort.getConfirmatoryPCR() == null) {
                    cohort.setConfirmatoryPCR(buildConfirmatoryPcr(objectFactory, sampleDate, result));
                }
            } else if (testType.contains("3RD") || testType.contains("THIRD")) {
                if (cohort.getThirdPCR() == null) {
                    cohort.setThirdPCR(buildConfirmatoryPcr(objectFactory, sampleDate, result));
                }
            } else if (testType.contains("2ND") || testType.contains("SECOND")) {
                if (cohort.getSecondPCR() == null) {
                    cohort.setSecondPCR(buildConfirmatoryPcr(objectFactory, sampleDate, result));
                }
            } else {
                if (cohort.getFirstPCR() == null) {
                    PCRTestType firstPcr = objectFactory.createPCRTestType();
                    if (ageAtTest != null) {
                        firstPcr.setAgeAtTest(ageAtTest);
                    }
                    if (sampleDate != null) {
                        firstPcr.setDateSampleCollected(toXmlDate(sampleDate));
                    }
                    if (result != null) {
                        firstPcr.setResult(result);
                    }
                    if (receivedDate != null) {
                        firstPcr.setDateResultReceived(toXmlDate(receivedDate));
                    }
                    cohort.setFirstPCR(firstPcr);
                } else if (cohort.getSecondPCR() == null) {
                    cohort.setSecondPCR(buildConfirmatoryPcr(objectFactory, sampleDate, result));
                } else if (cohort.getThirdPCR() == null) {
                    cohort.setThirdPCR(buildConfirmatoryPcr(objectFactory, sampleDate, result));
                }
            }
        }

        // RapidAntibodyTest
        JsonNode rapidNode = firstNonNullJson(row.getRapidTestDataJson(), row.getRelRapidTestJson());
        if (rapidNode != null) {
            Integer ageMonths = parseFirstInt(getJsonText(rapidNode, "ageAtTest"));
            LocalDate testDate = getJsonDate(rapidNode, "dateOfTest");
            PCRRapidTestResultType rapidRes = mapPcrRapidResult(getJsonText(rapidNode, "result"));
            if (ageMonths != null || testDate != null || rapidRes != null) {
                InfantRapidAntibodyTestType rapid = objectFactory.createInfantRapidAntibodyTestType();
                if (ageMonths != null) {
                    rapid.setAgeAtTestMonths(BigInteger.valueOf(ageMonths));
                }
                if (testDate != null) {
                    rapid.setDateOfTest(toXmlDate(testDate));
                }
                if (rapidRes != null) {
                    rapid.setResult(rapidRes);
                }
                cohort.setRapidAntibodyTest(rapid);
            }
        }

        // OutcomeAt18Months
        InfantOutcomeStatusType outcome = mapInfantOutcomeStatus(row.getOutcomeAt18Months());
        if (outcome != null) {
            cohort.setOutcomeAt18Months(outcome);
        }

        // ARTEnrollment
        if (row.getDateLinkedToArtClinic() != null || StringUtils.isNotBlank(row.getArtEnrollmentNumber())) {
            InfantARTEnrollmentType artEnroll = objectFactory.createInfantARTEnrollmentType();
            if (row.getDateLinkedToArtClinic() != null) {
                artEnroll.setDateLinkedToARTClinic(toXmlDate(row.getDateLinkedToArtClinic()));
            }
            if (StringUtils.isNotBlank(row.getArtEnrollmentNumber())) {
                artEnroll.setARTEnrollmentNumber(row.getArtEnrollmentNumber().trim());
            }
            cohort.setARTEnrollment(artEnroll);
        }

        return cohort;
    }

    // -------------------------------------------------------------------------
    // Helper builders
    // -------------------------------------------------------------------------

    private VLMeasurementType buildVlMeasurement(ObjectFactory objectFactory, LocalDate date, BigDecimal result) throws Exception {
        if (date == null || result == null) {
            return null;
        }
        VLMeasurementType vl = objectFactory.createVLMeasurementType();
        vl.setResultDate(toXmlDate(date));
        vl.setResultCopiesPerML(result);
        return vl;
    }

    private PartnerNotificationType buildPartnerNotification(ObjectFactory objectFactory, PMTCTRegisterProjection row) {
        Boolean agreed = parseBoolean(row.getPartnerAgreedToNotification());
        String testedHiv = mapPosNeg(row.getPartnerTestedHiv());
        String testedSyph = mapPosNeg(row.getPartnerTestedSyphilis());
        String testedHbv = mapPosNeg(row.getPartnerTestedHepatitisB());

        Boolean refArt = null;
        Boolean refFp = null;
        Boolean refPrep = null;
        Boolean refOther = null;
        if (StringUtils.isNotBlank(row.getPartnerReferral())) {
            String refUpper = row.getPartnerReferral().toUpperCase();
            if (refUpper.contains("ART")) {
                refArt = Boolean.TRUE;
            } else if (refUpper.contains("FP") || refUpper.contains("FAMILY")) {
                refFp = Boolean.TRUE;
            } else if (refUpper.contains("PREP")) {
                refPrep = Boolean.TRUE;
            } else if (!refUpper.contains("NONE")) {
                refOther = Boolean.TRUE;
            }
        }

        if (agreed == null && testedHiv == null && testedSyph == null && testedHbv == null
                && refArt == null && refFp == null && refPrep == null && refOther == null) {
            return null;
        }

        PartnerNotificationType pn = objectFactory.createPartnerNotificationType();
        pn.setAgreedToNotification(agreed);
        pn.setPartnerTestedHIV(testedHiv);
        pn.setPartnerTestedSyphilis(testedSyph);
        pn.setPartnerTestedHepatitiB(testedHbv);
        pn.setReferredToART(refArt);
        pn.setReferredToFamilyPlanning(refFp);
        pn.setReferredToPrEP(refPrep);
        pn.setReferredToOther(refOther);
        return pn;
    }

    private MotherCurrentARTRegimenType buildMotherRegimen(
            ObjectFactory objectFactory,
            String regimenCode,
            String regimenDescription,
            String regimenNdrCode) {
        if (StringUtils.isBlank(regimenCode) && StringUtils.isBlank(regimenDescription) && StringUtils.isBlank(regimenNdrCode)) {
            return null;
        }
        MotherCurrentARTRegimenType reg = objectFactory.createMotherCurrentARTRegimenType();
        if (StringUtils.isNotBlank(regimenCode)) {
            reg.setRegimenCode(regimenCode.trim());
        }
        if (StringUtils.isNotBlank(regimenDescription)) {
            reg.setRegimenDescription(regimenDescription.trim());
        }
        if (StringUtils.isNotBlank(regimenNdrCode)) {
            reg.setRegimenNDRCode(regimenNdrCode.trim());
        }
        return reg;
    }

    private InfantHIVProphylaxisType buildInfantHivProphylaxis(
            ObjectFactory objectFactory,
            JsonNode arvNode,
            DeliveryChildrenDetailsProjection row) throws Exception {
        LocalDate dateOfInit = getJsonDate(arvNode, "dateOfArv");
        if (dateOfInit == null) {
            dateOfInit = row.getRelArvDate();
        }

        String rawTime = firstNonBlank(getJsonText(arvNode, "infantArvTime"), row.getRelArvTime());
        Integer ageWks = parseFirstInt(rawTime);
        if (ageWks == null && rawTime != null && rawTime.toLowerCase().contains("within 72")) {
            ageWks = 0;
        }

        String rawType = firstNonBlank(getJsonText(arvNode, "infantArvType"), row.getRelArvType());
        String prophType = mapDeliveryInfantHivProphylaxisType(rawType, row.getChildGivenArvWithin72());
        String otherSpecify = firstNonBlank(getJsonText(arvNode, "otherProphylaxisType"), row.getRelArvOtherSpecify());

        if (dateOfInit == null && ageWks == null && prophType == null && StringUtils.isBlank(otherSpecify)) {
            return null;
        }

        InfantHIVProphylaxisType hivProph = objectFactory.createInfantHIVProphylaxisType();
        if (dateOfInit != null) {
            hivProph.setDateOfInitiation(toXmlDate(dateOfInit));
        }
        if (ageWks != null) {
            hivProph.setAgeAtInitiationWks(ageWks);
        }
        if (prophType != null) {
            hivProph.setTypeOfProphylaxis(prophType);
        }
        if (StringUtils.isNotBlank(otherSpecify)) {
            hivProph.setOtherSpecify(otherSpecify.trim());
        }
        return hivProph;
    }

    private InfantCTXType buildInfantCtx(
            ObjectFactory objectFactory,
            JsonNode arvNode,
            DeliveryChildrenDetailsProjection row) throws Exception {
        LocalDate dateOfCtx = getJsonDate(arvNode, "dateOfCtx");
        if (dateOfCtx == null) {
            dateOfCtx = row.getRelCtxDate();
        }
        Integer ageAtCtx = parseFirstInt(firstNonBlank(getJsonText(arvNode, "ageAtCtx"), row.getRelCtxAge()));

        if (dateOfCtx == null && ageAtCtx == null) {
            return null;
        }

        InfantCTXType ctx = objectFactory.createInfantCTXType();
        if (dateOfCtx != null) {
            ctx.setDateOfInitiation(toXmlDate(dateOfCtx));
        }
        if (ageAtCtx != null) {
            ctx.setAgeAtInitiationWks(BigInteger.valueOf(ageAtCtx));
        }
        return ctx;
    }

    private InfantSyphilisProphylaxisType buildInfantSyphilisProphylaxis(ObjectFactory objectFactory, JsonNode syphNode) throws Exception {
        if (syphNode == null) {
            return null;
        }
        LocalDate dateOfInit = getJsonDate(syphNode, "dateOfInitiation");
        Integer ageWks = parseFirstInt(getJsonText(syphNode, "ageAtInitiation"));
        String type = mapInfantSyphilisProphylaxisType(getJsonText(syphNode, "typeOfProphylaxis"));

        if (dateOfInit == null && ageWks == null && type == null) {
            return null;
        }

        InfantSyphilisProphylaxisType syph = objectFactory.createInfantSyphilisProphylaxisType();
        if (dateOfInit != null) {
            syph.setDateOfInitiation(toXmlDate(dateOfInit));
        }
        if (ageWks != null) {
            syph.setAgeAtInitiationWks(ageWks);
        }
        if (type != null) {
            syph.setTypeOfProphylaxis(type);
        }
        return syph;
    }

    private InfantHBVVaccinationType buildInfantHbvVaccination(
            ObjectFactory objectFactory,
            JsonNode hbvVacArrayNode,
            JsonNode visitHbvNode) throws Exception {
        LocalDate firstDoseDate = null;
        String firstDoseTiming = null;
        LocalDate secondDoseDate = null;
        LocalDate thirdDoseDate = null;

        if (hbvVacArrayNode != null) {
            JsonNode firstItem = hbvVacArrayNode.isArray() && hbvVacArrayNode.size() > 0 ? hbvVacArrayNode.get(0) : hbvVacArrayNode;
            firstDoseDate = getJsonDate(firstItem, "dateOfVaccination");
            if (firstDoseDate == null) {
                firstDoseDate = getJsonDate(firstItem, "firstDoseDate");
            }
            firstDoseTiming = mapFirstDoseTiming(firstNonBlank(getJsonText(firstItem, "ageAtVaccination"), getJsonText(firstItem, "firstDoseTiming")));
            secondDoseDate = getJsonDate(firstItem, "secondDoseDate");
            thirdDoseDate = getJsonDate(firstItem, "thirdDoseDate");
        }

        if (visitHbvNode != null) {
            if (firstDoseDate == null) {
                firstDoseDate = getJsonDate(visitHbvNode, "firstDoseDate");
            }
            if (firstDoseTiming == null) {
                firstDoseTiming = mapFirstDoseTiming(getJsonText(visitHbvNode, "firstDoseTiming"));
            }
            if (secondDoseDate == null) {
                secondDoseDate = getJsonDate(visitHbvNode, "secondDoseDate");
            }
            if (thirdDoseDate == null) {
                thirdDoseDate = getJsonDate(visitHbvNode, "thirdDoseDate");
            }
        }

        if (firstDoseDate == null && firstDoseTiming == null && secondDoseDate == null && thirdDoseDate == null) {
            return null;
        }

        InfantHBVVaccinationType hbv = objectFactory.createInfantHBVVaccinationType();
        if (firstDoseDate != null) {
            hbv.setFirstDoseDate(toXmlDate(firstDoseDate));
        }
        if (firstDoseTiming != null) {
            hbv.setFirstDoseTiming(firstDoseTiming);
        }
        if (secondDoseDate != null) {
            hbv.setSecondDoseDate(toXmlDate(secondDoseDate));
        }
        if (thirdDoseDate != null) {
            hbv.setThirdDoseDate(toXmlDate(thirdDoseDate));
        }
        return hbv;
    }

    private InfantHBVProphylaxisType buildInfantHbvProphylaxis(
            ObjectFactory objectFactory,
            Boolean hivExposedWithin24,
            Boolean nonHbvExposedWithin24,
            JsonNode hbvVacArrayNode,
            JsonNode visitHbvNode) {
        Boolean within24 = null;
        Boolean after24 = null;
        Boolean secondDose = null;
        Boolean thirdDose = null;

        if (Boolean.TRUE.equals(hivExposedWithin24) || Boolean.TRUE.equals(nonHbvExposedWithin24)) {
            within24 = Boolean.TRUE;
        }

        if (hbvVacArrayNode != null) {
            JsonNode firstItem = hbvVacArrayNode.isArray() && hbvVacArrayNode.size() > 0 ? hbvVacArrayNode.get(0) : hbvVacArrayNode;
            String timing = mapFirstDoseTiming(firstNonBlank(getJsonText(firstItem, "ageAtVaccination"), getJsonText(firstItem, "firstDoseTiming")));
            if ("Within24hrs".equals(timing)) {
                within24 = Boolean.TRUE;
            } else if ("After24hrs".equals(timing)) {
                after24 = Boolean.TRUE;
            }
            if (getJsonDate(firstItem, "secondDoseDate") != null) {
                secondDose = Boolean.TRUE;
            }
            if (getJsonDate(firstItem, "thirdDoseDate") != null) {
                thirdDose = Boolean.TRUE;
            }
        }

        if (visitHbvNode != null) {
            if (getJsonDate(visitHbvNode, "secondDoseDate") != null) {
                secondDose = Boolean.TRUE;
            }
            if (getJsonDate(visitHbvNode, "thirdDoseDate") != null) {
                thirdDose = Boolean.TRUE;
            }
        }

        if (within24 == null && after24 == null && secondDose == null && thirdDose == null) {
            return null;
        }

        InfantHBVProphylaxisType proph = objectFactory.createInfantHBVProphylaxisType();
        proph.setBirthDoseWithin24Hrs(within24);
        proph.setBirthDoseAfter24Hrs(after24);
        proph.setSecondDoseGiven(secondDose);
        proph.setThirdDoseGiven(thirdDose);
        return proph;
    }

    private ConfirmatoryPCRType buildConfirmatoryPcr(ObjectFactory objectFactory, LocalDate sampleDate, PCRRapidTestResultType result) throws Exception {
        if (sampleDate == null && result == null) {
            return null;
        }
        ConfirmatoryPCRType conf = objectFactory.createConfirmatoryPCRType();
        if (sampleDate != null) {
            conf.setDateSampleCollected(toXmlDate(sampleDate));
        }
        if (result != null) {
            conf.setResult(result);
        }
        return conf;
    }

    // -------------------------------------------------------------------------
    // Enum / Code Mapping Helpers
    // -------------------------------------------------------------------------

    private PregnancyBreastfeedingStatusType mapPregnancyBreastfeedingStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("BF") || u.contains("BREASTFEED") || u.contains("POST-PARTUM") || u.contains("POSTPARTUM")) {
            return PregnancyBreastfeedingStatusType.BF;
        }
        if (u.equals("P") || u.contains("PREG") || u.contains("ANC") || u.contains("LABOUR")) {
            return PregnancyBreastfeedingStatusType.P;
        }
        return null;
    }

    private KnownHIVPositiveStatusType mapKnownHivPositive(String previouslyKnown, String currentlyOnArt) {
        if (StringUtils.isBlank(previouslyKnown) && StringUtils.isBlank(currentlyOnArt)) {
            return null;
        }
        String ca = StringUtils.defaultString(currentlyOnArt).trim().toUpperCase();
        String pk = StringUtils.defaultString(previouslyKnown).trim().toUpperCase();
        if (ca.equals("ONART") || ca.equals("YES") || ca.equals("TRUE") || pk.contains("ON_ART") || pk.contains("CARE_ART")) {
            return KnownHIVPositiveStatusType.ON_ART;
        }
        if (ca.equals("NOTONART") || ca.equals("NO") || ca.equals("FALSE")) {
            return KnownHIVPositiveStatusType.NOT_ON_ART;
        }
        if (pk.contains("POS") || pk.equals("YES") || pk.equals("TRUE")) {
            return KnownHIVPositiveStatusType.ON_ART;
        }
        return null;
    }

    private String mapHivEarlyAcute(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("NOT") || u.equals("NO") || u.equals("FALSE")) {
            return "NotSuspected";
        }
        if (u.contains("SUSPECT") || u.equals("YES") || u.equals("TRUE")) {
            return "Suspected";
        }
        return null;
    }

    private String mapHivEarlyViralLoad(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("TND") || u.contains("NOT_DETECTED") || u.contains("NOT DETECTED") || u.contains("UNDETECT")) {
            return "TND";
        }
        if (u.equals("TD") || u.contains("DETECTED")) {
            return "TD";
        }
        return null;
    }

    private String mapPosNeg(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("POS") || u.contains("POSITIVE") || u.equals("REACTIVE") || u.equals("R")) {
            return "Pos";
        }
        if (u.equals("NEG") || u.contains("NEGATIVE") || u.contains("NON-REACTIVE") || u.contains("NON REACTIVE") || u.equals("NR")) {
            return "Neg";
        }
        return null;
    }

    private String mapRegisterArtTiming(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("LT36") || u.contains("< 36") || u.contains("<36") || u.contains("LESS_THAN_36") || u.contains("PRIOR")) {
            return "LT36";
        }
        if (u.equals("GTE36") || u.contains(">= 36") || u.contains(">=36") || u.contains("36_WEEKS") || u.contains("GREATER")) {
            return "GTe36";
        }
        if (u.equals("LD") || u.contains("LABOUR") || u.contains("DELIVERY")) {
            if (u.contains("AFTER_DELIVERY") || u.contains("POST-PARTUM") || u.contains("POSTPARTUM")) {
                return "BF";
            }
            return "LD";
        }
        if (u.equals("BF") || u.contains("AFTER_DELIVERY") || u.contains("POST-PARTUM") || u.contains("BREASTFEED")) {
            return "BF";
        }
        return null;
    }

    private String mapRegisterSyphilisReferral(String treated, String referred) {
        String t = StringUtils.defaultString(treated).trim().toUpperCase();
        String r = StringUtils.defaultString(referred).trim().toUpperCase();
        if (t.equals("TREATED") || t.equals("YES") || t.equals("TRUE")) {
            return "Treated";
        }
        if (r.equals("REFERRED") || r.equals("YES") || r.equals("TRUE") || t.equals("REFERRED")) {
            return "Referred";
        }
        if (t.equals("NOTTREATED") || t.equals("NO") || t.equals("FALSE")) {
            return "NotTreated";
        }
        return null;
    }

    private String mapRegisterHbvReferral(String treated, String referred, String status) {
        String t = StringUtils.defaultString(treated).trim().toUpperCase();
        String r = StringUtils.defaultString(referred).trim().toUpperCase();
        String s = StringUtils.defaultString(status).trim().toUpperCase();
        if (t.contains("PRIOR") || s.contains("TREATMENT") || t.equals("YES") || t.equals("TRUE")) {
            return "PriorOnHBVTreatment";
        }
        if (t.contains("PROPHYLAXIS") || s.contains("PROPHYLAXIS") || s.contains("PROHYLAXIS")) {
            return "NewOnProphylaxis";
        }
        if (r.equals("YES") || r.equals("TRUE") || t.contains("REFER")) {
            return "Referred";
        }
        if (t.equals("NO") || t.equals("FALSE") || s.contains("NOT_ON_TREATMENT") || s.contains("NOT ON TREATMENT")) {
            return "NotTreated";
        }
        return null;
    }

    private String mapInitiatedOnProphylaxis(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("PROPHYLAXIS") || u.contains("PROHYLAXIS")) {
            return "Prophylaxis";
        }
        if (u.contains("TREATMENT") && !u.contains("NOT")) {
            return "Treatment";
        }
        if (u.equals("NONE") || u.contains("NOT") || u.contains("NEGATIVE")) {
            return "None";
        }
        return null;
    }

    private String mapRegisterTbStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("NP") || u.contains("NO_SIGN") || u.contains("NO SIGN") || u.contains("NON")) {
            return "NP";
        }
        if (u.equals("P") || u.contains("PRESUMPTIVE") || u.contains("SUSPECT") || u.contains("ON_TB") || u.contains("CONFIRMED")) {
            return "P";
        }
        return null;
    }

    private String mapRegisterTbReferral(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("INTER") || u.equals("1")) {
            return "Inter";
        }
        if (u.equals("INTRA") || u.equals("2")) {
            return "Intra";
        }
        return null;
    }

    private String mapMotherArtStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("NOTONART") || u.contains("NOT ON ART") || u.contains("NOT_ON_ART") || u.equals("NO")) {
            return "NotOnART";
        }
        if (u.equals("ONART") || u.contains("ON ART") || u.contains("ON_ART") || u.equals("YES")) {
            return "OnART";
        }
        return null;
    }

    private String mapMotherPairHbvStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("NEG")) {
            return "Negative";
        }
        if (u.contains("PROPHYLAXIS") || u.contains("PROHYLAXIS")) {
            return "PositiveOnProhylaxis";
        }
        if (u.contains("NOT") && u.contains("TREATMENT")) {
            return "PositveNotOnTreatment";
        }
        if (u.contains("TREATMENT")) {
            return "PositiveOnTreatment";
        }
        if (u.contains("POS")) {
            return "PositveNotOnTreatment";
        }
        return null;
    }

    private String mapMotherPairSyphilisStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("NEG")) {
            return "Negative";
        }
        if (u.contains("NOT") && u.contains("TREATMENT")) {
            return "PositveNotOnTreatment";
        }
        if (u.contains("TREATMENT") || u.contains("TREATED")) {
            return "PositiveOnTreatment";
        }
        if (u.contains("POS")) {
            return "PositveNotOnTreatment";
        }
        return null;
    }

    private String mapInfantFeedingPractice(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("EBF") || u.contains("EXCLUSIVE BREAST")) {
            return "EBF";
        }
        if (u.contains("CIF") || u.contains("ERF") || u.contains("FORMULA") || u.contains("REPLACEMENT")) {
            return "CIF";
        }
        if (u.contains("MF") || u.contains("MIXED")) {
            return "MF";
        }
        return null;
    }

    private String mapYN(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("Y") || u.equals("YES") || u.equals("TRUE") || u.equals("1")) {
            return "Y";
        }
        if (u.equals("N") || u.equals("NO") || u.equals("FALSE") || u.equals("0")) {
            return "N";
        }
        return null;
    }

    private String mapReferredToTreatment(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("HIV")) {
            return "HIVtreatment";
        }
        if (u.contains("SYPHILIS")) {
            return "Syphilistreatment";
        }
        if (u.contains("HBV") || u.contains("HEPATITIS")) {
            return "HBVtreatment";
        }
        return null;
    }

    private String mapModeOfDelivery(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("VAGINAL") || u.equals("SVD")) {
            return "Vaginal";
        }
        if (u.contains("ELECTIVE")) {
            return "ElectiveCS";
        }
        if (u.contains("EMERGENCY") || u.contains("CS") || u.contains("CAESAREAN") || u.contains("CESAREAN")) {
            return "EmergencyCS";
        }
        return "Other";
    }

    private String mapBirthOutcome(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("ALIVE") || u.contains("LIVE")) {
            return "Alive";
        }
        if (u.contains("DEAD") || u.contains("STILL") || u.contains("SB") || u.contains("NND")) {
            return "Dead";
        }
        return null;
    }

    private String mapChildEntryPoint(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("ANC")) {
            return "ANC";
        }
        if (u.equals("LD") || u.contains("LABOUR") || u.contains("DELIVERY")) {
            return "LD";
        }
        if (u.contains("POSTNATAL") || u.contains("POST-NATAL") || u.contains("POST_NATAL") || u.contains("POST-PARTUM")) {
            return "Postnatal";
        }
        if (u.contains("IMMUNIZATION")) {
            return "Immunization";
        }
        if (u.contains("OPD") || u.contains("OUTPATIENT")) {
            return "OPD";
        }
        if (u.contains("INPATIENT") || u.contains("WARD")) {
            return "Inpatient";
        }
        if (u.contains("NUTRITION")) {
            return "Nutrition";
        }
        if (u.contains("FAMILY") || u.equals("FP")) {
            return "FamilyPlanning";
        }
        if (u.contains("TRANSFER")) {
            return "TransferIn";
        }
        return "Other";
    }

    private String mapSex(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("M") || u.startsWith("MALE")) {
            return "M";
        }
        if (u.equals("F") || u.startsWith("FEMALE")) {
            return "F";
        }
        return null;
    }

    private String mapDeliveryInfantHivProphylaxisType(String rawType, String givenWithin72) {
        if (StringUtils.isNotBlank(rawType)) {
            String u = rawType.trim().toUpperCase();
            if (u.contains("AZT") && u.contains("3TC") && u.contains("NVP")) {
                return "AZT+3TC+NVP";
            }
            if ((u.contains("NVP") && u.contains("AZT")) || (u.contains("NVP") && u.contains("ZDV"))) {
                return "NVP+AZT";
            }
            if (u.contains("NVP") || u.contains("NEVIRAPINE")) {
                return "NVP";
            }
            if (u.contains("AZT") || u.contains("ZDV") || u.contains("ZIDOVUDINE")) {
                return "AZT";
            }
            if (u.contains("NONE") || u.equals("NO")) {
                return "None";
            }
            if (u.contains("OTHER")) {
                return "Other";
            }
        }
        if (StringUtils.isNotBlank(givenWithin72)) {
            String g = givenWithin72.trim().toUpperCase();
            if (g.equals("NO") || g.equals("FALSE")) {
                return "None";
            }
            if (g.equals("YES") || g.equals("TRUE")) {
                return "NVP";
            }
        }
        return null;
    }

    private String mapInfantSyphilisProphylaxisType(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("1") && u.contains("BPG")) {
            return "BPG(1dose)";
        }
        if (u.contains("3") && u.contains("BPG")) {
            return "BPG(3doses)";
        }
        if (u.contains("PROCAINE")) {
            return "ProcainePenicillinG";
        }
        if (u.contains("AQUEOUS") || u.contains("CRYSTALLINE")) {
            return "AqueousCrystallinePenicillinG";
        }
        return null;
    }

    private String mapFirstDoseTiming(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("WITHIN") || u.contains("< 24") || u.contains("<24") || u.equals("0")) {
            return "Within24hrs";
        }
        if (u.contains("AFTER") || u.contains("> 24") || u.contains(">24")) {
            return "After24hrs";
        }
        return null;
    }

    private PCRRapidTestResultType mapPcrRapidResult(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("POS")) {
            return PCRRapidTestResultType.POSITIVE;
        }
        if (u.contains("NEG")) {
            return PCRRapidTestResultType.NEGATIVE;
        }
        return null;
    }

    private PCRAgeAtTestType mapPcrAgeAtTest(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("ATBIRTH_LTE72HRS") || u.contains("<_72") || u.contains("< 72") || u.contains("<= 72") || u.equals("0")) {
            return PCRAgeAtTestType.AT_BIRTH_LTE_72_HRS;
        }
        if (u.equals("GT72HRS_LT2MONTHS") || u.contains(">72") || u.contains("<_2_MONTHS") || u.contains("< 2 MONTHS")) {
            return PCRAgeAtTestType.GT_72_HRS_LT_2_MONTHS;
        }
        if (u.equals("TWOTO12MONTHS") || u.contains("2-12") || u.contains("2_12")) {
            return PCRAgeAtTestType.TWO_TO_12_MONTHS;
        }
        return null;
    }

    private InfantOutcomeStatusType mapInfantOutcomeStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("NOT") && u.contains("LINKED")) {
            return InfantOutcomeStatusType.HIV_POSITIVE_NOT_LINKED_TO_ART;
        }
        if (u.contains("LINKED")) {
            return InfantOutcomeStatusType.HIV_POSITIVE_LINKEDTO_ART;
        }
        if (u.contains("NEG")) {
            return InfantOutcomeStatusType.HIV_NEG;
        }
        if (u.contains("BREASTFEED")) {
            return InfantOutcomeStatusType.STILL_BREASTFEEDING;
        }
        if (u.contains("TRANSFER")) {
            return InfantOutcomeStatusType.TRANSFERRED_OUT;
        }
        if (u.contains("LOST") || u.contains("LTFU")) {
            return InfantOutcomeStatusType.LOST_TO_FOLLOW_UP;
        }
        if (u.contains("DEAD") || u.contains("DIED")) {
            return InfantOutcomeStatusType.DEAD;
        }
        return null;
    }

    private String mapMotherCohortStatus(String raw) {
        if (StringUtils.isBlank(raw)) {
            return "HIV+";
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("HIV") && u.contains("SYPHILIS")) {
            return "HIVSyphilis+";
        }
        if (u.contains("HIV") && (u.contains("HBV") || u.contains("HEPATITIS"))) {
            return "HIVHBV+";
        }
        if (u.contains("SYPHILIS") && (u.contains("HBV") || u.contains("HEPATITIS"))) {
            return "SyphilisHBV+";
        }
        if (u.contains("SYPHILIS")) {
            return "Syphilis+";
        }
        if (u.contains("HBV") || u.contains("HEPATITIS")) {
            return "HepatitisB+";
        }
        return "HIV+";
    }

    private String mapMotherCohortArtTiming(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.contains("PRIOR")) {
            return "PriorOnPregnancy";
        }
        if (u.contains("< 36") || u.contains("<36") || u.contains("LESS_THAN_36") || u.contains("LESSTHAN36")) {
            return "InitiatedLessThan36Weeks";
        }
        if (u.contains(">= 36") || u.contains("> 36") || u.contains("36_WEEKS") || u.contains("GREATER")) {
            return "InitiatedGreaterThan36Weeks";
        }
        if (u.contains("AFTER_DELIVERY") || u.contains("AFTERDELIVERY") || u.contains("POST-PARTUM") || u.contains("POSTPARTUM")) {
            return "InitiatedAfterDelivery";
        }
        if (u.contains("LABOUR") || u.contains("DELIVERY")) {
            return "InitiatedAtLabourAndDelivery";
        }
        if (u.contains("NONE") || u.equals("NO")) {
            return "None";
        }
        return null;
    }

    private InfantARVProphylaxisType mapCohortInfantArvType(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if ((u.contains("NVP") && u.contains("ZDV")) || (u.contains("NVP") && u.contains("AZT"))) {
            return InfantARVProphylaxisType.NVP_AND_ZDV;
        }
        if (u.contains("NVP") || u.contains("NEVIRAPINE")) {
            return InfantARVProphylaxisType.NVP;
        }
        if (u.contains("ZDV") || u.contains("AZT") || u.contains("ZIDOVUDINE")) {
            return InfantARVProphylaxisType.ZDV;
        }
        if (u.contains("NONE") || u.equals("NO")) {
            return InfantARVProphylaxisType.NONE;
        }
        if (u.contains("OTHER")) {
            return InfantARVProphylaxisType.OTHER;
        }
        return null;
    }

    private InfantARVProphylaxisTimingType mapCohortInfantArvTiming(
            String rawTime,
            String rawDeliveryPoint,
            String rawWithin72,
            String rawAfter72) {
        String combined = (StringUtils.defaultString(rawTime) + " "
                + StringUtils.defaultString(rawDeliveryPoint) + " "
                + StringUtils.defaultString(rawWithin72) + " "
                + StringUtils.defaultString(rawAfter72)).toUpperCase();

        if (StringUtils.isBlank(combined)) {
            return null;
        }

        boolean isOutside = combined.contains("OUTSIDE");
        boolean isAfter = combined.contains("AFTER 72") || combined.contains("AFTER_72") || StringUtils.isNotBlank(rawAfter72);
        boolean isWithin = combined.contains("WITHIN 72") || combined.contains("WITHIN_72") || StringUtils.isNotBlank(rawWithin72);

        if (isWithin && isOutside) {
            return InfantARVProphylaxisTimingType.DELIVERED_OUTSIDE_FACILITY_WITHIN_72_HRS;
        }
        if (isWithin) {
            return InfantARVProphylaxisTimingType.IN_FACILITY_WITHIN_72_HRS;
        }
        if (isAfter && isOutside) {
            return InfantARVProphylaxisTimingType.DELIVERED_FACILITY_AFTER_72_HRS;
        }
        if (isAfter) {
            return InfantARVProphylaxisTimingType.IN_FACILITY_AFTER_72_HRS;
        }
        return null;
    }

    private CTXAgeCategoryType mapCtxAgeCategory(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("LT2MONTHS") || u.contains("< 2") || u.contains("<2") || u.contains("LESS")) {
            return CTXAgeCategoryType.LT_2_MONTHS;
        }
        if (u.equals("GTE2MONTHS") || u.contains(">= 2") || u.contains(">=2") || u.contains("> 2") || u.contains("GREATER")) {
            return CTXAgeCategoryType.G_TE_2_MONTHS;
        }
        Integer weeks = parseFirstInt(u);
        if (weeks != null) {
            return weeks < 8 ? CTXAgeCategoryType.LT_2_MONTHS : CTXAgeCategoryType.G_TE_2_MONTHS;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // JSON & Date Parsing Utilities
    // -------------------------------------------------------------------------

    private XMLGregorianCalendar toXmlDate(LocalDate localDate) throws Exception {
        if (localDate == null) {
            return null;
        }
        return DateUtil.getXmlDate(Date.valueOf(localDate));
    }

    private JsonNode parseJson(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode firstNonNullJson(String primaryJson, String secondaryJson) {
        JsonNode node = parseJson(primaryJson);
        if (node != null && !node.isNull() && !node.isEmpty()) {
            return node;
        }
        return parseJson(secondaryJson);
    }

    private List<JsonNode> collectPcrNodes(String pcrJson, String relPcrListJson) {
        List<JsonNode> list = new ArrayList<>();
        JsonNode primary = parseJson(pcrJson);
        if (primary != null && !primary.isNull()) {
            if (primary.isArray()) {
                primary.forEach(list::add);
            } else if (primary.isObject()) {
                list.add(primary);
            }
        }
        if (list.isEmpty()) {
            JsonNode rel = parseJson(relPcrListJson);
            if (rel != null && rel.isArray()) {
                rel.forEach(list::add);
            }
        }
        return list;
    }

    private String getJsonText(JsonNode node, String field) {
        if (node == null || !node.has(field)) {
            return null;
        }
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) {
            return null;
        }
        String text = val.asText();
        return StringUtils.isNotBlank(text) ? text.trim() : null;
    }

    private LocalDate getJsonDate(JsonNode node, String field) {
        if (node == null || !node.has(field)) {
            return null;
        }
        JsonNode val = node.get(field);
        if (val == null || val.isNull()) {
            return null;
        }
        if (val.isArray() && val.size() >= 3) {
            try {
                return LocalDate.of(val.get(0).asInt(), val.get(1).asInt(), val.get(2).asInt());
            } catch (Exception e) {
                return null;
            }
        }
        return parseFlexibleDate(val.asText());
    }

    private LocalDate parseFlexibleDate(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String cleaned = raw.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            JsonNode arr = parseJson(cleaned);
            if (arr != null && arr.isArray() && arr.size() >= 3) {
                try {
                    return LocalDate.of(arr.get(0).asInt(), arr.get(1).asInt(), arr.get(2).asInt());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        if (cleaned.length() >= 10) {
            try {
                return LocalDate.parse(cleaned.substring(0, 10));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private BigDecimal parseDecimal(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String cleaned = raw.replaceAll("[^0-9.]", "");
        if (StringUtils.isBlank(cleaned)) {
            return null;
        }
        try {
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseFirstInt(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(raw);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private Boolean parseBoolean(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String u = raw.trim().toUpperCase();
        if (u.equals("TRUE") || u.equals("YES") || u.equals("1") || u.equals("Y")) {
            return Boolean.TRUE;
        }
        if (u.equals("FALSE") || u.equals("NO") || u.equals("0") || u.equals("N")) {
            return Boolean.FALSE;
        }
        return null;
    }

    private String firstNonBlank(String a, String b) {
        return StringUtils.isNotBlank(a) ? a.trim() : (StringUtils.isNotBlank(b) ? b.trim() : null);
    }
}
