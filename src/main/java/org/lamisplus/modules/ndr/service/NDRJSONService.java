package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.User;
import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.ndr.domain.dto.*;
import org.lamisplus.modules.ndr.domain.entities.NDRMessages;
import org.lamisplus.modules.ndr.domain.entities.NDRPusher;
import org.lamisplus.modules.ndr.domain.entities.NDRPusherConfig;
import org.lamisplus.modules.ndr.domain.entities.NdrXmlStatus;
import org.lamisplus.modules.ndr.repositories.NDRMessagesRepository;
import org.lamisplus.modules.ndr.repositories.NDRPusherConfigRepository;
import org.lamisplus.modules.ndr.repositories.NDRPusherRepository;
import org.lamisplus.modules.ndr.repositories.NdrXmlStatusRepository;
import org.lamisplus.modules.ndr.schema.Container2;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NDRJSONService {
    //TODO: Save the hard-coded values in database
    String pingUrl = "https://emr-ndrpushsandbox.phis3project.org.ng/api/Cronbox";
    
    //"https://emr-ndrpush.phis3project.org.ng/api/Cronbox";

    private final int BATCHMAX = 10;
    String authEndPoint = "/auth";
    String pushEndPoint = "/beep";
    String logsEndPoint = "/errorLogs";
   // String baseUrl = "";
    String baseUrl = "******";
    String email = "*******";
    String password = "********";

    private final NDRPusherRepository ndrPusherRepository;

    private final UserService userService;

    private final NDRMessagesRepository ndrMessagesRepository;

    private final NDRPusherConfigRepository ndrPusherConfigRepository;

    private final NdrXmlStatusRepository ndrXmlStatusRepository;

    private String ConvertContainerToString(Container2 container) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(df);
        return mapper.writeValueAsString(container);
    }

    //ping NDR server
    public String PingNDRServer() {
        HttpEntity<String> entity = new HttpEntity<>("{}", GetHTTPHeaders());
        ResponseEntity<NDRPingResponseDTO> response = GetRestTemplate().exchange(pingUrl, HttpMethod.GET,
                entity, NDRPingResponseDTO.class);
        NDRPingResponseDTO responseDTO = response.getBody();
        return "success";
    }

    public NDRAuthResponseDTO AuthenticateUser(String email, String password) {
        NDRAuthRequestDTO loginRequestDTO = new NDRAuthRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);
        HttpEntity<NDRAuthRequestDTO> loginEntity = new HttpEntity<>(loginRequestDTO, GetHTTPHeaders());
        ResponseEntity<NDRAuthResponseDTO> response = GetRestTemplate().exchange(baseUrl + authEndPoint,
                HttpMethod.POST, loginEntity, NDRAuthResponseDTO.class);
        log.info("url {}  data {}", baseUrl + authEndPoint, response.getBody());
        return response.getBody();
    }

    private NDRDataResponseDTO PushData(String token, List<String> NDRData) {
        HttpHeaders headers = GetHTTPHeaders();
        headers.set("token", token);
        HttpEntity<List<String>> dataEntity = new HttpEntity<>(NDRData, headers);
        ResponseEntity<NDRDataResponseDTO> response = GetRestTemplate().exchange(baseUrl + pushEndPoint,
                HttpMethod.POST, dataEntity, NDRDataResponseDTO.class);
        return response.getBody();
    }

    public NDRLogsResponseDTO GetErrorLogs(String batchId) {
        HttpEntity<String> entity = new HttpEntity<>("", GetHTTPHeaders());
        ResponseEntity<NDRLogsResponseDTO> response = GetRestTemplate().exchange(
                baseUrl + logsEndPoint + "?batchId=" + batchId,
                HttpMethod.GET, entity, NDRLogsResponseDTO.class);
        return response.getBody();
    }

    private HttpHeaders GetHTTPHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Application");
        return headers;
    }

    public RestTemplate GetRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        //set message converters
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }

    public void creatPusher(NDRDataResponseDTO ndrDataResponseDTO, String msg, Long facilityId) {
        NDRPusher ndrPusher = new NDRPusher();
        ndrPusher.setBatchNumber(ndrDataResponseDTO.getBatchNumber());
        ndrPusher.setCode(ndrDataResponseDTO.getCode());
        ndrPusher.setDeMessage(msg);
        ndrPusher.setIsAuthenticated(ndrDataResponseDTO.getIsAuthenticated());
        ndrPusher.setMessage(ndrDataResponseDTO.getMessage());
        ndrPusher.setPushDate(LocalDate.now());
        ndrPusher.setFacilityId(facilityId);
//        Optional<User> currentUser = this.userService.getUserWithRoles();
//        if (currentUser.isPresent()) {
//            ndrPusher.setFacilityId(currentUser.get().getCurrentOrganisationUnitId());
//        }
        ndrPusherRepository.save(ndrPusher);


    }

    private String GetJWT() {
        //TODO: Check if token exists and is valid (less than 7 days)
        //return token
        //else
        //login to NDR and get new token
        //save token in DB
        //return token
        Optional<User> currentUser = this.userService.getUserWithRoles();
        if (currentUser.isPresent()) {
            Long facilityId = currentUser.get().getCurrentOrganisationUnitId();
            Optional<NDRPusherConfig> ndrPusherConfigOptional = ndrPusherConfigRepository.findByFacilityId(facilityId);
            if (ndrPusherConfigOptional.isPresent()) {
                NDRAuthResponseDTO authResponseDTO = AuthenticateUser(ndrPusherConfigOptional.get().getUsername(), ndrPusherConfigOptional.get().getPassword());
                return authResponseDTO.getToken();
            }
        }
        return "no token";
    }

    public NDRPusherConfig save(String username, String password, String baseUrl) {
        NDRPusherConfig ndrPusherConfig = new NDRPusherConfig();
        Optional<User> currentUser = this.userService.getUserWithRoles();
        User user = (User) currentUser.get();
        Long facilityId = user.getCurrentOrganisationUnitId();
        ndrPusherConfig.setPassword(password);
        ndrPusherConfig.setUsername(username);
        ndrPusherConfig.setFacilityId(facilityId);
        return ndrPusherConfigRepository.save(ndrPusherConfig);
    }

