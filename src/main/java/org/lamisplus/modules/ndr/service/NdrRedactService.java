package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.PatientRedactedDemographicDTO;
import org.lamisplus.modules.ndr.schema.redacted.Container;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class NdrRedactService {
    public static final String BASE_DIR = "runtime/ndr_redact/transfer/";
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

    public Marshaller getMarshaller(JAXBContext jaxbContext) throws JAXBException {
        return jaxbContext.createMarshaller ();
    }

    public String processAndGenerateRedactedFile(
            long facilityId,
            Marshaller jaxbMarshaller,
            Container container,
            PatientRedactedDemographicDTO demographics,
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
    private String generateFileName( PatientRedactedDemographicDTO demographics) {
        return formulateFileName(demographics);
    }

    static String formulateFileName(
            PatientRedactedDemographicDTO demographics) {
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

//    public void creatNDRMessages(Container container, String identifier, Long facilityId){
//        try {
//            NDRMessages ndrMessages = new NDRMessages();
//            String msg = ConvertContainerToString(container);
//            ndrMessages.setDeMessage(msg);
//            ndrMessages.setMessageDate(LocalDate.now());
//            ndrMessages.setIsPushed(Boolean.FALSE);
//            //Optional<User> currentUser = this.userService.getUserWithRoles();
//            //currentUser.ifPresent(user -> ndrMessages.setFacilityId(user.getCurrentOrganisationUnitId()));
//            ndrMessages.setFacilityId(facilityId);
//            ndrMessages.setIdentifier(identifier);
//            ndrMessagesRepository.save(ndrMessages);
//        }catch (Exception e){}
//
//    }
}
