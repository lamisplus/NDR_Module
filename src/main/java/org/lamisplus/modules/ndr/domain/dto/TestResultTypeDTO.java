package org.lamisplus.modules.ndr.domain.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.time.LocalDate;

public interface TestResultTypeDTO {
	//@XmlElement(name = "ScreeningTestResult", required = true)
	 String getScreeningTestResult();
	//@XmlElement(name = "ScreeningTestResultDate", required = true)
	//@XmlSchemaType(name = "date")
	 LocalDate getScreeningTestResultDate();
	//@XmlElement(name = "ConfirmatoryTestResult", required = true)
	 String getConfirmatoryTestResult();
	//@XmlElement(name = "ConfirmatoryTestResultDate", required = true)
	//@XmlSchemaType(name = "date")
	LocalDate getConfirmatoryTestResultDate();
	//@XmlElement(name = "TieBreakerTestResult", required = true)
     String getTieBreakerTestResult();
	//@XmlElement(name = "TieBreakerTestResultDate", required = true)
	//@XmlSchemaType(name = "date")
	 LocalDate getTieBreakerTestResultDate();
	//@XmlElement(name = "FinalTestResult", required = true)
	 String getFinalTestResult();
}
