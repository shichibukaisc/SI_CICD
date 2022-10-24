package ca.alberta.services.sithdfca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeycloakHealthIndicator implements HealthIndicator {

	@Autowired
	Defaults defaults;
	@Value("${logging.file.name}")
	String loggingFileName;
	
	@Override
	public Health health() {
		log.debug("In Health Check. log filename is " + loggingFileName );
		try {
			ResponseEntity<String> keyResponse = new RestTemplate().getForEntity(defaults.getJwkProviderUri(), String.class);
			assert keyResponse != null && keyResponse.getBody().trim().length() > 0; 
		} catch (Exception e) {
			log.error(e.getMessage() + e.getStackTrace().toString());
			return Health.down()
    			.withDetail("Keycloak is not available!", e.getMessage())
    			.build();
		}
		return Health.up().build();
  }
}
