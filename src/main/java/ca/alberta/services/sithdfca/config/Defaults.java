package ca.alberta.services.sithdfca.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import ca.alberta.services.sithdfca.Constants;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@ConfigurationProperties
@Component
@Hidden
@Data @AllArgsConstructor @NoArgsConstructor
public class Defaults {

	@Value("${application.version}")
	String applicationVersion;
	@Value("${application.environment}")
	String environment;
	@Value("${springdoc.swagger-ui.path}")
	String swaggerUIPath;
	@Value("${oidc.jwks.uri}")
	String jwkProviderUri;
	@Value("${subscriptionAudience}")
	String subscriptionAudience;
	@Value("#{'${swagger.whitelist.paths}'.split(',')}")
	String[] swaggerWhiteList;
	@Value("#{'${spring.security.custom.whitelist}'.split(',')}")
	String[] springSecurityCustomWhiteList;
	@Value("${application.authorization.strategy:" + Constants.DEFAULT_AUTHORIZATION_STRATEGY + "}")
	String authorizationStrategy;
	
}
