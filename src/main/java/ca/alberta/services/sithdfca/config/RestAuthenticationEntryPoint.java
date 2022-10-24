package ca.alberta.services.sithdfca.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ca.alberta.services.sithdfca.model.ApiError;

/**
 * 
 * This is a custom error handling class for Authentication errors.  Unfortunately, this doesn't account for issue with JWT decoding
 * such as an invalid audience.  Currently don't have a workaround for that.  The default handling puts an error in the response header.
 * 	WWW-Authenticate: 
 * 		Bearer error="invalid_token",
 * 		error_description="An error occurred while attempting to decode the Jwt: The required audience is missing",
 * 		error_uri="https://tools.ietf.org/html/rfc6750#section-3.1"
 * 
 *
 */

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		// This object is just like the model class,
		// the processor will convert it to appropriate format in response body
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		String path = request.getRequestURI();
		ApiError error = (ApiError)request.getSession().getAttribute("errorObj");
		if(error != null) {
			request.getSession().removeAttribute("errorObj");
		}else {
			error = new ApiError();
			error.setMessage(authException.getMessage());
			error.setDebugMessage(authException.getLocalizedMessage());
			error.setHttpStatus(HttpStatus.UNAUTHORIZED);
			error.setPath(path);
		}
		response.setStatus(error.getHttpStatus().value());
		response.setContentType("application/json");		
		response.getOutputStream().print(om.writeValueAsString(error));
	}
}
