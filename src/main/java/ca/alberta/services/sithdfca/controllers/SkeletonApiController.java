package ca.alberta.services.sithdfca.controllers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ca.alberta.services.sithdfca.Constants;
import ca.alberta.services.sithdfca.config.Defaults;
import ca.alberta.services.sithdfca.exception.SubscriptionIDException;
import ca.alberta.services.sithdfca.model.ApiError;
import ca.alberta.services.sithdfca.model.ExampleResponse;
import ca.alberta.services.sithdfca.model.SubscriptionInfo;
import ca.alberta.services.sithdfca.model.SubscriptionInfoUserDetail;
import ca.alberta.services.sithdfca.service.LoginUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
public class SkeletonApiController implements SkeletonApi{
	    
    @Autowired
    Defaults defaults;
    
    @Autowired
    LoginUserDetailService svc;
	
	@Value("${example.value}")
	String exampleValue;

	@Value("${example.name}")
	String exampleName;
	
	@Autowired
	LoginUserDetailService userDetailsService;
		
	@Operation(
		summary = "Sample API that demonstrates a simple get request that outputs a value from a property file",
		description = "Sample API that demonstrates a simple get request that outputs values from the properties file - description field"
	)
	@Tag(name=Constants.TAG_1_NAME)
	@ApiResponses(value = {
		@ApiResponse(responseCode="200",description="Your success message",content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE, schema=@Schema(implementation=ExampleResponse.class),
			examples={
				@ExampleObject(name="Default with Auth Param",value="{\"name\":\"JJ\",\"value\":\"Used Auth\"}"),
				@ExampleObject(name="Default No Auth",value="{\"name\":\"JJ\",\"value\":\"No Auth\"}"),
			})
		),
		@ApiResponse(responseCode="403", description="Not Authorized", content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE, schema= @Schema(implementation=ApiError.class),
			examples={
					@ExampleObject(name="Not Authorized",value="{\"timestamp\":\"29-04-2021 01:31:20\",\"message\":\"Not Authorized - Authorization is required to access this service\",\"path\":\"/skeleton/v1/hello\",\"httpStatus\":\"FORBIDDEN\",\"errors\":[{\"message\":\"Not Authorized - Authorization is required to access this service\"}]}")
				})),
		@ApiResponse(responseCode="404", description="Subscription ID not found", content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE, schema= @Schema(implementation=ApiError.class),
			examples={
					@ExampleObject(name="SubscriptionID Not found",value="{\"timestamp\":\"29-04-2021 01:31:45\",\"message\":\"SubscriptionID is invalid or not enrolled in this service.\",\"path\":\"/skeleton/v1/hello\",\"httpStatus\":\"NOT_FOUND\",\"errors\":[{\"message\":\"SubscriptionID is invalid or not enrolled in this service.\"}]}")
				}))
	})
	@SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
	@GetMapping(value = "/hello", produces=MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({Constants.USER_ROLE,Constants.ADMIN_ROLE})
	public ResponseEntity<ExampleResponse> getName(HttpServletRequest request) throws JsonMappingException, JsonProcessingException{
		String ev = exampleValue;
		ExampleResponse ex = new ExampleResponse(exampleName, ev);		
		return new ResponseEntity<ExampleResponse>(ex, HttpStatus.OK);
	}
	
	/**
	 * This method demonstrates how to leverage the Authentication object to get the current user details
	 * 
	 * @param request
	 * @param auth
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	
    @GetMapping(value="/subscriptionInfo")
	@RolesAllowed({Constants.USER_ROLE,Constants.ADMIN_ROLE})
	@Operation(
		summary = "Displays the identity authenticated by the provided access token",
		description = "This endpoint demonstrates implicily accessing the Authentication object to retrive the current UserDetails.\n"
				+ "This call is functionally identical to not specifying a subscritionId for /subscriptions/{subscriptionId}"
	)
	@SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
    @Tag(name=Constants.TAG_2_NAME)
	public ResponseEntity<?> getSubscriptionInfo(HttpServletRequest request, Authentication auth) throws JsonMappingException, JsonProcessingException {
    	SubscriptionInfoUserDetail userDetails = svc.loadUserByAuthentication(auth);
		log.debug("This is the clientId of the authorized token. " + userDetails.getSubscriptionInfo().getClientId());
		return new ResponseEntity<SubscriptionInfo>(userDetails.getSubscriptionInfo(), HttpStatus.OK);
	}
	
	@Override
	@RolesAllowed({Constants.USER_ROLE,Constants.ADMIN_ROLE})
	@SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
	public ResponseEntity<ExampleResponse> getName(@Valid @NotNull String name, HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		//Authorize the request
		//SubscriptionInfo subInfo = Utils.authorize(repo, request, defaults);
		ExampleResponse ex = new ExampleResponse(name, exampleValue);
		return new ResponseEntity<ExampleResponse>(ex, HttpStatus.OK);
	}
	
	/**
	 * A sample method that demonstrates how to retrieve all the SubscriptionInfo objects from the repository
	 * @param r
	 * @return
	 */

    @GetMapping(value="/whoami")
    @SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
    @Tag(name=Constants.TAG_2_NAME)
	//@RolesAllowed({Constants.USER_ROLE,Constants.ADMIN_ROLE})
	@Operation(
		summary = "Displays the User Details for the provided access token",
		description = "The access token will be used to retrieve the user details.  We will leverage the Authentication object to demonstrate this"
	)
    public ResponseEntity<SubscriptionInfoUserDetail> getWhoAmI(Authentication auth, HttpServletRequest r) {
    	if(log.isDebugEnabled())
    		auth.getAuthorities().stream().forEach(e -> log.debug("Role: " + e.toString()));
        SubscriptionInfoUserDetail si = userDetailsService.loadUserByAuthentication(auth);
        return new ResponseEntity<>(si, HttpStatus.OK);
    }
	
	/**
	 * A sample method that demonstrates how to retrieve a specific SubscriptionInfo object from the repository
	 * It also demonstrates leveraging the Principal object directly.  The subscription ID of the currently authenticated user will be used if
	 * no subscriptionId is entered.
	 * @param r
	 * @return
	 */

    @GetMapping(value={"/subscriptions/{subscriptionId}"})
    @SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
	@Tag(name=Constants.TAG_2_NAME)
	@RolesAllowed({Constants.USER_ROLE,Constants.ADMIN_ROLE})
	@Operation(
		summary = "Displays the identity information for the provided subscriptionId",
		description = "If no subscriptionId is provided, all subscriptions will be returned."
	)
    public ResponseEntity<?> getSubscription(@Parameter(required = true) @PathVariable(name="subscriptionId") Long subscriptionId, HttpServletRequest r, Authentication auth) {
        //SubscriptionInfo subscriptionList = (List<SubscriptionInfo>) repo.findAll();
    	SubscriptionInfoUserDetail subInfo = svc.loadUserBySubscriptionId(subscriptionId);
        if (subInfo == null) {
            throw new SubscriptionIDException("No Subcription found. Is the datasource mapped properly?");
        }else {
        	return new ResponseEntity<>(subInfo.getSubscriptionInfo(), HttpStatus.OK);
        }        
    }
	
	/**
	 * Retrieve all the subscriptions.  This method demonstrates requiring a specific role in order to access the endpoint
	 * 
	 * @param r
	 * @return
	 */

    @GetMapping(value={"/subscriptions"})
    @SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
	@Tag(name=Constants.TAG_2_NAME)
	@RolesAllowed({Constants.USER_ROLE, Constants.ADMIN_ROLE})
	@Operation(
		summary = "Displays identity information for all subscribers of the service",
		description = "This endpoint should only be accessible to the " + Constants.ADMIN_ROLE + " role."
	)
    public ResponseEntity<List<SubscriptionInfoUserDetail>> getAllSubscriptions() {
        //SubscriptionInfo subscriptionList = (List<SubscriptionInfo>) repo.findAll();
    	List<SubscriptionInfoUserDetail> allSubInfo = svc.loadAllSubscriptions();
        if (allSubInfo == null) {
            throw new SubscriptionIDException("No Subcriptions found. Is the datasource mapped properly?");
        }else {
        	return new ResponseEntity<>(allSubInfo, HttpStatus.OK);
        }
    }
	
	/**
	 * A Utility Method that dumps all the headers as a json response.  This endpoint is a public endpoint and is configured for anonymous access as part
	 * of the ${spring.security.custom.whitelist} property in application.properties
	 * @param r
	 * @return
	 */

    @Operation(
    	summary = "A Utility Method that dumps all the headers as a json response",
    	description ="This endpoint is a public endpoint and is configured for anonymous access as part of the '**spring.security.custom.whitelist**' property in application.properties"
    )
	@Tag(name=Constants.TAG_3_NAME, description = Constants.TAG_3_DESCRIPTION)
    @GetMapping(value="/public/headers")
    public ResponseEntity<HashMap<String,String[]>>getHeaders(HttpServletRequest r) {
    	Enumeration<String> headers =  r.getHeaderNames();
    	HashMap<String, String[]> allHeaders = new HashMap<String, String[]>();
    	while(headers.hasMoreElements()) {
    		String headerName = headers.nextElement();
    		Enumeration<String> headerValuesEnum = r.getHeaders(headerName);
    		ArrayList<String> headerValues = new ArrayList<String>();
    		while(headerValuesEnum.hasMoreElements()) {
    			headerValues.add(headerValuesEnum.nextElement());
    		}    		
    		allHeaders.put(headerName, headerValues.toArray(new String[headerValues.size()]));
    	}
        return new ResponseEntity<>(allHeaders, HttpStatus.OK);
    }
    
    @GetMapping(value="/validate/{param1}/{param2}")
    @SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
	@Tag(name=Constants.TAG_1_NAME)
	@RolesAllowed({Constants.USER_ROLE,Constants.ADMIN_ROLE})
    public ResponseEntity<String> validationDemo(
    		@PathVariable @NotNull @Min(5) Integer param1,
    		@PathVariable @NotNull @Min(10) Integer param2, HttpServletRequest req){
    	return new ResponseEntity<>(String.format("Hello! Path param1=%s and Path param2=%s!", param1, param2), HttpStatus.OK);
    }
    
    /**
     * This method demonstrates connecting to a 3rd party using the RestTemplateBuilder.
     * In this example it retrieves the JSON Web Keys from the configured Identity Provider
     * @param request
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    
    @GetMapping(value="/public/jwks")
	@Tag(name=Constants.TAG_3_NAME, description = Constants.TAG_3_DESCRIPTION)
    @Operation(
    	summary = "A Utility Method that gets the jwks from the identity provider",
    	description ="This endpoint is a public endpoint and is configured for anonymous access as part of the '**spring.security.custom.whitelist**' property in application.properties\n"
    			+ "It also demonstrates connecting to a 3rd party using a RestTemplate"
    )
	public ResponseEntity<?> getJWKS(HttpServletRequest request) throws JsonMappingException, JsonProcessingException {
		RestTemplate rt = new RestTemplateBuilder().build();
		ResponseEntity<String> result = rt.exchange(defaults.getJwkProviderUri(), HttpMethod.GET, null, String.class);		
		return result;
	}
	
}
