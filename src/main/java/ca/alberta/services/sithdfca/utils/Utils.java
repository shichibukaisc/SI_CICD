package ca.alberta.services.sithdfca.utils;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import ca.alberta.services.sithdfca.config.Defaults;
import ca.alberta.services.sithdfca.exception.NotAuthorizedException;
import ca.alberta.services.sithdfca.exception.SubscriptionIDException;
import ca.alberta.services.sithdfca.model.SubscriptionInfo;
import ca.alberta.services.sithdfca.repositories.SubscriptionInfoJpaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
			
	/**
	 * A custom converter for Boolean.  This can sometimes be needed depending on the type of 'booleans' being returned by 3rd parties
	 * @param value
	 * @return
	 */
	public static Boolean convertToBoolean(String value) {

	    boolean returnValue = false;
	    if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || 
	        "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
	        returnValue = true;
	    return returnValue;
	    
	}
	
	public static Boolean convertToBoolean(Integer value) {
		return value != null ? convertToBoolean(value.toString()) : null;
	}
	
	public static RestTemplate restBasicAuthTemplate(RestTemplateBuilder builder, String user, String pass) {
		RestTemplate rt = builder.build();
		String auth = Base64.encodeBase64String((user + ":" + pass).trim().getBytes());
		rt.getInterceptors().add(new ClientHttpRequestInterceptor() {
			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
				//This is used when a custom basic auth header is needed.  In this case 'Basic ' isn't used but instead 'Passcode '
				request.getHeaders().set("Authorization", "Passcode " + auth);// Set the header for each request
				return execution.execute(request, body);
			}
		});
		return rt;
	}
	
	/**
	 * 
	 * Retrieve the JWT token from the Authorize Header and verify it.  This is a manual approach that can be used when 
	 * Spring security is not enabled.  The flow is as follows:
	 * 1) Get the Token from the header
	 * 2) Make sure its in JWT format
	 * 3) Make sure there is a clientId claim
	 * 4) Make sure that there is an aud claim and that the audience value for this service is contained in the aud claim
	 * 5) Make sure that there is an entry in our SubscriptionInfo in-memory DB for the clientId / audience pair
	 * 6) verify the signature of the token.  This also verifies the expirey date.
	 * 
	 * If everything is success, it will return the related SubscriptionInfo object.
	 * 
	 * 
	 * @param repo
	 * @param r
	 * @param defaults
	 * @return
	 */
	
	public static SubscriptionInfo authorize(SubscriptionInfoJpaRepository repo, HttpServletRequest r, Defaults defaults) {
		SubscriptionInfo si = null;
		
		//Decode the token
		String authHeader = getTokenFromHeader(r);
		if(StringUtils.isBlank(authHeader)) {
			throw new SubscriptionIDException("No Authorization Header found.  Authorization is required to access this service");
		} 
		DecodedJWT jwtd = JWT.decode(authHeader);
		//Get the clientId
		String clientId  = jwtd.getClaim("clientId").asString();
		List<String> audience = jwtd.getAudience();
		if(log.isDebugEnabled()) log.debug("subId:" + clientId);
		

		//If there wasn't a SubscriptionID in the token, throw it out.
		if(StringUtils.isBlank(clientId)) {
			throw new SubscriptionIDException("No SubscriptionID present in JWT Token");
		} 
		if(audience == null || audience.isEmpty()) {
			throw new SubscriptionIDException("No audience present in JWT Token");
		}
		
		//Check to see if the subscription Audience is in the list.  If it is, lookup the si.
		String subscriptionAudience = defaults.getSubscriptionAudience();
		if(audience.contains(subscriptionAudience)) {
			si = repo.getByClientIdAndAudience(clientId,subscriptionAudience);
		}
		if(si == null) {
			throw new SubscriptionIDException("SubscriptionID is invalid or not enrolled in this service.");			
		}

		if(subscriptionAudience != null && !subscriptionAudience.equalsIgnoreCase(si.getAudience())) {
			throw new SubscriptionIDException("ClientId is invalid or not enrolled in this service.");			
		}
		
		//Check to see what Algorithm type is present in the header.  If its HS256, validate with secret, if its RS256, verify with JWKS
		String algorithmString = jwtd.getAlgorithm();
		if(algorithmString.equalsIgnoreCase("RS256")) {
			verifyRS256(defaults, jwtd);			
		}else if(algorithmString.equalsIgnoreCase("HS256")) {
			verifyHS256(si, jwtd);
			
		}else {
			throw new SubscriptionIDException("Unknown or unsupported alg type in JWT.");
		}
		
		
				
		return si;
	}
	
	/**
	 * Verify the token if the algorithm is HS256
	 * 
	 * @param si
	 * @param jwtd
	 */

	@Deprecated
	private static void verifyHS256(SubscriptionInfo si, DecodedJWT jwtd) {
		//Get the secret
		String secret = si.getSecret();
		//The Algorithm is hardcoded to be HS256.  We could change this to call different methods depending on what the alg type is in the JWT Header.
		Algorithm algo = Algorithm.HMAC256(secret);
		JWTVerifier verifier = JWT.require(algo).build();

		//verify the Token
		try {
			verifier.verify(jwtd);			
		} catch (Exception ex) {
		    throw new NotAuthorizedException("Not Authorized - " + ex.getMessage());
			// TODO: handle exception
		}
	}
	
	/**
	 * Verify the token if it is RS256 algorithm.
	 * @param defaults
	 * @param jwtd
	 */

	private static void verifyRS256(Defaults defaults, DecodedJWT jwtd) {
		Jwk jwk;
		JwkProvider provider = new CustomJwkProvider(defaults.getJwkProviderUri());
		try {
			jwk = provider.get(jwtd.getKeyId());
			Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
			algorithm.verify(jwtd);
			if(log.isDebugEnabled())log.debug("JWT verified!");
		} catch (JwkException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			e.printStackTrace();
		    throw new NotAuthorizedException("Not Authorized - " + e.getMessage());
		}
	}
	
	/**
	 * Get the {token} part from the Authorize HttpHeaders.  Standard format is:
	 * Authorize: Bearer {token}
	 * @param r
	 * @return
	 * @throws NotAuthorizedException
	 */
	public static String getTokenFromHeader(HttpServletRequest r) throws NotAuthorizedException {
		//First we need to get the JWT from the Authorization Header
		String authHeader = r.getHeader(HttpHeaders.AUTHORIZATION.toString());
		// If the auth header is null, throw an exception
		if(authHeader == null)
		    throw new NotAuthorizedException("Not Authorized - Authorization is required to access this service");
		
		//If authHeader doesn't have 2 values, throw an exception
		String[] authValues = authHeader.split(" ");
		if( authValues == null || authValues.length != 2)
		    throw new NotAuthorizedException("Not Authorized - Authorization is required to access this service");
		
		String authType = authHeader.split(" ")[0];
		if(authType == null || !authType.trim().equalsIgnoreCase("Bearer"))
		    throw new NotAuthorizedException("Not Authorized - Valid JWT Bearer token is required to access this service");
			
		String jwtToken = authHeader.split(" ")[1];
		return jwtToken;
	}

}
