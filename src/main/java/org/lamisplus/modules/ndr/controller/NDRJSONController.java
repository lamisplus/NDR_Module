package org.lamisplus.modules.ndr.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.audit4j.core.util.Log;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.domain.entities.NDRPusherConfig;
import org.lamisplus.modules.ndr.schema.Container;
import org.lamisplus.modules.ndr.schema.Container2;
import org.lamisplus.modules.ndr.service.NDRJSONService;
import org.lamisplus.modules.ndr.service.NDRService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/ndr-emr")
@RequiredArgsConstructor
public class NDRJSONController {
    private final NDRJSONService NDRJSONService;
    private final NDRService ndrService;

    @GetMapping("/server-status")
    public String pingDNRServer() {
        return NDRJSONService.PingNDRServer();
    }

    @PostMapping("/jwt")
    public NDRAuthResponseDTO getJWT(@RequestParam("email") String email, @RequestParam("password") String password) {
        return NDRJSONService.AuthenticateUser(email, password);
    }

//    @PostMapping("/ndr-data")
//    public NDRDataResponseDTO pushData(@RequestParam("personUuid") String personUuid) throws Exception {
//        List<String> data = new ArrayList<>();
//
//        //TODO: Process patient records in batches
//        //pull the patients from database and process them in batches
//        Container2 container = ndrService.PatientRecordJson(personUuid);
//        String patientData = ConvertContainerToString(container);
//        data.add(patientData);
//        //
//
//        Log.info(data);
//        return NDRJSONService.PushPatientData(data);
//    }

    @GetMapping("/error-logs")
    public NDRLogsResponseDTO getErrorLogs(@RequestParam("batchId") String batchId){
        return NDRJSONService.GetErrorLogs(batchId);
    }

//    @GetMapping("/ndr-json")
//    public List<String> GetNDRDataJson(@RequestParam("personUuid") String personUuid) throws JsonProcessingException {
//        Container container = ndrService.PatientRecordJson(personUuid);
//        return Collections.singletonList(ConvertContainerToString(container));
//    }



//    @PostMapping("/ndr-auto-pusher")
//    public void pushData(
//    @RequestParam Long facilityIds,
//    @RequestParam boolean isInitial) throws Exception {
//                //pull the patients from database and process them in batches
//        List<Container2> container = ndrService.generateNDRReportAndPushByFacility(facilityIds, isInitial);
//
//        NDRJSONService.PushPatientData(container);
//    }

    @PostMapping("/auto-push-configuration")
    public NDRPusherConfig autoPushConfig(@RequestBody NDRAuthRequestDTO ndrAuthRequestDTO) throws Exception {
        NDRPusherConfig ndrPusherConfig = NDRJSONService.save(
                ndrAuthRequestDTO.getEmail(),
                ndrAuthRequestDTO.getPassword(),
                ndrAuthRequestDTO.getBaseUrl()
        );
        return ndrPusherConfig;
    }

    @PutMapping("/auto-push-configuration-editor")
    public ResponseEntity<NDRPusherConfig> autoPushConfigEdit(@RequestBody NDRAuthRequestDTO ndrAuthRequestDTO) {
        return ResponseEntity.ok (NDRJSONService.updateAutoPushConfig(ndrAuthRequestDTO));
    }

    @DeleteMapping("/auto-push-configuration-deleter")
    public String deleteAutoPushConfig() {
        return this.NDRJSONService.deleteAutoPushConfig();
       // return ResponseEntity.accepted ().build ();
    }

    @GetMapping(value = "auto-push-configuration-viewer")
    public NDRPusherConfig viewConfiguratoin() {
        return NDRJSONService.getViewConfiguration();
    }

    @GetMapping("/ndr-auto-pusher")
    public void pushData(@RequestParam("id") Integer id) throws Exception {
        NDRJSONService.batchPushToNDR(id);//.PushPatientData(container);
    }
//    @GetMapping("/percentage-pushed")
//    public int getPercentagePushed(@RequestParam("id") Integer id) throws Exception {
//        return NDRJSONService.getPacentagePushed(id);//.PushPatientData(container);
//    }

}
