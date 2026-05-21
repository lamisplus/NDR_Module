package org.lamisplus.modules.ndr.domain.dto;

import javax.xml.bind.annotation.XmlElement;

public interface PostTestCounsellingTypeDto {
	String getTestedForHIVBeforeWithinThisYear();

	Boolean getAcceptedIndexTesting();

	Boolean getProvidedWithInformationOnFPandDualContraception();

	Boolean getClientOrPartnerUseFPMethodsOtherThanCondoms();

	Boolean getClientOrPartnerUseCondomsAsOneFPMethods();

	Boolean getClientRecievedHIVTestResult();

	Boolean getCorrectCondomUseDemonstrated();

	Boolean getHivSelfTestKitsProvided();

	Integer getHivSelfTestKitsCount();

	Boolean getCondomsProvidedToClient();

	String getCategoryOfClient();

	Boolean getClientReferredToOtherServices();
}
