package org.lamisplus.modules.ndr.controller;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.ndr.service.RecaptureBiometricService;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/ndr/recapture")
@RequiredArgsConstructor
@Slf4j
public class BiometricRecaptureController {
	
	private  final RecaptureBiometricService biometricService;
	private final SimpMessageSendingOperations messagingTemplate;
	
	@GetMapping("/generate")
	public boolean generateRecaptureBiometricDetails(@RequestParam List<Long> facilityIds){
		messagingTemplate.convertAndSend("/topic/ndr-recapture", "start");
		Stopwatch stopwatch = Stopwatch.createStarted();
		List<Boolean> result = new ArrayList<>();
		facilityIds.forEach (
				facilityId -> {
					boolean result1 = biometricService.generateRecaptureBiometrics(facilityId);
					result.add(result1);
				});
		messagingTemplate.convertAndSend("/topic/ndr-recapture", "end");
		log.info("Total time taken to generate a file: {}", stopwatch.elapsed().toMillis());
		return result.contains(true);
	}
}
