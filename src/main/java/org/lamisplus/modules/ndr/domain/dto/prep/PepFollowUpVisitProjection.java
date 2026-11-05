package org.lamisplus.modules.ndr.domain.dto.prep;

import java.time.LocalDate;
import java.util.Optional;

public interface PepFollowUpVisitProjection {
    String getVisitID();
    LocalDate getVisitDate();

    // ----- Visit details -----

    /**
     * Mode of HIV exposure.
     * 1 = Occupational, 2 = Non-Occupational, 3 = Suspected Acute HIV Infection
     */
    String getModeOfExposure();

    /**
     * Time elapsed between exposure and PEP provision.
     * 1=Less than 24 Hours, 2=Less than 48 Hours,
     * 3=Less than 72 Hours, 4=More than 72 Hours
     */
    String getDurationBeforePepProvided();

    /** Blood pressure in mmHg. */
    String getBloodPressure();

    /**
     * HIV status of source person at time of exposure.
     * 1=Positive, 2=Negative, 3=Suspected Acute HIV Infection
     */
    String getHivStatusAtExposure();

    /**
     * Side effects noted at this PEP follow-up visit.
     * 0=No side effects, 1=Nausea/vomiting, 2=Headache, 3=Insomnia/bad dreams,
     * 4=Fatigue/weakness, 5=Abdominal pain, 6=Diarrhea, 7=Fever, 8=Rash,
     * 9=Anemia, 10=Stevens-Johnson Syndrome, 11=Hyperglycemia, 12=Other
     */
    String getNotedSideEffects();

    /**
     * Syndromic STI screening results at this PEP follow-up visit.
     * 0=No syndromic STI symptoms/signs, 1=Urethral discharge, 2=Genital ulcer,
     * 3=Vaginal discharge, 4=Lower abdominal pain, 5=Scrotal swelling,
     * 6=Anal warts, 7=Genital warts, 8=Inguinal bubo, 9=Others
     */
    String getSyndromicSTIScreening();

    /**
     * Risk reduction services received at this PEP follow-up visit.
     * 1=Risk Reduction strategies discussed,
     * 2=Plan 1 plus correct condom use demonstrated,
     * 3=Plan 1 and 2 plus lubricant provided
     */
    String getRiskReductionServices();

    /**
     * Adherence to PEP regimen at this follow-up visit.
     * G=Good (&gt;=95%), F=Fair (85-94%), P=Poor (&lt;85%)
     */
    String getAdherence();

    /** PEP regimen. 1=TDF+3TC+DTG, 2=Other */
    String getPepRegimen();

    LocalDate getDatePepGivenStart();

    LocalDate getDatePepGivenStop();

    // ----- Follow-up HIV test results -----

    /** 1st follow-up HIV test result at 6 weeks. Pos / Neg */
    String getFollowUpHivTestResult1st6Weeks();

    /** 2nd follow-up HIV test result at 3 months. Pos / Neg */
    String getFollowUpHivTestResult2nd3Months();

    /** 3rd follow-up HIV test result at 6 months. Pos / Neg */
    String getFollowUpHivTestResult3rd6Months();

    /** Yes / No */
    String getReferIfPositive();

    LocalDate getNextAppointmentDate();
}




