package ca.alberta.services.sithdfca.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.alberta.services.sithdfca.config.ExternalHealthState;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ExternalHealthController {
	
	@Autowired
	private ExternalHealthState state;
	
	@GetMapping(value="/public/sicken", produces=MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> hello(HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		state.setUnhealthy();
		return new ResponseEntity<>("made unhealthy", HttpStatus.OK);
	}
}
