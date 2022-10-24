package ca.alberta.services.sithdfca.converters;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import ca.alberta.services.sithdfca.service.LoginUserDetailService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class UserDetailsBaseGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
	
	@Autowired
	LoginUserDetailService userDetailsService;
	
	/**
	 * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
	 * 
	 * @param jwt The {@link Jwt} token
	 * @return The {@link GrantedAuthority authorities} read from the token scopes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		log.debug("converting JWT to granted Authorities");
		UserDetails ud = userDetailsService.loadUserByJwt(jwt);
		Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>)ud.getAuthorities();
//		for (String authority : getAuthorities(jwt)) {
//			grantedAuthorities.add(new SimpleGrantedAuthority(this.authorityPrefix + authority));
//		}
		return grantedAuthorities;
	}

//	/**
//	 * For this approach, we will be using the jwt to retrieve the userdetails.  The user details contain the application specific granted authorities
//	 * 
//	 * @param jwt
//	 * @return
//	 */
	
//	private Collection<String> getAuthorities(Jwt jwt) {
//		
//		return Collections.emptyList();
//	}

//	@SuppressWarnings("unchecked")
//		private Collection<String> castAuthoritiesToCollection(Object authorities) {
//			return (Collection<String>) authorities;
//		}
}
