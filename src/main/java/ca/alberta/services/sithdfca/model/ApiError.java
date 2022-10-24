package ca.alberta.services.sithdfca.model;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonFormat;

import ca.alberta.services.sithdfca.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data public class ApiError {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.ISO_8601_DATETIME_FORMAT)
    private ZonedDateTime timestamp;
	    
	private String message;
	private String debugMessage;
    private String path;
	private HttpStatus httpStatus;
	private List<ApiSubError> errors;

	public ApiError() {
		timestamp = ZonedDateTime.now();
	}

	public ApiError(ConstraintViolationException e, HttpStatus httpStatus) {
		this();
		e.getConstraintViolations().toString();
		this.message = e.getMessage();
	}
	
	public ApiError(HttpStatus httpStatus, WebRequest request, Throwable ex, String message, ApiSubError error ) {
		this();
		this.httpStatus = httpStatus;
		this.path = ((ServletWebRequest)request).getRequest().getRequestURI().toString();
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
		this.errors = Arrays.asList(error);
	}
	
	public ApiError(HttpStatus httpStatus, WebRequest request, Throwable ex, String message, List<ApiSubError> errors) {
		this();
		this.httpStatus = httpStatus;
		this.path = ((ServletWebRequest)request).getRequest().getRequestURI().toString();
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
		this.errors = errors;
	}
	
	public ApiError(HttpStatus httpStatus, HttpServletRequest request, Throwable ex, String message, ApiSubError error ) {
		this();
		this.httpStatus = httpStatus;
		this.path = request.getRequestURI().toString();
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
		this.errors = Arrays.asList(error);
	}
	
	public ApiError(HttpStatus httpStatus, HttpServletRequest request, Throwable ex, String message, List<ApiSubError> errors) {
		this();
		this.httpStatus = httpStatus;
		this.path = request.getRequestURI().toString();
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
		this.errors = errors;
	}

}
