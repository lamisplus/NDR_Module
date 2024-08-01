package org.lamisplus.modules.ndr.service;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.domain.entities.User;
import org.lamisplus.modules.base.domain.repositories.OrganisationUnitRepository;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.hiv.domain.entity.ARTClinical;
import org.lamisplus.modules.hiv.repositories.ARTClinicalRepository;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.domain.entities.NDRMessages;
import org.lamisplus.modules.ndr.domain.entities.NdrMessageLog;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.lamisplus.modules.ndr.mapper.ConditionTypeMapper;
import org.lamisplus.modules.ndr.mapper.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.mapper.NDREligibleClientMapper;
import org.lamisplus.modules.ndr.mapper.PatientDemographicsMapper;
import org.lamisplus.modules.ndr.repositories.NDRCodeSetRepository;
import org.lamisplus.modules.ndr.repositories.NDRMessagesRepository;
import org.lamisplus.modules.ndr.repositories.NdrMessageLogRepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.*;
import org.lamisplus.modules.ndr.utility.ZipUtility;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.stereotype.Service;


import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Slf4j
public class NDRService {
    private final NDRCodeSetRepository nDRCodeSetRepository;
    
    private int MAX = 2;
    private final OrganisationUnitRepository organisationUnitRepository;

    private final MessageHeaderTypeMapper messageHeaderTypeMapper;

    private final PatientDemographicsMapper patientDemographicsMapper;
    

    private final ConditionTypeMapper conditionTypeMapper;

    private final ARTClinicalRepository artClinicalRepository;

    private final OrganisationUnitService organisationUnitService;

    private final NDRCodeSetResolverService ndrCodeSetResolverService;
    

    private final NdrMessageLogRepository ndrMessageLogRepository;

    private final NdrXmlStatusRepository ndrXmlStatusRepository;
    
    private final ArtPharmacyRepository pharmacyRepository;
    
    private final NDREligibleClientMapper clientMap;

    private final PersonRepository personRepository;

    private final NDRMessagesRepository ndrMessagesRepository;
    
    private final BootData bootData;

    private final UserService userService;

    public static final String BASE_DIR = "runtime/ndr/transfer/";
    public static final String BASE_REDACTED_DIR = "runtime/ndr_redact/transfer/";

    public static final String   USER_DIR = "user.dir";

    public static final String JAXB_ENCODING = "UTF-8";
    public static final String XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION = "\n<!-- This XML was generated from LAMISPlus application -->";
    public static final String HEADER_BIND_COMMENT = "com.sun.xml.bind.xmlHeaders";
    public final AtomicLong messageId = new AtomicLong (0);

//    public void shouldPrintMessageHeaderTypeXml(Long id) {
//        try {
//            new MessageHeaderType ();
//            MessageHeaderType messageHeaderType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (MessageHeaderType.class);
//            messageHeaderType = messageHeaderTypeMapper.getMessageHeader (id);
//            messageHeaderType.setMessageUniqueID (String.valueOf (id));
//            messageHeaderType.setMessageStatusCode ("INITIAL");
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "message-header.xml";
//            File file = new File (String.format ("%s/temp/%d/%s", currentPath, id, fileName));
//            jaxbMarshaller.marshal (messageHeaderType, file);
//        } catch (Exception e) {
//            e.printStackTrace ();
//        }
//    }

//    public void shouldPrintPatientDemographicsTypeXml(Long id) {
//        try {
//            new PatientDemographicsType ();
//            PatientDemographicsType patientDemographicsType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (PatientDemographicsType.class);
//            patientDemographicsType = patientDemographicsMapper.getPatientDemographics (id);
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "patient_demographics.xml";
//            File file = new File (String.format ("%s/temp/%d/%s", currentPath, id, fileName));
//            jaxbMarshaller.marshal (patientDemographicsType, file);
//        } catch (Exception e) {
//            e.printStackTrace ();
//        }
//    }

