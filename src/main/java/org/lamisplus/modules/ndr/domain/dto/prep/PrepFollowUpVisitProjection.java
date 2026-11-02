package org.lamisplus.modules.ndr.domain.dto.prep;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PrepFollowUpVisitProjection {
//    List<PrepFollowUpVisitType> getPrepFollowUpVisits();

    // ----- Required (minOccurs=1) -----

    /** Unique identifier for this follow-up visit. */
    String getVisitID();

    /** Date of the visit. */
    LocalDate getVisitDate();

    // ----- Visit details -----

    /**
     * 1=Initiation, 2=SecondInitiation, 3=Refill/Re-Injection,
     * 4=MethodSwitch, 5=Restart, 6=TransferIn, 7=NoPrepProvided,
     * 8=Discontinuation, 9=DiscontinuationFollowUp
     */
    String getVisitType();

    /** Duration on PrEP in months at this visit. */
    Integer getDurationOnPrepMonths();

    /** P = Pregnant, BF = Breastfeeding, NP = Not Pregnant. */
    String getPregnancyStatus();

    /** Weight in kg. */
    BigDecimal getFvWeight();

    /** Blood pressure in mmHg (e.g. 120/80). */
    String getBloodPressure();

    /**
     * HIV Testing Service result code at this visit.
     * 1=HIV Positive, 2=HIV Negative, 3=Suspected Acute HIV, 4=Not Done
     */
    String getHtsResult();

    /**
     * Side effects noted at this visit.
     * 0=No side effects, 1=Nausea/vomiting, 2=Headache, 3=Insomnia/bad dreams,
     * 4=Fatigue/weakness, 5=Abdominal pain, 6=Diarrhea, 7=Fever, 8=Rash,
     * 9=Anemia, 10=Stevens-Johnson Syndrome, 11=Hyperglycemia, 12=Other
     */
    String getNotedSideEffects();

    /**
     * Syndromic STI screening results at this visit.
     * 0=No syndromic STI symptoms/signs, 1=Urethral discharge, 2=Genital ulcer,
     * 3=Vaginal discharge, 4=Lower abdominal pain, 5=Scrotal swelling,
     * 6=Anal warts, 7=Genital warts, 8=Inguinal bubo, 9=Others
     */
    String getSyndromicSTIScreening();

    /**
     * Risk reduction services received at this visit.
     * 1=Risk Reduction strategies discussed,
     * 2=Plan 1 plus correct condom use demonstrated,
     * 3=Plan 1 and 2 plus lubricant provided
     */
    String getRiskReductionServices();

    /**
     * Adherence level at this visit (self-report or provider assessment).
     * G=Good (&gt;=95%), F=Fair (85-94%), P=Poor (&lt;85%)
     */
    String getAdherence();

    /**
     * Reason for poor/fair adherence.
     * 1=Forgot, 2=Fell asleep/slept through dose, 3=Change in routine/away from home,
     * 4=Busy/working/at school, 5=Patient moved, 6=Ran out of medications,
     * 7=Drug stock-out, 8=Not able to pay, 9=Partner influence, 10=Other
     */
    String getReasonForPoorFairAdherence();

    /** PrEP type at this visit. 1=Oral, 2=Injectable, 3=Ring, 4=Others */
    String getPrepType();

    /** PrEP regimen at this visit. 1=TDF/FTC, 2=TDF/3TC, 3=Cabotegravir, 4=Lenacapavir */
    String getPrepRegimen();

    /** Number of months of PrEP provided at this visit. */
    Integer getMonthsOfRefill();

    String getOtherDrugsPrescribed();

    // ----- Lab tests -----

    LocalDate getDateOfUrinalysis();

    /**
     * Urinalysis result.
     * 1=No proteinuria, 2=Proteinuria Present - One +,
     * 3=Proteinuria Present - Two ++, 4=Proteinuria Present - Three +++
     */
    String getUrinalysisResult();

    LocalDate getDateOfHepatitisTest();

    /**
     * Hepatitis test result.
     * 1=Hepatitis B Positive, 2=Hepatitis B Negative,
     * 3=Hepatitis C Positive, 4=Hepatitis C Negative,
     * 5=HIV/HBV Coinfected, 6=HIV/HCV Coinfected
     */
    String getHepatitisTestResult();

    LocalDate getDateOfSyphilisTest();

    /** Syphilis test result. 1=Negative, 2=Positive, 3=Not Done, 4=Others */
    String getSyphilisTestResult();

    LocalDate getDateOfLiverFunctionTest();

    /**
     * Liver function test result.
     * 1=ALT Normal, 2=ALT Deranged, 3=ALP Normal, 4=ALP Deranged,
     * 5=AST Normal, 6=AST Deranged, 7=GGT Normal, 8=GGT Deranged,
     * 9=Bilirubin Normal, 10=Bilirubin Deranged, 11=Albumin Normal,
     * 12=Albumin Deranged, 13=Total protein Normal, 14=Total protein Deranged
     */
    String getLiverFunctionTestResult();

    LocalDate getDateOfOtherTests();

    /**
     * Other test result.
     * 1=HB/PCV, 2=WBC+Diff, 3=ALT, 4=AST, 5=Creatinine, 6=Lipid Profile,
     * 7=HBsAg, 8=Urinalysis, 9=Sputum AFB, 10=Chest Xray, 11=Others
     */
    String getOtherTestsResult();

    // ----- Early HIV detection -----

    /** Yes / No */
    String getSuspectedAcuteInfection();

    /** TargetDetected / TargetNotDetected */
    String getEarlyHivDetectionViralLoadResult();

    LocalDate getNextAppointmentDate();

}


