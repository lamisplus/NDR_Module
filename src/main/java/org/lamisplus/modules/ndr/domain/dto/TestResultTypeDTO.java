package org.lamisplus.modules.ndr.domain.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.time.LocalDate;

public interface TestResultTypeDTO {

	 String getScreeningTestResult();
	 LocalDate getScreeningTestResultDate();
	 String getSuspectedAcuteHIVInfection();
	 String getConfirmatoryTestResult();
	 LocalDate getConfirmatoryTestResultDate();

	 String getFinalTestResult();
}