    public Marshaller getMarshaller(JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createMarshaller ();
    }


//    public void shouldPrintPatientAddressTypeXml(Long personId) {
//        try {
//            new AddressType ();
//            AddressType addressType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (AddressType.class);
//            addressType = addressTypeMapper.getPatientAddress (personId);
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "patient_address.xml";
//            File file = new File (String.format ("%s/temp/%d/%s", currentPath, personId, fileName));
//            jaxbMarshaller.marshal (addressType, file);
//        } catch (Exception ignore) {
//            ignore.printStackTrace ();
//        }
//    }

//    public void shouldPrintPatientCommonQuestionsTypeXml(Long personId) {
//        try {
//            new AddressType ();
//            CommonQuestionsType commonQuestionsType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (CommonQuestionsType.class);
//            commonQuestionsType = commonQuestionsTypeMapper.getPatientCommonQuestion (personId);
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "patient_common_question.xml";
//            File file = new File (String.format ("%s/temp/%d/%s", currentPath, personId, fileName));
//            jaxbMarshaller.marshal (commonQuestionsType, file);
//        } catch (Exception ignore) {
//            ignore.printStackTrace ();
//        }
//    }

//    public void shouldPrintPatientConditionSpecificQuestionsTypeXml(Long personId) {
//        try {
//            new AddressType ();
//            ConditionSpecificQuestionsType conditionSpecificQuestionsType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (ConditionSpecificQuestionsType.class);
//            conditionSpecificQuestionsType = specificQuestionsTypeMapper.getConditionSpecificQuestionsType (personId);
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "patient_specific_hiv_questions.xml";
//            File file = new File (String.format ("%s/temp/%d/%s", currentPath, personId, fileName));
//            jaxbMarshaller.marshal (conditionSpecificQuestionsType, file);
//        } catch (Exception ignore) {
//            ignore.printStackTrace ();
//        }
//    }

//    public void shouldPrintPatientConditionEncountersTypeXml(Long personId) {
//        try {
//            new EncountersType ();
//            EncountersType encountersType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (EncountersType.class);
//            encountersType = encountersTypeMapper.encounterType (personId);
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "patient_encounters.xml";
//            File file = new File (String.format ("%s/temp/%d/%s", currentPath, personId, fileName));
//            jaxbMarshaller.marshal (encountersType, file);
//        } catch (Exception ignore) {
//            ignore.printStackTrace ();
//        }
//    }

//    public void shouldPrintPatientConditionTypeXml(Long personId) {
//        try {
//            new ConditionType ();
//            ConditionType conditionType;
//            JAXBContext jaxbContext = JAXBContext.newInstance (ConditionType.class);
//            conditionType = conditionTypeMapper.getConditionType (personId);
//            Marshaller jaxbMarshaller = getMarshaller (jaxbContext);
//            jaxbMarshaller.setProperty (HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            jaxbMarshaller.setProperty (Marshaller.JAXB_ENCODING, JAXB_ENCODING);
//            String currentPath = System.getProperty (USER_DIR);
//            String fileName = "patient.xml";
//            File file = new File (String.format ("%s/temp/%s", currentPath, fileName));
//            jaxbMarshaller.marshal (conditionType, file);
//        } catch (Exception ignore) {
//            ignore.printStackTrace ();
//        }
//    }

    public NDRStatus shouldPrintPatientContainerXml(String personUuid , Long facilityId, boolean isInitial, String pushIdentifier) {
        log.info("generating ndr xml of patient with uuid {}" , personUuid);
        try {
           
            log.info("fetching patient demographics");
            Optional<PatientDemographics> demographicsOptional =
                    ndrXmlStatusRepository.getPatientDemographicsByUUID(personUuid);
            
            
            if (demographicsOptional.isPresent()) {
                log.info("found  patient demographics");
                PatientDemographics demographics = demographicsOptional.get();
                long id = messageId.incrementAndGet();
                Container container = new Container();
                JAXBContext jaxbContext = JAXBContext.newInstance(Container.class);
                //save this somewhere...
                PatientDemographicsType patientDemographics =
                        patientDemographicsMapper.getPatientDemographics(demographics);
                //
                if (patientDemographics != null) {
                    IndividualReportType individualReportType = new IndividualReportType();
                    log.info("fetching treatment details ");
                    ConditionType conditionType = conditionTypeMapper.getConditionType(demographics);
                    individualReportType.setPatientDemographics(patientDemographics);
                    MessageHeaderType messageHeader = messageHeaderTypeMapper.getMessageHeader(demographics);
                    messageHeader.setMessageStatusCode("INITIAL");
                    messageHeader.setMessageUniqueID(Long.toString(id));
                    container.setMessageHeader(messageHeader);
                    container.setIndividualReport(individualReportType);
                    log.info("done fetching treatment details ");
                    Marshaller jaxbMarshaller = getMarshaller(jaxbContext);
                    jaxbMarshaller.setProperty(HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);
                    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR 1.6.2.xsd"));
                    jaxbMarshaller.setSchema(schema);
                    String identifier = patientDemographics.getPatientIdentifier();
                    if (conditionType != null) {
                        individualReportType.getCondition().add(conditionType);
                    }
                    log.info("converting treatment details to xml ");
                    NDRStatus ndrStatus = processAndGenerateNDRFile(jaxbMarshaller, container,demographics, identifier, id);
                   //====================Dr Karim coding session begins
                   // if(ndrStatus != null) creatNDRMessages(container, pushIdentifier);
                    //====================Dr Karim coding session ends
                    log.info("NDR xml for patient with uuid {}  was created successfully", personUuid);
                    return ndrStatus;
                }
            }
            } catch(Exception e){
            log.error("error when generating person with uuid {}", personUuid);
            log.error("error: " + e.getMessage());
            }
        return null;
    }
    
