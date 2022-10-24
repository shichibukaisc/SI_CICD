package ca.alberta.services.sithdfca.exception;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ca.alberta.services.sithdfca.model.ApiError;
import ca.alberta.services.sithdfca.model.ApiSubError;

//TODO Externalize Custom Error Message strings to properties file
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	@Autowired
	ExceptionMessages em;
	
	
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
 		ApiError apiError = new ApiError(status, request, ex, em.getNoHandlerFound(), new ApiSubError(ex.getMessage()));
		return new ResponseEntity<Object>(apiError, headers, apiError.getHttpStatus());
	}
	
	@ExceptionHandler({ NotAuthorizedException.class})
	public ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException ex, WebRequest request) {
		ApiSubError error = new ApiSubError(ex.getMessage());
		ApiError errors = new ApiError(HttpStatus.FORBIDDEN,request,ex,ex.getMessage(),error);
		return new ResponseEntity<>(errors,errors.getHttpStatus());
	}
	
	@ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		ApiSubError errors = new ApiSubError(ex.getMessage());
		ApiError error = new ApiError(HttpStatus.FORBIDDEN,request,ex,ex.getMessage(), errors);
		error.setMessage(ex.getMessage() + " - You do not have sufficient privilege to access this resource");
		error.setDebugMessage(ex.getLocalizedMessage());
		error.setHttpStatus(HttpStatus.FORBIDDEN);
		error.setPath(((ServletWebRequest)request).getRequest().getRequestURI());
		return new ResponseEntity<>(error,error.getHttpStatus());
    }
	
	@ExceptionHandler({ SubscriptionIDException.class})
	public ResponseEntity<Object> noDataFoundException(SubscriptionIDException ex, WebRequest request) {
		ApiSubError error = new ApiSubError(ex.getMessage());
		ApiError errors = new ApiError(HttpStatus.NOT_FOUND,request,ex,ex.getMessage(),error);
		return new ResponseEntity<>(errors,errors.getHttpStatus());
	}
	
	@ExceptionHandler({KeyCloakErrorException.class})
	public ResponseEntity<Object> keycloakErrorException(KeyCloakErrorException ex, WebRequest request) {
		ApiSubError error = new ApiSubError("Keycloak is not available!");
		ApiError errors = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, request,ex, "Something wrong with Keycloak", error);
		return new ResponseEntity<>(errors,errors.getHttpStatus());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<ApiSubError> errors = new ArrayList<ApiSubError>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(new ApiSubError(error.getField() + ": " + error.getDefaultMessage()));
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(new ApiSubError(error.getObjectName() + ": " + error.getDefaultMessage()));
		}

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request, ex, "My MethodArgumentNotValidException Message", errors);

		return handleExceptionInternal(ex, apiError, headers, apiError.getHttpStatus(), request);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiSubError error = new ApiSubError(ex.getParameterName() + " parameter is missing");
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request,ex, "My MissingServletRequestParameterException Message", error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}	
	
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		//ApiSubError error = new ApiSubError(ex.getVariableName() + " template variable is missing");
		ApiSubError error = new ApiSubError(ex.getMessage());
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request,ex, "My MissingPathVariableException Message", error);
		//return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
		
		return handleExceptionInternal(ex, apiError, headers, apiError.getHttpStatus(), request);
	}
	
	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,  WebRequest request) {
		List<ApiSubError> errors = new ArrayList<ApiSubError>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			String path =  violation.getPropertyPath().toString();
			String param = path.substring(path.indexOf(".") + 1);
			errors.add(new ApiSubError(String.format("{%s} : %s",param,violation.getMessage())));
		}

		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request, ex, em.getConstraintViolation(), errors);
		return new ResponseEntity<ApiError>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ApiError> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
		ApiSubError error = new ApiSubError(ex.getMessage());
//		BamboraError error = new BamboraError();
//		ObjectMapper om = new ObjectMapper();
//		try {
//			error = om.readValue(ex.getResponseBodyAsString(), BamboraError.class);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			log.error("Unable to map Bambora Error response to BamboraError object");
//		}
		ApiError er = new ApiError(ex.getStatusCode(),request, ex, "My HttpClientErrorException message", error);
		return new ResponseEntity<>(er, er.getHttpStatus());

	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
		String requiredTypeName = ex.getRequiredType().getName();
		//This trims off names like java.lang.Integer to just Integer
		requiredTypeName = requiredTypeName.substring(requiredTypeName.lastIndexOf(".") + 1);
		ApiSubError error = new ApiSubError(ex.getName() + " should be of type " + requiredTypeName);
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request, ex, "my MethodArgumentTypeMismatchException message", error);
		return new ResponseEntity<ApiError>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}	

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
		ApiSubError error = new ApiSubError(builder.toString());
		ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, request, ex, "My HttpRequestMethodNotSupportedException message", error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
		
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,	WebRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));
		
		ApiSubError error = new ApiSubError(builder.substring(0, builder.length() - 2));

		ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, ex, "My HttpMediaTypeNotSupportedException message", error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, request, ex, "My Exception message", new ApiSubError("error occurred"));
		return new ResponseEntity<ApiError>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String msg = ex.getMessage().replace("java.lang.", "");
		msg = msg.substring(0,msg.indexOf(";"));
		//builder.append("\n" + ex.getCause().getMessage());
		ApiSubError error = new ApiSubError(msg);
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request, ex, "My HttpRequestMethodNotSupportedException message", error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}
	
	@ExceptionHandler({JwtValidationException.class})
	public ResponseEntity<ApiError> handleAll(JwtValidationException ex, WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, request, ex, "My Exception message", new ApiSubError("error occurred"));
		return new ResponseEntity<ApiError>(apiError, new HttpHeaders(), apiError.getHttpStatus());
	}

}
