package ca.alberta.services.sithdfca.controllers;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class HelloController {
	
	@GetMapping(value="/public/hello", produces=MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> hello(HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		var uptime = Duration.between(Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime()), Instant.now());
		var uptimeString = String.format("%02d:%02d:%02d", uptime.toHoursPart(), uptime.toMinutesPart(), uptime.toSecondsPart());
		String value = String.format("Hello from %s! I am this old: %s", System.getenv("HOSTNAME"), uptimeString);
		return new ResponseEntity<>(value, HttpStatus.OK);
	}
}