    public NDRStatus shouldPrintPatientContainerXml(String personUuid , Long facilityId, LocalDateTime lastUpdated, String pushIdentifier) {
        log.info("generating ndr xml of patient with uuid {}" , personUuid);
        try {
            log.info("fetching patient demographics....");
            Optional<PatientDemographics> demographicsOptional =
                    ndrXmlStatusRepository.getPatientDemographicsByUUID(personUuid);
            if (demographicsOptional.isPresent()) {
                log.info("found  patient demographics");
                PatientDemographics demographics = demographicsOptional.get();
                long id = messageId.incrementAndGet();
                Container container = new Container();
                JAXBContext jaxbContext = JAXBContext.newInstance(Container.class);
                //caching this because is static
                PatientDemographicsType patientDemographics =
                        patientDemographicsMapper.getPatientDemographics(demographics);
                if (patientDemographics != null) {
                    log.info("fetching treatment details... ");
                    IndividualReportType individualReportType = new IndividualReportType();
                    ConditionType conditionType = conditionTypeMapper.getConditionType(demographics, lastUpdated);
                    individualReportType.setPatientDemographics(patientDemographics);
                    MessageHeaderType messageHeader = messageHeaderTypeMapper.getMessageHeader(demographics);
                    messageHeader.setMessageStatusCode("UPDATED");
                    messageHeader.setMessageUniqueID(Long.toString(id));
                    container.setMessageHeader(messageHeader);
                    container.setIndividualReport(individualReportType);
                    
                    log.info("done fetching treatment details ");
                    Marshaller jaxbMarshaller = getMarshaller(jaxbContext);
                    jaxbMarshaller.setProperty(HEADER_BIND_COMMENT, XML_WAS_GENERATED_FROM_LAMISPLUS_APPLICATION);
                    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, JAXB_ENCODING);
                    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = sf.newSchema(getClass().getClassLoader().getResource("NDR 1.6.2.xsd"));
                    jaxbMarshaller.setSchema(schema);
                    String identifier = patientDemographics.getPatientIdentifier();
                    if (conditionType != null) {
                        individualReportType.getCondition().add(conditionType);
                    }
                    log.info("converting treatment details to xml... ");
                    NDRStatus ndrStatus = processAndGenerateNDRFile(jaxbMarshaller, container,demographics, identifier, id);
                    //====================Dr Karim coding session begins
                    //if(ndrStatus != null) creatNDRMessages(container, pushIdentifier);
                    //====================Dr Karim coding session ends
                    log.info("NDR xml for patient with uuid {}  was created successfully", personUuid);
                    return ndrStatus;
                    //return processAndGenerateNDRFile(jaxbMarshaller, container, demographics, identifier, id);
                }
            }
        } catch(Exception e){
            log.error("error when generating person with uuid {}", personUuid);
            log.error("error: " + e.getMessage());
        }
        return null;
    }

    public boolean generateNDRXMLByFacility(Long facilityId, boolean isInitial) {
        cleanupFacility(facilityId);
       if(isInitial){
           log.info("start generating NDR file from initial");
           System.out.println(facilityId);
           List<String> personUuidsForNDR = nDRCodeSetRepository.getNDREligiblePatientUuidList(facilityId);
           log.info("about {} patients are identified for generating NDR file", personUuidsForNDR.size());
           if(personUuidsForNDR.isEmpty()) return false;
           log.info("generating NDR file ....");
           processAndGenerateNDRFiles(facilityId, personUuidsForNDR);
           log.info("NDR generated successful");
           return true;
       }else {
           log.info("start generating NDR file from updated");
           Optional<Timestamp> lastGenerateDateTimeByFacilityId =
                   ndrXmlStatusRepository.getLastGenerateDateTimeByFacilityId(facilityId,"treatment");
           if(lastGenerateDateTimeByFacilityId.isPresent()){
               LocalDateTime lastModified =
                       lastGenerateDateTimeByFacilityId.get().toLocalDateTime();
               List<String> personUuidsForNDR =
                       nDRCodeSetRepository.getNDREligiblePatientUuidUpdatedListByLastModifyDate(lastModified, facilityId);
               log.info("about {} patients are identified for generating NDR file", personUuidsForNDR.size());
               if(personUuidsForNDR.isEmpty()){
                   return false;
               }
               processAndGenerateNDRFiles(facilityId, personUuidsForNDR, lastModified);
               log.info("NDR generated successful");
               return true;
           }
           return false;
        }
    }
    
   
    
   
    private void processAndGenerateNDRFiles(Long facilityId, List<String> personUuidsForNDR) {
        String pushIdentifier = UUID.randomUUID().toString();
        List<NDRStatus> ndrStatusList =
                personUuidsForNDR.parallelStream ()
                        .map (patientId -> shouldPrintPatientContainerXml (patientId, facilityId, true, pushIdentifier))
                        .collect (Collectors.toList ());
       
        int filesSize = (int) ndrStatusList
                .stream()
                .filter(Objects::nonNull)
                .map(ndrStatus -> new NdrMessageLog(ndrStatus.identifier, ndrStatus.getFile(), LocalDateTime.now()))
                .map(this::saveMessageLog).count();
         processAndZipFacilityNDRXML(facilityId, personUuidsForNDR, filesSize, pushIdentifier);
    }
    
    private void processAndGenerateNDRFiles(Long facilityId, List<String> personUuidsForNDR, LocalDateTime lastUpdated) {
        String pushIdentifier = UUID.randomUUID().toString();
        List<NDRStatus> ndrStatusList =
                personUuidsForNDR.stream ()
                        .map (patientId -> shouldPrintPatientContainerXml (patientId, facilityId, lastUpdated, pushIdentifier))
                        .collect (Collectors.toList ());
        log.info("xml generated about {} files ", ndrStatusList.size());
        Set<NdrMessageLog> messageLogs = ndrStatusList
                .stream()
                .filter(Objects::nonNull)
                .map(ndrStatus -> new NdrMessageLog(ndrStatus.identifier, ndrStatus.getFile(), LocalDateTime.now()))
                .map(this::saveMessageLog)
                .collect(Collectors.toSet());
         log.info("message log saved {} ", messageLogs.size());
        processAndZipFacilityNDRXML(facilityId, personUuidsForNDR, messageLogs.size(), pushIdentifier);
    }
    
    public void processAndZipFacilityNDRXML(Long facilityId, List<String> personUuidsForNDR, int filesSize, String pushIdentifier) {
        Optional<String> personId = personUuidsForNDR.stream().findFirst();
        if(personId.isPresent()) {
            Optional<PatientDemographics> patientDemographicsOptional=
                    ndrXmlStatusRepository.getPatientDemographicsByUUID(personId.get());
            if(patientDemographicsOptional.isPresent()) {
                patientDemographicsOptional.get().getAddress();
                String fileName = zipFileWithType(patientDemographicsOptional.get());
                NdrXmlStatus ndrXmlStatus = new NdrXmlStatus ();
                ndrXmlStatus.setFacilityId (facilityId);
                ndrXmlStatus.setFiles (filesSize);
                ndrXmlStatus.setFileName (fileName);
                ndrXmlStatus.setLastModified (LocalDateTime.now ());
                ndrXmlStatus.setPushIdentifier(pushIdentifier);
                ndrXmlStatus.setCompletelyPushed(Boolean.FALSE);
                ndrXmlStatus.setPercentagePushed(0l);
                ndrXmlStatusRepository.save (ndrXmlStatus);
            }
        }
    }
    
    
    private  NdrMessageLog  saveMessageLog (NdrMessageLog ndrStatus) {
       Optional<NdrMessageLog> list = ndrMessageLogRepository.findFirstByIdentifier(ndrStatus.getIdentifier());
       if (list.isPresent()) {
           NdrMessageLog ndrMessageLog = list.get();
           ndrMessageLog.setLastUpdated(LocalDateTime.now());
           ndrMessageLog.setIdentifier(ndrStatus.getIdentifier());
           //coming back here
     return  ndrMessageLogRepository.save(ndrMessageLog);
       } else {
           return ndrMessageLogRepository.save(ndrStatus);
       }
   }
    public NDRStatus processAndGenerateNDRFile(
            Marshaller jaxbMarshaller,
            Container container,
            PatientDemographics demographics,
            String identifier,
            Long id) {
       try {
           File dir = new File(BASE_DIR + "temp/"+demographics.getFacilityId()+"/");
           if (!dir.exists()) {
               log.info(" directory created => : {}", dir.mkdirs());
           }
           String fileName = generateFileName(demographics, identifier);
           File file = new File(BASE_DIR + "temp/" + demographics.getFacilityId() + "/" + fileName);
           jaxbMarshaller.marshal(container, file);
           return new NDRStatus(id, identifier, fileName);
       }catch (JAXBException e){
           log.error(" error generating file with uuid {}", demographics.getPersonUuid());
           e.printStackTrace();
       }
       return null;
    }
    
    public String processAndGenerateNDRFile(
            long facilityId,
            Marshaller jaxbMarshaller,
            Container container,
            PatientDemographicDTO demographics,
            Long id, List<NDRErrorDTO> ndrErrors) {
        String fileName = null;
        try {
            //creating file
            String path = BASE_DIR + "temp/" + facilityId + "/";
            File dir = new File(path);
            if (!dir.exists()) {
                log.info("directory created => : {}", dir.mkdirs());
            }
            fileName = generateFileName(demographics);
            File file = new File(path + fileName);
            jaxbMarshaller.marshal(container, file);
            log.info("file generated: {}", fileName);
            return fileName;
        }catch (Exception e){
            log.error(" An error occur while generating file with patient hospital number {} Error: {}",
                    demographics.getHospitalNumber(),
                    Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            ndrErrors.add(new NDRErrorDTO(demographics.getPersonUuid(), demographics.getHospitalNumber(), e.getMessage()));
        }
        return null;
    }

    private String generateFileName( PatientDemographics demographics, String identifier) {
        return formulateFileName(demographics, identifier, ndrCodeSetResolverService);
    }
    
    private String generateFileName( PatientDemographicDTO demographics) {
        return formulateFileName(demographics);
    }
    
    
    public String zipFileWithType(PatientDemographics demographics) {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("ddMMyyyy");
        String sCode = "";
        String lCode = "";
        Optional<String> stateCode =
                ndrCodeSetResolverService.getNDRCodeSetCode("STATES", demographics.getState());
        if(stateCode.isPresent()) sCode = stateCode.get();
        Optional<String> lgaCode =
                ndrCodeSetResolverService.getNDRCodeSetCode("LGA", demographics.getLga());
        if(lgaCode.isPresent()) lCode = lgaCode.get();
        String fileName = StringUtils.leftPad (sCode, 2, "0") +"_"+
                StringUtils.leftPad ( lCode, 3, "0") + "_" + demographics.getDatimId() +
                "_" + demographics.getFacilityName()+ "_" + dateFormat.format (new Date());
        
        fileName = RegExUtils.replaceAll (fileName, "/", "-");
        log.info ("file name for download 1 {}", fileName);
        String finalFileName = fileName.replace(" ", "").replace(",", "")
                .replace(".", "");
        log.info ("file name for download {}", finalFileName);
        String outputZipFile = null;
        try {
            String sourceFolder = BASE_DIR + "temp/" + demographics.getFacilityId() + "/";
            outputZipFile = BASE_DIR + "ndr/" + finalFileName;
            new File (BASE_DIR + "ndr").mkdirs ();
            new File (Paths.get (outputZipFile).toAbsolutePath ().toString ()).createNewFile ();
            List<File> files = new ArrayList<> ();
            files = getFiles (sourceFolder, files);
            log.info ("Files: {}", files);
            long fifteenMB = FileUtils.ONE_MB * 15;
            ZipUtility.zip (files, Paths.get (outputZipFile).toAbsolutePath ().toString (), fifteenMB);
            return finalFileName;
        } catch (Exception exception) {
            log.error ("An error occurred while creating temporary file " + outputZipFile);
        }
        return null;
    }
    
    public String zipFileWithType(PatientDemographics demographics, String sourceFolder, String type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("ddMMyyyy");
        String sCode = "";
        String lCode = "";
        Optional<String> stateCode =
                ndrCodeSetResolverService.getNDRCodeSetCode("STATES", demographics.getState());
        if(stateCode.isPresent()) sCode = stateCode.get();
        Optional<String> lgaCode =
                ndrCodeSetResolverService.getNDRCodeSetCode("LGA", demographics.getLga());
        if(lgaCode.isPresent()) lCode = lgaCode.get();
        String fileName = StringUtils.leftPad (sCode, 2, "0") +
                StringUtils.leftPad ( lCode, 3, "0") + "_" + demographics.getDatimId() +
                "_" + demographics.getFacilityName()+"_"+type+ "_" + dateFormat.format (new Date());
        
        fileName = RegExUtils.replaceAll (fileName, "/", "-");
        log.info ("file name for download {}", fileName);
        String finalFileName = fileName.replace(" ", "").replace(",", "")
                .replace(".", "");
        String outputZipFile = null;
        try {
            outputZipFile = BASE_DIR + "ndr/" + finalFileName;
            new File (BASE_DIR + "ndr").mkdirs ();
            new File (Paths.get (outputZipFile).toAbsolutePath ().toString ()).createNewFile ();
            List<File> files = new ArrayList<> ();
            files = getFiles (sourceFolder, files);
            log.info ("Files: {}", files);
            long fifteenMB = FileUtils.ONE_MB * 15;
            ZipUtility.zip (files, Paths.get (outputZipFile).toAbsolutePath ().toString (), fifteenMB);
            return finalFileName;
        } catch (Exception exception) {
            log.error ("An error occurred while creating temporary file " + outputZipFile);
        }
        return null;
    }

    public List<File> getFiles(String sourceFolder, List<File> files) {
        try (Stream<Path> walk = Files.walk (Paths.get (sourceFolder))) {
            files = walk.filter (Files::isRegularFile)
                    .map (Path::toFile)
                    .collect (Collectors.toList ());
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return files;
    }

    @SneakyThrows
    public ByteArrayOutputStream downloadFile(String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        String folder = BASE_DIR + "ndr/";
        Optional<String> fileToDownload = listFilesUsingDirectoryStream (folder).stream ()
                .filter (f -> f.equals (file))
                .findFirst ();
        fileToDownload.ifPresent (s -> {
            try (InputStream is = new FileInputStream (folder + s)) {
                IOUtils.copy (is, baos);
            } catch (IOException ignored) {
            
            }
        });
        return baos;
    }

    @SneakyThrows
    public ByteArrayOutputStream downloadRedactedFile(String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        String folder = BASE_REDACTED_DIR + "ndr/";
        Optional<String> fileToDownload = listFilesUsingDirectoryStream (folder).stream ()
                .filter (f -> f.equals (file))
                .findFirst ();
        fileToDownload.ifPresent (s -> {
            try (InputStream is = new FileInputStream (folder + s)) {
                IOUtils.copy (is, baos);
            } catch (IOException ignored) {

            }
        });
        return baos;
    }


    public void cleanupFacility(Long facilityId) {
        String folder = BASE_DIR + "temp/" + facilityId + "/";
        try {
            if (Files.isDirectory(Paths.get(folder))) {
                FileUtils.deleteDirectory(new File(folder));
            }
        } catch (IOException ignored) {
        }
    }
        public void cleanupFacility(Long facilityId, String folder) {
            try {
                if (Files.isDirectory(Paths.get(folder))) {
                    FileUtils.deleteDirectory(new File(folder));
                }
            } catch (IOException ignored) {
            }
        }
//        String file = BASE_DIR + "ndr/";
//        try (Stream<Path> list = Files.list (Paths.get (BASE_DIR + "ndr/"))) {
//            list.filter (path -> path.getFileName ().toString ().contains (file))
//                    .forEach (path -> {
//                        try {
//                            Files.delete (path);
//                        } catch (IOException e) {
//                            e.printStackTrace ();
//                        }
//                    });
//        } catch (IOException e) {
//        }
    

    public Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        Set<String> fileList = new HashSet<> ();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream (Paths.get (dir))) {
            for (Path path : stream) {
                if (! Files.isDirectory (path)) {
                    fileList.add (path.getFileName ().toString ());
                }
            }
        }
        return fileList;
    }

    @SneakyThrows
    public Set<String> listFiles() {
        String folder = BASE_DIR + "ndr";
        return listFilesUsingDirectoryStream (folder);
    }

