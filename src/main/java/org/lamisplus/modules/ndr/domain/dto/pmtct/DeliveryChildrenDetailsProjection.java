package org.lamisplus.modules.ndr.domain.dto.pmtct;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DeliveryChildrenDetailsProjection {
    String getDeliveryUuid();
    String getInfantUuid();
    LocalDate getDateOfDelivery();
    String getModeOfDelivery();
    String getBirthWeightKg();
    BigDecimal getBirthLengthCm();
    String getBirthOutcome();
    String getChildHospitalNumber();
    String getChildEntryPoint();
    String getSex();
    String getChildGivenArvWithin72();
    String getBabyInfoJson();
    String getInfantArvDataJson();
    String getSyphilisProphylaxisJson();
    String getHbvVaccinationsJson();
    String getInfantPcrDataJson();
    String getVisitHbvVaccinationDataJson();
    String getRapidTestDataJson();
    String getOutcomeAt18Months();
    LocalDate getRelArvDate();
    String getRelArvType();
    String getRelArvTime();
    String getRelArvOtherSpecify();
    LocalDate getRelCtxDate();
    String getRelCtxAge();
    String getRelPcrListJson();
    String getRelRapidTestJson();
}