//    public NDRPusherConfig  updateAutoPushConfig (Long id, NDRAuthRequestDTO ndrAuthRequestDTO)
//    {
//        NDRPusherConfig ndrPusherConfig = new NDRPusherConfig();
//        Optional<NDRPusherConfig> ndrPusherConfigOptional = ndrPusherConfigRepository.findById(id);
//        if(ndrPusherConfigOptional.isPresent()){
//            ndrPusherConfig = ndrPusherConfigOptional.get();
//            ndrPusherConfig.setPassword(ndrAuthRequestDTO.getPassword());
//            ndrPusherConfig.setUsername(ndrAuthRequestDTO.getEmail());
//            //ndrPusherConfig.setFacilityId(facilityId);
//            return ndrPusherConfigRepository.save(ndrPusherConfig);
//        }
//        return null;
//    }

    public NDRPusherConfig updateAutoPushConfig(NDRAuthRequestDTO ndrAuthRequestDTO) {
        try {
            Long facilityId = 0L;
            Optional<User> currentUser = this.userService.getUserWithRoles();
            if (currentUser.isPresent()) {
                facilityId = currentUser.get().getCurrentOrganisationUnitId();
            }
            Optional<NDRPusherConfig> ndrPusherConfigOptional = this.ndrPusherConfigRepository.findByFacilityId(facilityId);
            // Optional<NDRPusherConfig> ndrPusherConfigOptional = ndrPusherConfigRepository.findById(id);
            if (ndrPusherConfigOptional.isPresent()) {
                NDRPusherConfig ndrPusherConfig = ndrPusherConfigOptional.get();
                ndrPusherConfig.setPassword(ndrAuthRequestDTO.getPassword());
                ndrPusherConfig.setUsername(ndrAuthRequestDTO.getEmail());
                //ndrPusherConfig.setFacilityId(facilityId);
                return ndrPusherConfigRepository.save(ndrPusherConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteAutoPushConfig() {
        Long facilityId = 0L;
        String reply = "Could not Deleted";
        Optional<User> currentUser = this.userService.getUserWithRoles();
        if (currentUser.isPresent()) {
            facilityId = currentUser.get().getCurrentOrganisationUnitId();
        }
        Optional<NDRPusherConfig> ndrPusherConfigOptional = this.ndrPusherConfigRepository.findByFacilityId(facilityId);
        if (ndrPusherConfigOptional.isPresent()) {
            this.ndrPusherConfigRepository.delete(ndrPusherConfigOptional.get());
            reply = "Deleted";
        }
        return reply;
    }

    public NDRPusherConfig getViewConfiguration() {
        System.out.println("I get here now "+LocalDate.now());
        Long facilityId = 0L;
        Optional<User> currentUser = this.userService.getUserWithRoles();
        if (currentUser.isPresent()) {
            facilityId = currentUser.get().getCurrentOrganisationUnitId();
        }
        Optional<NDRPusherConfig> ndrPusherConfigOptional = this.ndrPusherConfigRepository.findByFacilityId(facilityId);//.orElseThrow(() -> new Exception("NDRPusherConfig NOT FOUND"));
        if (ndrPusherConfigOptional.isPresent()) {
            System.out.println("I get here now2 "+LocalDate.now()+"\tfacilityId = "+facilityId);
            return ndrPusherConfigOptional.get();
        }
        return null;

    }

    @SneakyThrows
    public NDRPusherConfig getSingleInfant(Long id) {
        return this.ndrPusherConfigRepository.findById(id)
                .orElseThrow(() -> new Exception("NDRPusherConfig NOT FOUND"));
    }


    public Long getPacentagePushed(Integer id) {
       int per = 0;
        Long facilityId = 0L;
        Optional<User> currentUser = this.userService.getUserWithRoles();
        if (currentUser.isPresent()) {
            facilityId = currentUser.get().getCurrentOrganisationUnitId();
        }
        try {
            String identifier = ndrXmlStatusRepository.findById(id).get().getPushIdentifier();
            int yet2bePushed = ndrMessagesRepository.findNDRMessagesByIsPushedAndFacilityIdAndIdentifier(Boolean.FALSE, facilityId, identifier).size();
            int totalRecords = ndrMessagesRepository.getTotalRecordByFacilityAndIdentifier(facilityId, identifier);
            double percentage = 0.0;
            if (totalRecords > 0) percentage =  (((totalRecords - yet2bePushed) / totalRecords) * 100);
            per = (int) Math.ceil(percentage);
            //Long
        } catch (Exception e) {
        }
        return new Long(String.valueOf(per));
    }

    public void batchPushToNDR(Integer id) throws Exception {
        //System.out.println(container);
        Long facilityId = 0L;
        Optional<User> currentUser = this.userService.getUserWithRoles();
        if (currentUser.isPresent()) {
            facilityId = currentUser.get().getCurrentOrganisationUnitId();
        }

        if (PingNDRServer().equals("success")) {
            System.out.println("successfully ping");
            String token = GetJWT();
            String identifier = "";
            NdrXmlStatus ndrXmlStatus = new NdrXmlStatus();
            Optional<NdrXmlStatus> ndrXmlStatusOptional = ndrXmlStatusRepository.findById(id);
            if (ndrXmlStatusOptional.isPresent()) {
                ndrXmlStatus = ndrXmlStatusOptional.get();
                identifier = ndrXmlStatus.getPushIdentifier();

                List<NDRMessages> ndrMessagesList =
                        ndrMessagesRepository.findNDRMessagesByIsPushedAndFacilityIdAndIdentifier(Boolean.FALSE, facilityId, identifier);
                Iterator iterator = ndrMessagesList.iterator();
                int size = ndrMessagesList.size();
                log.info("container size :" + size);
                int batches = 0;
                int counter = 0;
                List<String> data = new ArrayList<>();
                List<NDRMessages> messages = new ArrayList<>();
                while (iterator.hasNext()) {
                    NDRMessages msg = (NDRMessages) iterator.next();
                    batches++; counter++;
                    if(batches%BATCHMAX != 0){
                        data.add(msg.getDeMessage().replace("null,", ""));
                        messages.add(msg);
                        continue;
                    }
                    else if ( (batches%BATCHMAX == 0) || (counter == size)) {
                        if (batches == BATCHMAX) {
                            data.add(msg.getDeMessage());
                            messages.add(msg);
                        }
                        NDRDataResponseDTO ndrDataResponseDTO = PushData(token, data);
                        if (ndrDataResponseDTO.getMessage().contains("success")) {
                            //msg.setIsPushed(Boolean.TRUE);
                            //msg.setMessageDate(LocalDate.now());
                            //ndrMessagesRepository.save(msg);
                            log.info("data:{}", ndrDataResponseDTO);
                            log.info("batchId from server : {}", ndrDataResponseDTO.getBatchNumber());
                            for (NDRMessages message:messages) {
                                message.setIsPushed(Boolean.TRUE);
                                message.setMessageDate(LocalDate.now());
                            }
                            ndrMessagesRepository.saveAll(messages);

                            Long percentagePushed = this.getPacentagePushed(ndrXmlStatus.getId());
                            ndrXmlStatus.setPercentagePushed(percentagePushed);
                            Boolean complete = Boolean.FALSE;
                            if (percentagePushed == 100) complete = Boolean.TRUE;
                            ndrXmlStatus.setCompletelyPushed(complete);
                            ndrXmlStatusRepository.save(ndrXmlStatus);
                        }
                        data = new ArrayList<>();
                        messages = new ArrayList<>();
                        batches = 0;
                    }
                }
            }
        }
    }
}