//    @SneakyThrows
//    public List<NdrXmlStatusDto> getNdrStatus() {
//        return ndrXmlStatusRepository.findAll ()
//                .stream ()
//                .map (ndrXmlStatus -> NdrXmlStatusDto
//                        .builder ()
//                        .facility (organisationUnitService.getOrganizationUnit (ndrXmlStatus.getFacilityId ()).getName ())
//                        .fileName (ndrXmlStatus.getFileName ())
//                        .files (ndrXmlStatus.getFiles ())
//                        .lastModified (ndrXmlStatus.getLastModified ())
//                        .id (ndrXmlStatus.getId ())
//                        .percentagePushed (ndrXmlStatus.getPercentagePushed())
//                        .completelyPushed (ndrXmlStatus.getCompletelyPushed())
//                        .pushIdentifier (ndrXmlStatus.getPushIdentifier())
//                        .build ()
//                )
//                .sorted(Comparator.comparing(NdrXmlStatusDto::getLastModified).reversed())
//                .collect (Collectors.toList ());
//    }


    public String getLga(OrganisationUnit facility) {
        Long lgaId = facility.getParentOrganisationUnitId ();
        OrganisationUnit lgaSystem = organisationUnitService.getOrganizationUnit (lgaId);
        Optional<CodedSimpleType> lgaNdr = ndrCodeSetResolverService.getNDRCodeSet ("LGA", lgaSystem.getName ());
        log.info ("System LGA {}", lgaSystem.getName ());
        StringBuilder lga = new StringBuilder ();
        lgaNdr.ifPresent(codedSimpleType -> lga.append(codedSimpleType.getCode()));
        return lga.toString ();
    }

    public String getState(OrganisationUnit lgaOrgUnit) {
        Long stateId = lgaOrgUnit.getParentOrganisationUnitId ();
        OrganisationUnit stateSystem = organisationUnitService.getOrganizationUnit (stateId);
        Optional<CodedSimpleType> stateNdr = ndrCodeSetResolverService
                .getNDRCodeSet ("STATES", stateSystem.getName ());
        log.info ("System State {}", stateSystem.getName ());
        StringBuilder state = new StringBuilder ();
        stateNdr.ifPresent(codedSimpleType -> state.append(codedSimpleType.getCode()));
        return state.toString ();
    }
    
    public List<NDREligibleClient> getNDRClientList(Long facilityId, String search) {
        if (search != null) {
            return artClinicalRepository.findAll()
                    .stream()
                    .filter(artClinical -> artClinical.getFacilityId().equals(facilityId))
                    .filter(ARTClinical::getIsCommencement)
                    .map(ARTClinical::getPerson)
                    .map(clientMap)
                    .filter(ndrClient -> ndrClient.getHospitalNumber().contains(search)
                                    || ndrClient.getName().contains(search))
                    .collect(Collectors.toList());
        } else {
            return artClinicalRepository.findAll()
                    .stream()
                    .filter(artClinical -> artClinical.getFacilityId().equals(facilityId))
                    .filter(ARTClinical::getIsCommencement)
                    .map(ARTClinical::getPerson)
                    .map(clientMap)
                    .limit(10)
                    .collect(Collectors.toList());
        }
    }


