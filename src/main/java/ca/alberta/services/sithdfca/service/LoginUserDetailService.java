package ca.alberta.services.sithdfca.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import ca.alberta.services.sithdfca.Constants;
import ca.alberta.services.sithdfca.DataLoader;
import ca.alberta.services.sithdfca.config.Defaults;
import ca.alberta.services.sithdfca.exception.SubscriptionIDException;
import ca.alberta.services.sithdfca.model.SubscriptionInfo;
import ca.alberta.services.sithdfca.model.SubscriptionInfoUserDetail;
import ca.alberta.services.sithdfca.repositories.SubscriptionInfoJpaRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Service
@Slf4j
public class LoginUserDetailService implements UserDetailsService {

	@Autowired
	DataLoader dl;
	
	@Autowired
	private final SubscriptionInfoJpaRepository repo;
	
	@Autowired
	Defaults defaults;
	
//	@Autowired
//	Authentication auth;
		
	@Override
	public SubscriptionInfoUserDetail loadUserByUsername(String clientId) throws SubscriptionIDException {
		final SubscriptionInfo subInfo = repo.findByClientId(clientId).orElseThrow(() -> new SubscriptionIDException("User Not Found"));
		return new SubscriptionInfoUserDetail(subInfo);
	}

//	public SubscriptionInfoUserDetail loadUserByUsernameAndAudience(String username, String audience) throws UsernameNotFoundException {
//		final SubscriptionInfo subInfo = repo.findByClientIdAndAudience(username,audience).orElseThrow(() -> new RuntimeException("User Not Found"));
//		return new SubscriptionInfoUserDetail(subInfo);
//	}
	
//	public SubscriptionInfoUserDetail loadUserBySubcriptionInfo(SubscriptionInfo subInfo) {
//		SubscriptionInfoUserDetail siud = null;
//		if(defaults.getAuthorizationMethod().equals(Constants.AUTHORIZATION_METHOD_ACCESS_TOKEN)) {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			siud = loadUserBySubcriptionInfo(subInfo, auth);
//		}else{
//			siud = new SubscriptionInfoUserDetail(subInfo);
//		}
//		return siud;
//	}
//	
	private SubscriptionInfoUserDetail loadUserBySubcriptionInfo(SubscriptionInfo subInfo, Authentication auth) {
		List<GrantedAuthority> ga = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		return new SubscriptionInfoUserDetail(subInfo, ga);
	}
	
	/**
	 * This method is used when loading the SubscriptionInfoUserDetail when creating the Authentication object.
	 * If the user doesn't exixt, there's a chance they were recently added.  On first fail, we should try to load them into the repo
	 * And then try again.
	 * @param jwt
	 * @return
	 * @throws UsernameNotFoundException
	 */
	
	public SubscriptionInfoUserDetail loadUserByJwt(Jwt jwt) throws UsernameNotFoundException {
		String username = jwt.getClaimAsString("azp");
		String audience = defaults.getSubscriptionAudience();
		SubscriptionInfo subInfo = null;
		if(!repo.findByClientIdAndAudience(username,audience).isPresent()) {
			//We didn't find them.  Were they recently added?
			try {
				dl.checkForFile(username);
				subInfo = repo.findByClientIdAndAudience(username,audience).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
			}catch(IOException ioe) {
				log.warn("Unable to load clientID " + username);
			}
		}else {
			subInfo = repo.findByClientIdAndAudience(username,audience).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		}
		
		return new SubscriptionInfoUserDetail(subInfo);
	}
	
	public SubscriptionInfoUserDetail loadUserByAuthentication(Authentication auth) throws SubscriptionIDException {
		Jwt jwt = (Jwt)auth.getCredentials();
		String username = jwt.getClaimAsString("azp");
		String audience = defaults.getSubscriptionAudience();
		//final SubscriptionInfo subInfo = repo.findByClientIdAndAudience(username,audience).orElseThrow(() -> new SubscriptionIDException("User Not Found"));
		SubscriptionInfo subInfo = null;
		if(!repo.findByClientIdAndAudience(username,audience).isPresent()) {
			//We didn't find them.  Were they recently added?
			log.debug("clientId not found.  Checking if was recently added..");
			try {
				dl.checkForFile(username);
				subInfo = repo.findByClientIdAndAudience(username,audience).orElseThrow(() -> new SubscriptionIDException("User Not Found"));
			}catch(IOException ioe) {
				log.warn("Unable to load clientID " + username);
			}
		}else {
			subInfo = repo.findByClientIdAndAudience(username,audience).orElseThrow(() -> new SubscriptionIDException("User Not Found"));
		}
		
		return getSIUD(subInfo,auth);
	}
	
	public List<SubscriptionInfo> getAllSubscriptions() {
		return repo.findAll();
	}
	
	public List<SubscriptionInfoUserDetail> loadAllSubscriptions() {
		List<SubscriptionInfo> all = repo.findAll();
		List<SubscriptionInfoUserDetail> allDetails = new ArrayList<SubscriptionInfoUserDetail>();
		//all.stream().forEach(si -> allDetails.add(new SubscriptionInfoUserDetail(si)));
		all.stream().forEach(si -> allDetails.add(getSIUD(si)));		
		return allDetails;
	}
	
	public SubscriptionInfoUserDetail loadUserBySubscriptionId(Long subscriptionId) throws SubscriptionIDException {
		final SubscriptionInfo subInfo = repo.findById(subscriptionId).orElseThrow(() -> new SubscriptionIDException("SubscriptionID " + subscriptionId + " not found"));
		//return new SubscriptionInfoUserDetail(subInfo);
		return getSIUD(subInfo);
	}
	
	private SubscriptionInfoUserDetail getSIUD(SubscriptionInfo si) {
		return getSIUD(si, null);
	}
	
	private SubscriptionInfoUserDetail getSIUD(SubscriptionInfo si, Authentication auth) {
		SubscriptionInfoUserDetail siud = null;
		if(auth == null) {
			//No auth passed, get it from the SecurityContextHolder
			auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth == null) throw new RuntimeException("No Authoriaztion Object present in LoginUserDetailService");
		}
		Jwt jwt = null;
		try {
			jwt = (Jwt)auth.getCredentials();
		}catch (Exception e) {
			log.warn("Failed to cast auth.getCredentials() to jwt.");
		}
		if(jwt != null) {
			String clientId = jwt.getClaim("azp");
			if(defaults.getAuthorizationStrategy().equals(Constants.AUTHORIZATION_STRATEGY_ACCESS_TOKEN) && clientId != null && clientId.equalsIgnoreCase(si.getClientId())) {
			//We are using the authority of the token
			siud = loadUserBySubcriptionInfo(si, auth);
			}else{
				siud = new SubscriptionInfoUserDetail(si);
			}
		}else {
			siud = new SubscriptionInfoUserDetail(si);
		}
		return siud;
	}
	
}
