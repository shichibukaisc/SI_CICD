package ca.alberta.services.sithdfca.config;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import ca.alberta.services.sithdfca.Constants;
import ca.alberta.services.sithdfca.converters.TokenBasedGrantedAuthoritiesConverter;
import ca.alberta.services.sithdfca.converters.UserDetailsBaseGrantedAuthoritiesConverter;
import ca.alberta.services.sithdfca.validator.AudienceValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	Defaults defaults;

	@Autowired
	JwtRequestFilter jwtRequestFilter;

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuer;
	
	@Value("${management.endpoints.web.path-mapping.shutdown}")
	private String shutdownEndpoint;
	
	@Autowired
	UserDetailsBaseGrantedAuthoritiesConverter userDetailsBasedGrantedAuthoritiesConverter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.debug("Setting http Security.  Here's the whitelisted endpoints");
		Stream.of(defaults.getSpringSecurityCustomWhiteList()).forEach(e -> log.debug(e));
// @formatter:on

		http		
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.csrf().disable()
			//Allow public access to any values specified in the applications.properties file value spring.security.custom.whitelist
			.authorizeRequests()
				//Allow access to Swagger Endpoints and to the health endpoint
				.antMatchers(defaults.springSecurityCustomWhiteList).permitAll()
				//.antMatchers("/v1/headers").permitAll()
				//Secure the shutdown endpoint
				//.antMatchers(shutdownEndpoint).hasRole("SBRAS_ADMIN")
				.antMatchers(shutdownEndpoint).hasRole(Constants.ADMIN_ROLE)
				//.antMatchers("/v1/headers").hasAnyRole("SBRAS_*", "ANONYMOUS")
				//Anything else must be authenticated.   We can further refine this to accept specific roles either here via
				//.antMatchers("/v1/**").hasRole("SBRAS_USER")
			//or via annotations 
			//on each controller method.			
			.anyRequest().authenticated()
			.and()
//			.anonymous()	
			.oauth2ResourceServer().jwt();
		http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint());
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		// TODO Auto-generated method stub
		return super.authenticationManager();
	}

	@Bean
	/**
	 * We need to enhance our JWT Decoder to include audience validation.  Below we take the default OIDC Issuer
	 * and add our audienceValidator to it.
	 * @return
	 */
	JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);

		OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(defaults.getSubscriptionAudience());
		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
		OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

		jwtDecoder.setJwtValidator(withAudience);

		return jwtDecoder;
	}
	
	@Bean
	/**
	 * In order for us to map our roles from either our local authorization management strategy or directly from our access token,
	 * we need to implement a converter so we know what fields map to what roles.  We can set with authorization we'd like to use
	 * in the application.properties
	 * @return
	 */
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	    //if we haven't sent anything, we will use the default authorization method (refer to Defaults for implementation)
		String authStrategy = defaults.getAuthorizationStrategy();
		
		if(authStrategy.equalsIgnoreCase(Constants.AUTHORIZATION_STRATEGY_ACCESS_TOKEN)) {
			//Get Granted Authorities from Access Token
			TokenBasedGrantedAuthoritiesConverter grantedAuthoritiesConverter = new TokenBasedGrantedAuthoritiesConverter();
			grantedAuthoritiesConverter.setAuthoritiesAudienceName(defaults.getSubscriptionAudience());
		    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		}else if(authStrategy.equalsIgnoreCase(Constants.AUTHORIZATION_STRATEGY_IN_MEMORY)) {
			//Get Granted Authorities from In Memory DB
			//UserDetailsBaseGrantedAuthoritiesConverter grantedAuthoritiesConverter = new UserDetailsBaseGrantedAuthoritiesConverter();
		    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(userDetailsBasedGrantedAuthoritiesConverter);
		}else {
			//use the default JWT granted authorities
			JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		}
	    return jwtAuthenticationConverter;
	}
	
	


}
