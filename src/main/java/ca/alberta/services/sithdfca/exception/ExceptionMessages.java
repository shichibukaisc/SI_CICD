package ca.alberta.services.sithdfca.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@ConfigurationProperties(prefix = "exception.message")
@Configuration
@PropertySource("classpath:exceptions.properties")
@Data
public class ExceptionMessages {
	private String noHandlerFound;
	private String constraintViolation;
	private String accessDenied;

}
