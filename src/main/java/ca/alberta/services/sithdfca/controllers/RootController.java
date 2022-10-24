package ca.alberta.services.sithdfca.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.alberta.services.sithdfca.Constants;
import ca.alberta.services.sithdfca.config.Defaults;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * This Controller is used as a filter to redirect users accessing the root of the service.  If it is in a non-prod environment, we will redirect them to the Swagger Documentation, otherwise
 * we will mark it as forbidden 
 * @author Admin
 *
 */

@RestController
//@Slf4j
public class RootController {

	@Autowired
	Defaults defaults;
	
	@RequestMapping(path={"","/","/${application.version}","${swaggerRoot}"})
	@Hidden	
	
	public ResponseEntity<Void> showRootMessage(HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {
		if(!defaults.getEnvironment().equalsIgnoreCase(Constants.PROD)) {
			request.getRequestDispatcher(defaults.getSwaggerUIPath()).forward(request, response);
			return null;
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
}
