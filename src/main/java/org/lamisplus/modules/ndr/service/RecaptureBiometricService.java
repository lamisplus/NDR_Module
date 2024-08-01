package org.lamisplus.modules.ndr.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographicDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientDemographics;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.lamisplus.modules.ndr.mapper.RecaptureBiometricMapper;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.recapture.Container;
import org.lamisplus.modules.ndr.utility.ZipUtility;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.lamisplus.modules.ndr.service.NDRService.formulateFileName;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecaptureBiometricService {
	
	private final RecaptureBiometricMapper biometricMapper;
	
	private final NdrMessageLogRepository ndrMessageLogRepository;
	
	private final NdrXmlStatusRepository ndrXmlStatusRepository;
	private final NDRCodeSetRepository nDRCodeSetRepository;
	
	private final NDRCodeSetResolverService ndrCodeSetResolverService;
	
	private final NDRService ndrService;

	public static final String BASE_DIR = "runtime/ndr/transfer/";
	
	
	public boolean generateRecaptureBiometrics(Long facilityId) {
		//String pathname = NDRService.BASE_DIR + "temp/biorecapture/" + facilityId + "/";
		final String pathname = BASE_DIR + "temp/biorecapture/" + facilityId + "/";
		ndrService.cleanupFacility(facilityId, pathname);
		AtomicInteger count = new AtomicInteger(0);
		log.info("start generating recapture biometrics patients");
		List<String> patientsIds = nDRCodeSetRepository.getNDREligiblePatientUuidList(facilityId);
		log.info("About {} patients are identified for generating NDR file", patientsIds.size());
		if (patientsIds.isEmpty()) {
			return false;
		}
		log.info("fetching patient demographics");
		List<PatientDemographics> demographics = new ArrayList<>();
		patientsIds.parallelStream()
				.forEach(id -> {
					Optional<PatientDemographics> demographicsOptional =
							ndrXmlStatusRepository.getPatientDemographicsByUUID(id);
					demographicsOptional.ifPresent(d -> {
						try {
							Container container = biometricMapper.getRecaptureBiometricMapper(d);
							if (container != null) {
								JAXBContext jaxbContext = JAXBContext.newInstance(Container.class);
								Marshaller jaxbMarshaller = getMarshaller(jaxbContext);
								SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
								Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR_FP_1.xsd"));
								jaxbMarshaller.setSchema(schema);
								//creating file
								File dir = new File(pathname);
								if (!dir.exists()) {
									log.info("directory created => : {}", dir.mkdirs());
								}
								String identifier = container.getPatientDemographics().getPatientIdentifier();
								String fileName = generateFileName(d, identifier);
								File file = new File(pathname + fileName);
								jaxbMarshaller.marshal(container, file);
								saveTheXmlFile(identifier, fileName);
								count.getAndIncrement();
								demographics.add(d);
							}
							
						} catch (JAXBException | SAXException e) {
							log.error("An error occurred while marshalling the container for patient with id {}  error {}",
									id, e.getMessage());
							//count.decrementAndGet();
						}
					});
				});
		if (!demographics.isEmpty()) {
			//zipAndSaveTheFilesforDownload(facilityId, pathname, count, demographics);
			List<NDRErrorDTO> ndrErrors = new ArrayList<NDRErrorDTO>();
			String pushIdentifier = UUID.randomUUID().toString();

			zipAndSaveTheFilesforDownload(
					facilityId,
					pathname,
					demographics.get(0),
					ndrErrors,
					"bio_recapture", pushIdentifier
			);

		}
		
		return true;
	}
	
	
	private void saveTheXmlFile(String identifier, String fileName) {
		NdrMessageLog ndrMessageLog = new NdrMessageLog();
		ndrMessageLog.setIdentifier(identifier);
		ndrMessageLog.setFile(fileName);
		ndrMessageLog.setLastUpdated(LocalDateTime.now());
		ndrMessageLog.setFileType("recaptured-biometric");
		ndrMessageLogRepository.save(ndrMessageLog);
	}
	
	
//	private void zipAndSaveTheFilesforDownload(
//			Long facilityId,
//			String pathname,
//			AtomicInteger count,
//			List<PatientDemographics> demographics) {
//		try {
//		String zipFileName = ndrService.zipFileWithType(demographics.get(0), pathname, "bio_recapture");
//		NdrXmlStatus ndrXmlStatus = new NdrXmlStatus();
//		ndrXmlStatus.setFacilityId(facilityId);
//		ndrXmlStatus.setFiles(count.get());
//		ndrXmlStatus.setFileName(zipFileName);
//		ndrXmlStatus.setLastModified(LocalDateTime.now());
//		ndrXmlStatus.setPushIdentifier(demographics.get(0).getDatimId().concat("_").concat(demographics.get(0).getPersonUuid()));
//		ndrXmlStatus.setCompletelyPushed(Boolean.FALSE);
//		ndrXmlStatus.setPercentagePushed(0L);
//		ndrXmlStatusRepository.save(ndrXmlStatus);
//		}catch (Exception e){
//			log.error("An error occurred while zipping files error {}", e.getMessage());
//		}
//	}
	
	private Marshaller getMarshaller(JAXBContext jaxbContext) throws JAXBException {
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		//marshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);
		return marshaller;
	}
	
	
	private String generateFileName(PatientDemographics demographics, String identifier) {
		return formulateFileName(demographics, identifier + "_bio_recapture", ndrCodeSetResolverService);
	}

	public void zipAndSaveTheFilesforDownload(
			Long facilityId,
			String pathname,
			PatientDemographics patient, List<NDRErrorDTO> ndrErrors, String type, String identifier) {
		try {
			zipFiles(patient, facilityId, pathname, ndrErrors,type,identifier);
		} catch (Exception e) {
			log.error("An error occurred while zipping files error {}", e.getMessage());
			ndrErrors.add(new NDRErrorDTO(patient.getPersonUuid(), patient.getHospitalNumber(), e.getMessage()));
		}
	}

	public void zipFiles(PatientDemographics demographic,
						 long facilityId,
						 String sourceFolder,
						 List<NDRErrorDTO> ndrErrors, String type, String identifier) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		String sCode = "";
		String lCode = "";
		Optional<String> stateCode =
				ndrCodeSetResolverService.getNDRCodeSetCode("STATES", demographic.getState());
		if(stateCode.isPresent()) sCode = stateCode.get();
		Optional<String> lgaCode =
				ndrCodeSetResolverService.getNDRCodeSetCode("LGA", demographic.getLga());
		if(lgaCode.isPresent()) lCode = lgaCode.get();
//		String sCode = demographic.getStateCode();
//		String lCode = demographic.getLgaCode();
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
			long fiftyMB = (FileUtils.ONE_MB * 25)*2;
			File folder = new File(BASE_DIR + "temp/biorecapture/" + facilityId + "/");
			if (ZipUtility.getFolderSize(folder) > fiftyMB) {
				List<List<File>> splitFiles = split(files, fiftyMB);
				for (int i = 0; i < splitFiles.size(); i++) {
					String splitFileName = finalFileName + "_" + (i + 1);
					String splitOutputZipFile = BASE_DIR + "ndr/" + splitFileName;
					Path path = Paths.get(splitOutputZipFile);
					new File(path.toAbsolutePath().toString()).createNewFile();
					zip(splitFiles.get(i), path.toAbsolutePath().toString());
					storeTheFileInBD(facilityId, new AtomicInteger(splitFiles.get(i).size()), demographic, ndrErrors, splitFileName,type, identifier);
				}
			} else {
				ZipUtility.zip(files, Paths.get(outputZipFile).toAbsolutePath().toString(), fiftyMB);
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

	public void storeTheFileInBD(Long facilityId, AtomicInteger count, PatientDemographics patient,
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
		ndrXmlStatus.setPushIdentifier(patient.getDatimId().concat("_").concat(patient.getPersonUuid()));
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
}
