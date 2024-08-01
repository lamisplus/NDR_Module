package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.mapper.MortalityTypeMapper;
import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
import org.lamisplus.modules.ndr.repositories.*;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.ZipUtility;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class NdrOptimizationService {

	private final NdrMessageLogRepository data;

	private final NDRService ndrService;

	private final MessageHeaderTypeMapper messageHeaderTypeMapper;
	private final PatientDemographicsMapper patientDemographicsMapper;
	private final ConditionTypeMapper conditionTypeMapper;
	private final MortalityTypeMapper mortalityTypeMapper;

	private final NdrXmlStatusRepository ndrXmlStatusRepository;

	public static final String BASE_DIR = "runtime/ndr/transfer/";

	public static final String USER_DIR = "user.dir";

	public static final String JAXB_ENCODING = "UTF-8";
	public static final String XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION = "\n<!-- This XML was generated from LAMISPlus application -->";
	public static final String HEADER_BIND_COMMENT = "com.sun.xml.bind.xmlHeaders";
	public final AtomicLong messageId = new AtomicLong(0);

	public void generatePatientOneNDRXml(long facilityId, boolean initial, String patientId) {
		final String pathname = BASE_DIR + "temp/" + facilityId + "/";
		log.info("folder -> "+ pathname);
		ndrService.cleanupFacility(facilityId, pathname);
		AtomicInteger generatedCount = new AtomicInteger();

		List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();

		PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

		String pushIdentifier = UUID.randomUUID().toString();

		getPatientNDRXml(patientId, facilityId, initial, ndrErrors, pushIdentifier);
		generatedCount.getAndIncrement();
		patientDemographicDTO[0] = data.getPatientDemographics(patientId, facilityId).get();

		File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
		log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
		if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
			log.info(BASE_DIR + "temp/" + facilityId + "/" + " will be split into two");
		}
		if (generatedCount.get() > 0 && ZipUtility.getFolderSize(folder) > 0) {
			zipAndSaveTheFilesforDownload(
					facilityId,
					pathname,
					generatedCount,
					patientDemographicDTO[0],
					ndrErrors,
					"treatment", pushIdentifier
			);
		}
		log.error("error list size {}", ndrErrors.size());
	}


	public void generatePatientsNDRXml(long facilityId, boolean initial){

		List<String> patientIds = new ArrayList<String>();
		LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
		if (initial) {
			patientIds = data.getPatientIdsEligibleForNDR(start, LocalDateTime.now(), facilityId);
			log.info("generating initial ....");
			generatePatientsNDRXml(facilityId, initial, patientIds,0);
		}else { //updated
			log.info("generating updated....");
			Optional<Timestamp> lastGenerateDateTimeByFacilityId =
					ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId, "treatment");
			if (lastGenerateDateTimeByFacilityId.isPresent()) {
				LocalDateTime lastModified =
						lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
				log.info("Last Generated Date: " + lastModified);
				patientIds = data.getPatientIdsEligibleForNDR(lastModified, LocalDateTime.now(), facilityId);
				List<String> unModifiedPatients = fetchUnModifiedPatients(patientIds, start, LocalDateTime.now(), facilityId);
				//log
				generatePatientsNDRXml_ByLastRecord(facilityId, false, unModifiedPatients);
				generatePatientsNDRXml(facilityId, false, patientIds,unModifiedPatients.size());
			}
			//call a functioin to generate thr last updated records of the other patients
			//List<String> unModifiedPatients = fetchUnModifiedPatients(patientIds, start, LocalDateTime.now(), facilityId);
			//generatePatientsNDRXml_ByLastRecord(facilityId, false, unModifiedPatients);
		}
	}

	private  List<String> fetchUnModifiedPatients(List<String> updatedPatients, LocalDateTime start,  LocalDateTime endDate, Long facilityId) {
		Objects.requireNonNull(data, "Data cannot be null");
		List<String> allEligiblePatients = data.getPatientIdsEligibleForNDR(start, endDate, facilityId);
		Set<String> updatedPatientsSet = new HashSet<>(updatedPatients);
		return allEligiblePatients.stream()
				.filter(p -> !updatedPatientsSet.contains(p))
				.collect(Collectors.toList());
	}

	public void generatePatientsNDRXml(long facilityId, LocalDateTime startDate, LocalDateTime endDate){
		List<String> patientIds  = data.getPatientIdsEligibleForNDR(startDate, endDate, facilityId);
		generatePatientsNDRXmlByDateRange(facilityId,  patientIds, startDate, endDate);
	}

	public void generatePatientsNDRXmlByDateRange(long facilityId, List<String> patientIds, LocalDateTime startDate, LocalDateTime endDate) {
		final String pathname = BASE_DIR + "temp/" + facilityId + "/";
		log.info("folder -> "+ pathname);
		ndrService.cleanupFacility(facilityId, pathname);
		AtomicInteger generatedCount = new AtomicInteger();
		AtomicInteger errorCount = new AtomicInteger();
		List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();

		PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

		log.info("patient size -> "+ patientIds.size());

		String pushIdentifier = UUID.randomUUID().toString();

		patientIds.parallelStream()
				.forEach(id -> {
			if (getPatientNDRXmlByDateRange(id, facilityId, startDate, endDate, ndrErrors, pushIdentifier)) {
				generatedCount.getAndIncrement();
				patientDemographicDTO[0] = data.getPatientDemographics(id, facilityId).get();
			} else {
				errorCount.getAndIncrement();
			}
		});
		log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
		log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
		File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
		log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
		if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
			log.info(BASE_DIR + "temp/" + facilityId + "/" + " will be split into two");
		}

		if (generatedCount.get() > 0 && ZipUtility.getFolderSize(folder) > 0) {
			zipAndSaveTheFilesforDownload(
					facilityId,
					pathname,
					generatedCount,
					patientDemographicDTO[0],
					ndrErrors,
					"treatment", pushIdentifier
					);
		}
		log.error("error list size {}", ndrErrors.size());
	}


	public void generatePatientsNDRXml(long facilityId, boolean initial, List<String> patientIds, long unModfiedCounts) {
		final String pathname = BASE_DIR + "temp/" + facilityId + "/";
		log.info("folder -> "+ pathname);
		ndrService.cleanupFacility(facilityId, pathname);
		AtomicInteger generatedCount = new AtomicInteger();
		generatedCount.set((int) unModfiedCounts);
		AtomicInteger errorCount = new AtomicInteger();
		List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();

		PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

		log.info("patient size -> "+ patientIds.size());

		String pushIdentifier = UUID.randomUUID().toString();

		patientIds.parallelStream()
				.forEach(id -> {
			if (getPatientNDRXml(id, facilityId, initial, ndrErrors, pushIdentifier)) {
				generatedCount.getAndIncrement();
				patientDemographicDTO[0] = data.getPatientDemographics(id, facilityId).get();
			} else {
				errorCount.getAndIncrement();
			}
		});
		log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
		log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
		File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
		log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
		if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
			log.info(BASE_DIR + "temp/" + facilityId + "/" + " will be split into two");
		}

		if (generatedCount.get() > 0 && ZipUtility.getFolderSize(folder) > 0) {
			zipAndSaveTheFilesforDownload(
					facilityId,
					pathname,
					generatedCount,
					patientDemographicDTO[0],
					ndrErrors,
					"treatment", pushIdentifier
					);
		}
		log.error("error list size {}", ndrErrors.size());
	}

	public void generatePatientsNDRXml_ByLastRecord(long facilityId, boolean initial, List<String> patientIds) {
		final String pathname = BASE_DIR + "temp/" + facilityId + "/";
		log.info("folder -> "+ pathname);
		ndrService.cleanupFacility(facilityId, pathname);
		AtomicInteger generatedCount = new AtomicInteger();
		AtomicInteger errorCount = new AtomicInteger();
		List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();

		PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];

		log.info("patient size -> "+ patientIds.size());

		String pushIdentifier = UUID.randomUUID().toString();

		patientIds.parallelStream()
				.forEach(id -> {
					if (getPatientNDRXml_lastRecord(id, facilityId, initial, ndrErrors, pushIdentifier)) {
						generatedCount.getAndIncrement();
						patientDemographicDTO[0] = data.getPatientDemographics(id, facilityId).get();
					} else {
						errorCount.getAndIncrement();
					}
				});
