package org.lamisplus.modules.ndr.controller;


import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.domain.dto.NDREligibleClient;
import org.lamisplus.modules.ndr.domain.dto.NDRErrorDTO;
import org.lamisplus.modules.ndr.domain.dto.NdrXmlStatusDto;
import org.lamisplus.modules.ndr.service.HtsService;
import org.lamisplus.modules.ndr.service.NDRService;
import org.lamisplus.modules.ndr.service.NdrOptimizationService;
import org.lamisplus.modules.ndr.service.RedactService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("api/v1/ndr")
@RequiredArgsConstructor
@Slf4j
public class NDRController {
    private final NDRService ndrService;
    private final NdrOptimizationService ndrOptmizationService;

    private final SimpMessageSendingOperations messagingTemplate;
    private final HtsService htsService;
    private final RedactService redactService;
    public static class Constants {
        public static final String FILE_GENERATION_TIME = "Total time taken to generate a file: {}";
    }

    @GetMapping("/generate/{personId}")
    public void generatePatientXml(@PathVariable("personId") String personId, @RequestParam("facility") Long facility) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String pushIdentifier = UUID.randomUUID().toString();
        ndrService.shouldPrintPatientContainerXml (personId, facility, true, pushIdentifier);
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMillis())));
    }

    //testing single file
    @GetMapping("/generate_one/{personId}")
    public void generateOnePatientXml(@PathVariable("personId") String personId, @RequestParam("facility") Long facility) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ndrOptmizationService.generatePatientOneNDRXml(facility, true, personId);
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMillis())));
    }
    @GetMapping("/generate")
    public boolean generateFacilityPatientXmls(@RequestParam List<Long> facilityIds, @RequestParam boolean isInitial ){
        messagingTemplate.convertAndSend("/topic/ndr-status", "start");
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<Boolean> result = new ArrayList<>();
        facilityIds.forEach (
                facilityId -> {
                    boolean result1 = ndrService.generateNDRXMLByFacility(facilityId, isInitial);
                    result.add(result1);
                });
        messagingTemplate.convertAndSend("/topic/ndr-status", "end");
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMillis())));
        return result.contains(true);
    }
    
    @GetMapping("/generate/patients")
    public void generateFacilitySelectedPatientXmls(
            @RequestParam List<Long> facilityIds,
            @RequestParam boolean initial,
            @RequestParam List<String> patientIds){
        messagingTemplate.convertAndSend("/topic/ndr-status", "start");
        Stopwatch stopwatch = Stopwatch.createStarted();
        facilityIds.forEach (facilityId -> ndrOptmizationService.generateNDRXMLByFacilityAndListOfPatient(facilityId,initial,patientIds));
        messagingTemplate.convertAndSend("/topic/ndr-status", "end");
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMillis())));
    }
    
    @GetMapping("/patients")
    public ResponseEntity<List<NDREligibleClient>> getNDREligibleClients(@RequestParam Long facilityId, String search){
        return  ResponseEntity.ok(ndrService.getNDRClientList(facilityId, search));
    }
    
    @GetMapping("/optimization/date-range")
    public ResponseEntity<Void> generateWithDateRange(@RequestParam List<Long> facilityIds, @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        facilityIds.forEach(facilityId -> ndrOptmizationService.generatePatientsNDRXml(facilityId, startDate, endDate));
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMinutes())));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/optimization")
    public ResponseEntity<Void> generateWithOptimization(@RequestParam List<Long> facilityIds, @RequestParam boolean isInitial) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        facilityIds.forEach(facilityId -> ndrOptmizationService.generatePatientsNDRXml(facilityId, isInitial));
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMinutes())));
        return ResponseEntity.ok().build();
    }


    @GetMapping("/generate/hts")
    public ResponseEntity<Void> generateHTSSelectedPatientXmls(
            @RequestParam List<Long> facilityIds,
            @RequestParam boolean isInitial,
            @RequestParam List<String> patientIds){
        Stopwatch stopwatch = Stopwatch.createStarted();
        facilityIds.forEach(facilityId -> htsService.generateSelectedPatientsHtsNDRXml(facilityId, isInitial, patientIds));
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMinutes())));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hts")
    public ResponseEntity<Void> generateHts(@RequestParam List<Long> facilityIds, @RequestParam boolean isInitial) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        facilityIds.forEach(facilityId -> htsService.generatePatientsHtsNDRXml(facilityId, isInitial));
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMinutes())));
        return ResponseEntity.ok().build();
    }

    //testing single HTS file
    @GetMapping("/generate_one_hts")
    public void generateOneHTSPatientXml(@RequestParam("clientCode") String clientCode, @RequestParam("facility") Long facility) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        htsService.generateOnePatientHtsNDRXml(facility, true, clientCode);
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMillis())));
    }
    

    @GetMapping("/download/{file}")
    public void downloadFile(@PathVariable String file, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream baos = ndrService.downloadFile (file);
        response.setHeader ("Content-Type", "application/octet-stream");
        response.setHeader ("Content-Disposition", "attachment;filename=" + file + ".zip");
        response.setHeader ("Content-Length", Integer.toString (baos.size ()));
        OutputStream outputStream = response.getOutputStream ();
        outputStream.write (baos.toByteArray ());
        outputStream.close ();
        response.flushBuffer ();
    }

    @GetMapping("/files")
    public Collection<NdrXmlStatusDto> listFiles() {
        return ndrService.getNdrStatus ();
    }
    
    
    @GetMapping("/file/error/{id}")
    public List<NDRErrorDTO> getErrorsByFileId(@PathVariable("id")  int id) throws IOException {
        return  ndrService.getNDRXmlFileErrors(id);
    }

    @GetMapping("/redacted")
    public ResponseEntity<Void> generateRedactedXML(@RequestParam List<Long> facilityIds, @RequestParam boolean isInitial) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        facilityIds.forEach(facilityId -> redactService.generatePatientsRedactedXml(facilityId, isInitial));
        log.info(Constants.FILE_GENERATION_TIME.replace("{}", String.valueOf(stopwatch.elapsed().toMinutes())));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download_redacted/{file}")
    public void downloadRedactedFile(@PathVariable String file, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream baos = ndrService.downloadRedactedFile (file);
        response.setHeader ("Content-Type", "application/octet-stream");
        response.setHeader ("Content-Disposition", "attachment;filename=" + file + ".zip");
        response.setHeader ("Content-Length", Integer.toString (baos.size ()));
        OutputStream outputStream = response.getOutputStream ();
        outputStream.write (baos.toByteArray ());
        outputStream.close ();
        response.flushBuffer ();
    }

}
