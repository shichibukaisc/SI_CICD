package ca.alberta.services.sithdfca.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class KeyCloakErrorException extends RuntimeException 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KeyCloakErrorException(String exception) {
        super(exception);
    }

}