//		log.info("generated  {}/{}", generatedCount.get(), patientIds.size());
//		log.info("files not generated  {}/{}", errorCount.get(), patientIds.size());
//		File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
//		log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
//		if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
//			log.info(BASE_DIR + "temp/" + facilityId + "/" + " will be split into two");
//		}
//
//		if (generatedCount.get() > 0 && ZipUtility.getFolderSize(folder) > 0) {
//			zipAndSaveTheFilesforDownload(
//					facilityId,
//					pathname,
//					generatedCount,
//					patientDemographicDTO[0],
//					ndrErrors,
//					"treatment", pushIdentifier
//			);
//		}
//		log.error("error list size {}", ndrErrors.size());
	}

	public void generateNDRXMLByFacilityAndListOfPatient(Long facilityId, boolean initial, List<String> patientUuidList) {
		final String pathname = BASE_DIR + "temp/" + facilityId + "/";
		log.info("folder -> "+ pathname);
		List<String> idsNotGenerated = new LinkedList<>();
		ndrService.cleanupFacility(facilityId, pathname);
		AtomicInteger generatedCount = new AtomicInteger();
		AtomicInteger errorCount = new AtomicInteger();
		List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();
		PatientDemographicDTO[] patientDemographicDTO = new PatientDemographicDTO[1];
		log.info("patient size -> "+ patientUuidList.size());

		String pushIdentifier = UUID.randomUUID().toString();

		patientUuidList.parallelStream()
				.forEach(id -> {
					if (getPatientNDRXml(id, facilityId, initial, ndrErrors, pushIdentifier)) {
						generatedCount.getAndIncrement();
						patientDemographicDTO[0] = data.getPatientDemographics(id, facilityId).get();
					} else {
						idsNotGenerated.add(id);
						errorCount.getAndIncrement();
					}
				});
		log.info("generated  {}/{}", generatedCount.get(), patientUuidList.size());
		log.info("files not generated  {}/{}", errorCount.get(), patientUuidList.size());
		log.info("patientIds of files not generated: {}", idsNotGenerated);
		File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
		log.info("fileSize {} bytes ", ZipUtility.getFolderSize(folder));
		if (ZipUtility.getFolderSize(folder) >= 15_000_000) {
			log.info(BASE_DIR + "temp/" + facilityId + "/" + " will be split into two");
		}
		if (generatedCount.get() > 0) {
			zipAndSaveTheFilesforDownload(
					facilityId,
					pathname,
					generatedCount,
					patientDemographicDTO[0],
					ndrErrors,"treatment", pushIdentifier
			);
		}
		log.error("error list size {}", ndrErrors.size());
	}


	private boolean getPatientNDRXml(String patientId, long facilityId, boolean initial,
									 List<NDRErrorDTO> ndrErrors,
									 String pushIdentifier) {
		PatientDemographicDTO patientDemographic =
				getPatientDemographic(patientId, facilityId, ndrErrors);
		if (patientDemographic == null)
			return false;
		ObjectMapper objectMapper = new ObjectMapper();
		log.info("starting process patient xml file information");
		log.info("facilityId {}, patientId {}", facilityId, patientId);
		LocalDate start = LocalDate.of(1985, Month.JANUARY, 1);
		LocalDate end = LocalDate.now().plusDays(1);
		log.info("start {}, end {}", start, end);

		//og.info("patient demographic.... {}",patientDemographic);
		log.info("initial patient demographic.... {}{}",initial, patientDemographic);
		Optional<NdrMessageLog> messageLog =
				data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "treatment");

		//check if xml is eligible for update generation
		if (!initial && messageLog.isPresent() ) {
				log.info("updated part 2");
				start = messageLog.get().getLastUpdated().toLocalDate();
				List<EncounterDTO> patientEncounters =
						getPatientEncounters(patientId, facilityId, objectMapper, start, end, ndrErrors);

				if(patientEncounters.isEmpty()){
					patientEncounters = getPatientEncounters_lastRecord(patientId, facilityId, objectMapper, ndrErrors);
				}

				List<RegimenDTO> patientRegimens =
						getPatientRegimens(patientId, facilityId, objectMapper, start, end, ndrErrors);

				if(patientRegimens.isEmpty()){
					patientRegimens =  getPatientLastRegimen(patientId, facilityId, objectMapper, ndrErrors);
				}

				List<LaboratoryEncounterDTO> patientLabEncounters =
						getPatientLabEncounter(patientId, facilityId, objectMapper, start, end, ndrErrors);

				if(patientLabEncounters.isEmpty()){
					System.out.println("getting last lab record");
					patientLabEncounters = getPatientLastLabEncounter(patientId, facilityId, objectMapper, ndrErrors);
				}
				// those that have updated


				MortalityType mortality = mortalityTypeMapper.getMortalityType(patientId, facilityId, start, end, ndrErrors);
				String 	fileName = generatePatientNDRXml(
						facilityId, patientDemographic,
						patientEncounters,
						patientRegimens,
						patientLabEncounters,
						mortality,
						initial,
						ndrErrors, pushIdentifier);

				if (fileName != null) {
					saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName,"treatment");
					return true;
				}

		}else {

			log.info("updated part 4");
			// those that don't have
			List<EncounterDTO> patientEncounters =
					getPatientEncounters(patientId, facilityId, objectMapper, start, end, ndrErrors);

			List<RegimenDTO> patientRegimens =
					getPatientRegimens(patientId, facilityId, objectMapper, start, end, ndrErrors);

			List<LaboratoryEncounterDTO> patientLabEncounters =
					getPatientLabEncounter(patientId, facilityId, objectMapper, start, end, ndrErrors);

			MortalityType mortality = mortalityTypeMapper.getMortalityType(patientId, facilityId, start, end, ndrErrors);
			String fileName = generatePatientNDRXml(
					facilityId, patientDemographic,
					patientEncounters,
					patientRegimens,
					patientLabEncounters,
					mortality,
					true,
					ndrErrors, pushIdentifier);

			if (fileName != null) {
				saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName, "treatment");
				return true;
			}
		}
		return false;
	}
	private boolean getPatientNDRXmlByDateRange(String patientId, long facilityId, LocalDateTime startDate,
												LocalDateTime endDate,
									 List<NDRErrorDTO> ndrErrors,
									 String pushIdentifier) {
		PatientDemographicDTO patientDemographic =
				getPatientDemographic(patientId, facilityId, ndrErrors);
		if (patientDemographic == null)
			return false;

		ObjectMapper objectMapper = new ObjectMapper();
		log.info("starting process patient xml file information");
		log.info("facilityId {}, patientId {}", facilityId, patientId);
		log.info("start {}, end {}", startDate, endDate);

		Optional<NdrMessageLog> messageLog =
				data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "treatment");

		if ( messageLog.isPresent()) {
				log.info("updated part 2");
				List<EncounterDTO> patientEncounters =
						getPatientEncounters(patientId, facilityId, objectMapper, startDate.toLocalDate(), endDate.toLocalDate(), ndrErrors);

				if(patientEncounters.isEmpty()){
					patientEncounters = getPatientEncounters_lastRecord(patientId, facilityId, objectMapper, ndrErrors);
				}

				List<RegimenDTO> patientRegimens =
						getPatientRegimens(patientId, facilityId, objectMapper,startDate.toLocalDate(), endDate.toLocalDate(), ndrErrors);

				if(patientRegimens.isEmpty()){
					patientRegimens =  getPatientLastRegimen(patientId, facilityId, objectMapper, ndrErrors);
				}

				List<LaboratoryEncounterDTO> patientLabEncounters =
						getPatientLabEncounter(patientId, facilityId, objectMapper, startDate.toLocalDate(), endDate.toLocalDate(), ndrErrors);

				if(patientLabEncounters.isEmpty()){
					System.out.println("getting last lab record");
					patientLabEncounters = getPatientLastLabEncounter(patientId, facilityId, objectMapper, ndrErrors);
				}
				// those that have updated

				MortalityType mortality = mortalityTypeMapper.getMortalityType(patientId, facilityId,startDate.toLocalDate(), endDate.toLocalDate(), ndrErrors);
				String 	fileName = generatePatientNDRXml(
						facilityId, patientDemographic,
						patientEncounters,
						patientRegimens,
						patientLabEncounters,
						mortality,
						false,
						ndrErrors, pushIdentifier);

				if (fileName != null) {
					saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName,"treatment");
					return true;
				}
		}else {//generate initial for the patient because there is no previous generation attempt
			LocalDate start = LocalDate.of(1985, Month.JANUARY, 1);
			LocalDate end = LocalDate.now().plusDays(1);

			log.info("updated part 4");
			// those that don't have
			List<EncounterDTO> patientEncounters =
					getPatientEncounters(patientId, facilityId, objectMapper, start, end, ndrErrors);

			List<RegimenDTO> patientRegimens =
					getPatientRegimens(patientId, facilityId, objectMapper, start, end, ndrErrors);

			List<LaboratoryEncounterDTO> patientLabEncounters =
					getPatientLabEncounter(patientId, facilityId, objectMapper, start, end, ndrErrors);

			MortalityType mortality = mortalityTypeMapper.getMortalityType(patientId, facilityId, start, end, ndrErrors);
			String fileName = generatePatientNDRXml(
					facilityId, patientDemographic,
					patientEncounters,
					patientRegimens,
					patientLabEncounters,
					mortality,
					true,
					ndrErrors, pushIdentifier);

			if (fileName != null) {
				saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName, "treatment");
				return true;
			}
		}
		return false;
	}

	private boolean getPatientNDRXml_lastRecord(String patientId, long facilityId, boolean initial,
									 List<NDRErrorDTO> ndrErrors,
									 String pushIdentifier) {
		ObjectMapper objectMapper = new ObjectMapper();
		log.info("starting process patient xml file information");
		log.info("facilityId {}, patientId {}", facilityId, patientId);
		LocalDate start = LocalDate.of(1985, Month.JANUARY, 1);
		LocalDate end = LocalDate.now().plusDays(1);
		log.info("start {}, end {}", start, end);

		PatientDemographicDTO patientDemographic =
				getPatientDemographic(patientId, facilityId, ndrErrors);

		//og.info("patient demographic.... {}",patientDemographic);
		log.info("initial patient demographic.... {}{}",initial, patientDemographic);

		/*if (!initial && patientDemographic != null) {
			Optional<NdrMessageLog> messageLog =
					data.findFirstByIdentifierAndFileType(patientDemographic.getPatientIdentifier(), "treatment");
			if (messageLog.isPresent()) {
				start = messageLog.get().getLastUpdated().toLocalDate();
			}
		}*/

		List<EncounterDTO> patientEncounters = getPatientEncounters_lastRecord(patientId, facilityId, objectMapper, ndrErrors);

		List<RegimenDTO> patientRegimens = getPatientLastRegimen(patientId, facilityId, objectMapper, ndrErrors);

		List<LaboratoryEncounterDTO> patientLabEncounters =	getPatientLastLabEncounter(patientId, facilityId, objectMapper, ndrErrors);

		if (patientDemographic == null)
			return false;

		MortalityType mortality = mortalityTypeMapper.getMortalityType(patientId, facilityId, start, end, ndrErrors);
		String 	fileName = generatePatientNDRXml(
				facilityId, patientDemographic,
				patientEncounters,
				patientRegimens,
				patientLabEncounters,
				mortality,
				initial,
				ndrErrors, pushIdentifier);

		if (fileName != null) {
			saveTheXmlFile(patientDemographic.getPatientIdentifier(), fileName,"treatment");
			return true;
		}

		return false;
	}

	public String generatePatientNDRXml(long facilityId, PatientDemographicDTO patientDemographic,
	                                    List<EncounterDTO> patientEncounters,
	                                    List<RegimenDTO> patientRegimens,
	                                    List<LaboratoryEncounterDTO> patientLabEncounters,
										MortalityType mortality,
	                                    boolean initial, List<NDRErrorDTO> ndrErrors, String pushIdentifier) {
		try {
			//log.info("generating ndr xml of patient with uuid {}", patientDemographic.getPatientIdentifier());
			log.info("fetching patient demographics....");
			long id = messageId.incrementAndGet();
			Container container = new Container();
			JAXBContext jaxbContext = JAXBContext.newInstance(Container.class);
			//caching this because is static
			PatientDemographicsType patientDemographics =
					patientDemographicsMapper.getPatientDemographics(patientDemographic);
			if (patientDemographics != null) {
				log.info("fetching treatment details... ");
				IndividualReportType individualReportType = new IndividualReportType();

				ConditionType conditionType =
						conditionTypeMapper.getConditionType(patientDemographic, patientEncounters, patientRegimens, patientLabEncounters);

				individualReportType.setPatientDemographics(patientDemographics);

				if (conditionType != null) {
					individualReportType.getCondition().add(conditionType);
				}
				//mortality
				if (mortality != null) {
					log.info("mortality generated with visit Id {}", mortality.getVisitID());
					individualReportType.getMortality().add(mortality);
				}


				MessageHeaderType messageHeader = messageHeaderTypeMapper.getMessageHeader(patientDemographic);
				String messageStatusCode = "INITIAL";
				if (!initial) {
					log.info("UPDATED details ");
					Optional<NdrMessageLog> firstByIdentifier =
							data.findFirstByIdentifier(patientDemographic.getPatientIdentifier());
					if (firstByIdentifier.isPresent()) {
						messageStatusCode = "UPDATED";
					}
				}
				messageHeader.setMessageStatusCode(messageStatusCode);
				messageHeader.setMessageUniqueID(Long.toString(id));
				container.setMessageHeader(messageHeader);
				container.setIndividualReport(individualReportType);
				log.info("done fetching treatment details ");
				Marshaller jaxbMarshaller = ndrService.getMarshaller(jaxbContext);
				jaxbMarshaller.setProperty(HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);
				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR1_6_6_1.xsd"));
				jaxbMarshaller.setSchema(schema);
				String identifier = patientDemographics.getPatientIdentifier();

				log.info("converting treatment details to xml... ");
				String fileName = ndrService.processAndGenerateNDRFile(facilityId, jaxbMarshaller, container, patientDemographic, id, ndrErrors);
				if (fileName != null) {
					log.info("NDR XML was successfully generated for patient with hospital number " + patientDemographic.getHospitalNumber());

					//====================Dr Karim coding session begins
					ndrService.creatNDRMessages(container, patientDemographic.getFacilityId().concat("_").concat(pushIdentifier), facilityId);
					//====================Dr Karim coding session ends
				}
				return fileName;
			}
		} catch (Exception e) {
//			log.error("An error occur when generating person with hospital number {}",
//					patientDemographic.getHospitalNumber());
			log.error("error: " + e.toString());
			e.printStackTrace();
			ndrErrors.add(new NDRErrorDTO(patientDemographic.getPersonUuid(),
					patientDemographic.getHospitalNumber(), e.toString()));
		}
		return null;
	}


	private List<RegimenDTO> getPatientRegimens(String patientId, long facilityId,
	                                            ObjectMapper objectMapper, LocalDate start, LocalDate end, List<NDRErrorDTO> ndrErrors) {
		try {
			PatientPharmacyEncounterDTO patientPharmacyEncounterDTO;
			Optional<PatientPharmacyEncounterDTO> patientPharmacyEncounter =
					data.getPatientPharmacyEncounter(patientId, facilityId, start, end);
			if (patientPharmacyEncounter.isPresent()) {
				patientPharmacyEncounterDTO = patientPharmacyEncounter.get();
				return getPatientRegimenList(patientPharmacyEncounterDTO, objectMapper, ndrErrors);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting patient regimen list error {}", e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
		}
		return new ArrayList<>();
	}

	private List<RegimenDTO> getPatientLastRegimen(String patientId, long facilityId, ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors) {
		try {
			PatientPharmacyEncounterDTO patientPharmacyEncounterDTO;
			Optional<PatientPharmacyEncounterDTO> patientPharmacyEncounter = data.getPatientLastPharmacyEncounter(patientId, facilityId);
			if (patientPharmacyEncounter.isPresent()) {
				patientPharmacyEncounterDTO = patientPharmacyEncounter.get();
				return getPatientRegimenList(patientPharmacyEncounterDTO, objectMapper, ndrErrors);
			}
		} catch (Exception e) {
			log.error("An error occurred while getting patient regimen list error {}", e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
		}
		return new ArrayList<>();
	}

	private List<EncounterDTO> getPatientEncounters(
			String patientId,
			long facilityId,
			ObjectMapper objectMapper,
			LocalDate start, LocalDate end,
			List<NDRErrorDTO> ndrErrors) {
		PatientEncounterDTO patientEncounterDTO;
		Optional<PatientEncounterDTO> patientEncounter =
				data.getPatientEncounter(patientId, facilityId, start, end);
		if (patientEncounter.isPresent()) {
			patientEncounterDTO = patientEncounter.get();
			return getPatientEncounterDTOList(patientEncounterDTO, objectMapper, ndrErrors);
		}

		return new ArrayList<>();
	}

	private List<EncounterDTO> getPatientEncounters_lastRecord(String patientId, long facilityId,	ObjectMapper objectMapper,
															   List<NDRErrorDTO> ndrErrors) {
		PatientEncounterDTO patientEncounterDTO;
		Optional<PatientEncounterDTO> patientEncounter = data.getPatientLastEncounter(patientId, facilityId);
		if (patientEncounter.isPresent()) {
			patientEncounterDTO = patientEncounter.get();
			return getPatientEncounterDTOList(patientEncounterDTO, objectMapper, ndrErrors);
		}
		return new ArrayList<>();
	}

	private List<LaboratoryEncounterDTO> getPatientLabEncounter(
			String patientId,
			long facilityId,
			ObjectMapper objectMapper,
			LocalDate start, LocalDate end,
			List<NDRErrorDTO> ndrErrors
	){
		try{
			PatientLabEncounterDTO laboratoryEncounter;

			Optional<PatientLabEncounterDTO> patientLabEncounter =
					data.getPatientLabEncounter(patientId, facilityId, start, end);

			if(patientLabEncounter.isPresent()){
				laboratoryEncounter = patientLabEncounter.get();
				return getPatientLabEncounterDTOList(laboratoryEncounter, objectMapper, ndrErrors);
			}

		}catch (Exception e) {
			log.error("An error occurred while getting patient Lab list error {}", e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
		}
		return new ArrayList<>();
	}

	private List<LaboratoryEncounterDTO> getPatientLastLabEncounter(String patientId, long facilityId, ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors){
		try{
			PatientLabEncounterDTO laboratoryEncounter;
			Optional<PatientLabEncounterDTO> patientLabEncounter = 	data.getPatientLastLabEncounter(patientId, facilityId);
			if(patientLabEncounter.isPresent()){
				System.out.println("Last lab is present");
				laboratoryEncounter = patientLabEncounter.get();
				return getPatientLabEncounterDTOList(laboratoryEncounter, objectMapper, ndrErrors);
			}

		}catch (Exception e) {
			log.error("An error occurred while getting patient Lab list error {}", e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
		}
		return new ArrayList<>();
	}

	private List<LaboratoryEncounterDTO> getPatientLabEncounterDTOList(
			PatientLabEncounterDTO laboratoryEncounter,
			ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors) {
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			return objectMapper.readValue(laboratoryEncounter.getLabs(),
					typeFactory.constructCollectionType(List.class, LaboratoryEncounterDTO.class));
		} catch (Exception e) {
			log.error("Error reading lab encounters of patient with uuid {} errorMsg {}",
					laboratoryEncounter.getPatientUuid(), e.getMessage());
			ndrErrors.add(new NDRErrorDTO(laboratoryEncounter.getPatientUuid(),
					"", e.getMessage()));
		}
		return new ArrayList<>();
	}

	private PatientDemographicDTO getPatientDemographic(String patientId, long facilityId, List<NDRErrorDTO> ndrErrors) {
		try {
			PatientDemographicDTO patientDemographicDTO;
			Optional<PatientDemographicDTO> patientDemographicDTOOptional =
					data.getPatientDemographics(patientId, facilityId);
			if (patientDemographicDTOOptional.isPresent()) {
				log.info("patient demographic information were retrieved successfully");
				patientDemographicDTO = patientDemographicDTOOptional.get();
				return patientDemographicDTO;
			}
		} catch (Exception e) {
			log.error("An error occur while fetching patient with uuid {} information error {}", patientId, e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patientId, "", e.getMessage()));
		}
		return null;
	}

	private List<EncounterDTO> getPatientEncounterDTOList(
			PatientEncounterDTO patientEncounterDTO,
			ObjectMapper objectMapper,
			List<NDRErrorDTO> ndrErrors) {
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			return objectMapper.readValue(patientEncounterDTO.getEncounters(), typeFactory.constructCollectionType(List.class, EncounterDTO.class));
		} catch (Exception e) {
			log.error("Error reading encounters of patient with uuid {} errorMsg {}",
					patientEncounterDTO.getPatientUuid(), e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patientEncounterDTO.getPatientUuid(),
					"", e.getMessage()));

		}
		return new ArrayList<>();
	}

	private List<RegimenDTO> getPatientRegimenList(PatientPharmacyEncounterDTO pharmacyEncounterDTO, ObjectMapper objectMapper, List<NDRErrorDTO> ndrErrors) {
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			return objectMapper.readValue(pharmacyEncounterDTO.getRegimens(), typeFactory.constructCollectionType(List.class, RegimenDTO.class));
		} catch (Exception e) {
			log.error("Error reading regimens of patient with uuid {}  errorMsg {}",
					pharmacyEncounterDTO.getPatientUuid(), e.getMessage());
			ndrErrors.add(new NDRErrorDTO(pharmacyEncounterDTO.getPatientUuid(), "", e.getMessage()));
		}
		return new ArrayList<>();
	}

	public void saveTheXmlFile(String identifier, String fileName, String fileTye) {
		NdrMessageLog ndrMessageLog = new NdrMessageLog();
		ndrMessageLog.setIdentifier(identifier);
		ndrMessageLog.setFile(fileName);
		ndrMessageLog.setLastUpdated(LocalDateTime.now());
		ndrMessageLog.setFileType(fileTye);
		data.save(ndrMessageLog);
	}

	public void zipAndSaveTheFilesforDownload(
			Long facilityId,
			String pathname,
			AtomicInteger count,
			PatientDemographicDTO patient, List<NDRErrorDTO> ndrErrors, String type, String identifier) {
		try {
			zipFiles(patient, facilityId, pathname, ndrErrors,type,identifier);
		} catch (Exception e) {
			log.error("An error occurred while zipping files error {}", e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patient.getPersonUuid(), patient.getHospitalNumber(), e.getMessage()));
		}
	}

	public void storeTheFileInBD(Long facilityId, AtomicInteger count, PatientDemographicDTO patient,
								 List<NDRErrorDTO> ndrErrors, String zipFileName, String type, String identifier) {
		NdrXmlStatus ndrXmlStatus = new NdrXmlStatus();
		if(ndrErrors.size() > 0){
			JsonNode node = getNode(ndrErrors);
			ndrXmlStatus.setError(node);
		}
		ndrXmlStatus.setFacilityId(facilityId);
		ndrXmlStatus.setFiles(count.get());
		ndrXmlStatus.setFileName(zipFileName);
		ndrXmlStatus.setLastModified(LocalDateTime.now());
		ndrXmlStatus.setPushIdentifier(patient.getFacilityId().concat("_").concat(identifier));
		ndrXmlStatus.setCompletelyPushed(Boolean.FALSE);
		ndrXmlStatus.setPercentagePushed(0L);
		ndrXmlStatus.setType(type);
		ndrXmlStatusRepository.save(ndrXmlStatus);
	}


	private JsonNode getNode(List<NDRErrorDTO> values) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return  mapper.valueToTree(values);
		} catch (Exception e) {
			log.error("An error occurred while converting error list to JsonB");
		}
		return null;
	}




	public void zipFiles(PatientDemographicDTO demographic,
	                       long facilityId,
	                       String sourceFolder,
	                       List<NDRErrorDTO> ndrErrors, String type, String identifier) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		String sCode = demographic.getStateCode();
		String lCode = demographic.getLgaCode();
		String fileName = StringUtils.leftPad(sCode, 2, "0") +
				StringUtils.leftPad(lCode, 3, "0") + "_" + demographic.getFacilityId() +
				"_" + demographic.getFacilityName() +"_"+type+ "_" + dateFormat.format(new Date());
		fileName = RegExUtils.replaceAll(fileName, "/", "-");
		log.info("file name for download {}", fileName);
		String finalFileName = fileName.replace(" ", "").replace(",", "")
				.replace(".", "");
		String outputZipFile = null;
		try {
			outputZipFile = BASE_DIR + "ndr/" + finalFileName;
			new File(BASE_DIR + "ndr").mkdirs();
			new File(Paths.get(outputZipFile).toAbsolutePath().toString()).createNewFile();
			List<File> files = new ArrayList<>();
			files = ndrService.getFiles(sourceFolder, files);
			long thirtyMB = (FileUtils.ONE_MB * 15)*2;
			File folder = new File(BASE_DIR + "temp/" + facilityId + "/");
			if (ZipUtility.getFolderSize(folder) > thirtyMB) {
				List<List<File>> splitFiles = split(files, thirtyMB);
				for (int i = 0; i < splitFiles.size(); i++) {
					String splitFileName = finalFileName + "_" + (i + 1);
					String splitOutputZipFile = BASE_DIR + "ndr/" + splitFileName;
					Path path = Paths.get(splitOutputZipFile);
					new File(path.toAbsolutePath().toString()).createNewFile();
					zip(splitFiles.get(i), path.toAbsolutePath().toString());
					storeTheFileInBD(facilityId, new AtomicInteger(splitFiles.get(i).size()), demographic, ndrErrors, splitFileName,type, identifier);
				}
			} else {
				ZipUtility.zip(files, Paths.get(outputZipFile).toAbsolutePath().toString(), thirtyMB);
				storeTheFileInBD(facilityId, new AtomicInteger(files.size()), demographic, ndrErrors, finalFileName,type, identifier);
			}
		} catch (Exception exception) {
			ndrErrors.add(new NDRErrorDTO(demographic.getPersonUuid(), demographic.getHospitalNumber(), exception.getMessage()));
			log.error("An error occurred while creating temporary file " + outputZipFile);
		}
	}

	public static List<List<File>> split(List<File> files, long sizeLimit) {
		List<List<File>> splitFiles = new ArrayList<>();
		List<File> currentSplit = new ArrayList<>();
		long currentSize = 0;
		for (File file : files) {
			long fileSize = file.length();
			if (currentSize + fileSize > sizeLimit) {
				splitFiles.add(currentSplit);
				currentSplit = new ArrayList<>();
				currentSize = 0;
			}
			currentSplit.add(file);
			currentSize += fileSize;
		}
		splitFiles.add(currentSplit);
		return splitFiles;
	}

	public static void zip(List<File> files, String outputZipFile) throws IOException {
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputZipFile))) {
			for (File file : files) {
				try (FileInputStream fileIn = new FileInputStream(file)) {
					ZipEntry zipEntry = new ZipEntry(file.getName());
					zipOut.putNextEntry(zipEntry);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fileIn.read(bytes)) >= 0) {
						zipOut.write(bytes, 0, length);
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}


