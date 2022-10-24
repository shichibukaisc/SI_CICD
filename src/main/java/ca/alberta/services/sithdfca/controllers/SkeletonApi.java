package ca.alberta.services.sithdfca.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ca.alberta.services.sithdfca.Constants;
import ca.alberta.services.sithdfca.model.ApiError;
import ca.alberta.services.sithdfca.model.ExampleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@Validated
public interface SkeletonApi {
	
	@Operation(
		summary = "Sample API that demonstrates a simple get request that outputs values from the properties file and accepts a name in the url path",
		description = "Sample API that demonstrates a simple get request that outputs values from the properties file - description field"
	)
	@Tag(name=Constants.TAG_1_NAME)
	@ApiResponses(value = {
		@ApiResponse(responseCode="200", description="Your success message", content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE, schema=@Schema(implementation=ExampleResponse.class))),
		@ApiResponse(responseCode="403", description="Not Authorized", content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE, schema= @Schema(implementation=ApiError.class))),
		@ApiResponse(responseCode="404", description="Transaction not found", content=@Content(mediaType=MediaType.APPLICATION_JSON_VALUE, schema= @Schema(implementation=ApiError.class)))
	})
	@SecurityRequirement(name=Constants.SECURITY_SCHEME_NAME)
	@GetMapping(value = "hello/{name}", produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ExampleResponse> getName(
		@Parameter(in = ParameterIn.PATH,description = "Name of the example",required = true,schema = @Schema())
		@Valid @PathVariable @NotNull String name, HttpServletRequest request) throws JsonMappingException, JsonProcessingException;
}