//  Here my coding begins Dr Karim
private String ConvertContainerToString(Container container) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(df);
        return mapper.writeValueAsString(container);
    }

    public void creatNDRMessages(Container container, String identifier, Long facilityId){
        try {
            NDRMessages ndrMessages = new NDRMessages();
            String msg = ConvertContainerToString(container);
            ndrMessages.setDeMessage(msg);
            ndrMessages.setMessageDate(LocalDate.now());
            ndrMessages.setIsPushed(Boolean.FALSE);
            //Optional<User> currentUser = this.userService.getUserWithRoles();
            //currentUser.ifPresent(user -> ndrMessages.setFacilityId(user.getCurrentOrganisationUnitId()));
            ndrMessages.setFacilityId(facilityId);
            ndrMessages.setIdentifier(identifier);
            ndrMessagesRepository.save(ndrMessages);
        }catch (Exception e){}

    }


    @SneakyThrows
    public List<NdrXmlStatusDto> getNdrStatus() {
        List<NdrXmlStatus> ndrXmlStatusList= ndrXmlStatusRepository.getAllFiles ();
        List<NdrXmlStatusDto> ndrXmlStatusDtos = new ArrayList<>();
        Iterator iterator = ndrXmlStatusList.iterator();
        log.info("SIZE = "+ndrXmlStatusList.size());
        while (iterator.hasNext()){
            NdrXmlStatus ndrXmlStatus = (NdrXmlStatus) iterator.next();
            NdrXmlStatusDto ndrXmlStatusDto = new NdrXmlStatusDto();
            ndrXmlStatusDto.setFacility((organisationUnitService.getOrganizationUnit (ndrXmlStatus.getFacilityId ()).getName ()));
            ndrXmlStatusDto.setFileName(ndrXmlStatus.getFileName());
            ndrXmlStatusDto.setFiles(ndrXmlStatus.getFiles());
            ndrXmlStatusDto.setLastModified(ndrXmlStatus.getLastModified());
            ndrXmlStatusDto.setId(ndrXmlStatus.getId());
            try {
                if(ndrXmlStatus.getError() != null){
                    ndrXmlStatusDto.setError(true);
                }else {
                    ndrXmlStatusDto.setError(false );
                }
                if (null == ndrXmlStatus.getPercentagePushed()) {
                    ndrXmlStatusDto.setPercentagePushed(100l);
                    ndrXmlStatusDto.setCompletelyPushed(Boolean.TRUE);
                    ndrXmlStatusDto.setPushIdentifier("Not Linked");
                } else {
                    ndrXmlStatusDto.setPercentagePushed(ndrXmlStatus.getPercentagePushed());
                    ndrXmlStatusDto.setCompletelyPushed(ndrXmlStatus.getCompletelyPushed());
                    ndrXmlStatusDto.setPushIdentifier(ndrXmlStatus.getPushIdentifier());

                }
            }catch (Exception e){
            }
            ndrXmlStatusDtos.add(ndrXmlStatusDto);



        }
        return ndrXmlStatusDtos;
    }
    
    public List<NDRErrorDTO> getNDRXmlFileErrors(int fileId) throws IOException {
        NdrXmlStatus xmlStatus = ndrXmlStatusRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(NdrXmlStatus.class, "id", fileId + " not found"));
        JsonNode error = xmlStatus.getError();
        if(error != null) {
                ObjectMapper mapper = new ObjectMapper();
            return mapper.readerForListOf(NDRErrorDTO.class)
                .readValue(error);
        }
        return new ArrayList<>();
    }
    
    static String formulateFileName(PatientDemographics demographics, String identifier, NDRCodeSetResolverService ndrCodeSetResolverService) {
        String sCode = "";
        String lCode = "";
        Optional<String> stateCode =
                ndrCodeSetResolverService.getNDRCodeSetCode("STATES", demographics.getState());
        if(stateCode.isPresent()) sCode = stateCode.get();
        Optional<String> lgaCode =
                ndrCodeSetResolverService.getNDRCodeSetCode("LGA", demographics.getLga());
        if(lgaCode.isPresent()) lCode = lgaCode.get();
        Date date = new Date ();
        SimpleDateFormat dateFormat = new SimpleDateFormat ("ddMMyyyy");
        String fileName = StringUtils.leftPad (sCode, 2, "0") +"_"+
                StringUtils.leftPad (lCode, 3, "0") +
                "_" + demographics.getDatimId() + "_" + StringUtils.replace (identifier, "/", "-")
                + "_" +dateFormat.format (date) + ".xml";
        return RegExUtils.replaceAll (fileName, "/", "-");
    }
    
    static String formulateFileName(
            PatientDemographicDTO demographics) {
        String sCode = demographics.getStateCode();
        String lCode = demographics.getLgaCode();
        Date date = new Date ();
        SimpleDateFormat dateFormat = new SimpleDateFormat ("ddMMyyyy");
        String fileName = StringUtils.leftPad (sCode, 2, "0") +"_"+
                StringUtils.leftPad (lCode, 3, "0") +
                "_" + demographics.getFacilityId() + "_" + StringUtils.replace (demographics.getPatientIdentifier(), "/", "-")
                + "_" +dateFormat.format (date) + ".xml";
        return RegExUtils.replaceAll (fileName, "/", "-");
    }

}
