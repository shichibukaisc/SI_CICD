package ca.alberta.services.sithdfca.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ca.alberta.services.sithdfca.exception.RestResponseEntityExceptionHandler;
import ca.alberta.services.sithdfca.repositories.SubscriptionInfoJpaRepository;

@Component
public class JwtRequestFilter extends OncePerRequestFilter{
		
	@Autowired
	SubscriptionInfoJpaRepository repo;
	
	@Autowired
	Defaults defaults;
	
	@Value("${subscriptionAudience}")
	String subscriptionAudience;
	
	@Autowired
	RestResponseEntityExceptionHandler eh;
	
	/**
	 * Due to the way spring auto handles authentication errors, we need a way to catch the errors before they are thrown in order
	 * to wrap them up nicely in our ApiError class.  This solution is incomplete so it isn't being used at the moment.  Keeping it around just in case. 
	 */
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//		SubscriptionInfo si = null;
//		try { 
//			si = Utils.authorize(repo, request, defaults);
//		}catch(SubscriptionIDException sidex) {
//			ApiSubError error = new ApiSubError(sidex.getMessage());
//			ApiError errors = new ApiError(HttpStatus.FORBIDDEN,request,sidex,sidex.getMessage(),error);
//			request.getSession().setAttribute("errorObj", errors);
//		}catch(NotAuthorizedException nae) {
//			ApiSubError error = new ApiSubError(nae.getMessage());
//			ApiError errors = new ApiError(HttpStatus.UNAUTHORIZED,request,nae,nae.getMessage(),error);
//			request.getSession().setAttribute("errorObj", errors);
//		}catch(Exception ex) {
//			ApiSubError error = new ApiSubError(ex.getMessage());
//			ApiError errors = new ApiError(HttpStatus.FORBIDDEN,request,ex,ex.getMessage(),error);
//			request.getSession().setAttribute("errorObj", errors);			
//		}
		
		filterChain.doFilter(request, response);
	}

}
