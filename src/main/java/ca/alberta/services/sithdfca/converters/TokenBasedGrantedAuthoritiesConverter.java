package ca.alberta.services.sithdfca.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class TokenBasedGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	private final Log logger = LogFactory.getLog(getClass());

	private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

	private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("resource_access");

	private String authorityPrefix = DEFAULT_ROLE_PREFIX;

	private String authoritiesClaimName;
	
	private String authoritiesAudienceName;

	/**
	 * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
	 * 
	 * @param jwt The {@link Jwt} token
	 * @return The {@link GrantedAuthority authorities} read from the token scopes
	 */
	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for (String authority : getAuthorities(jwt)) {
			grantedAuthorities.add(new SimpleGrantedAuthority(this.authorityPrefix + authority));
		}
		return grantedAuthorities;
	}

	/**
	 * Sets the prefix to use for {@link GrantedAuthority authorities} mapped by
	 * this converter. Defaults to
	 * {@link JwtGrantedAuthoritiesConverter#DEFAULT_AUTHORITY_PREFIX}.
	 * 
	 * @param authorityPrefix The authority prefix
	 * @since 5.2
	 */
	public void setAuthorityPrefix(String authorityPrefix) {
		Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
		this.authorityPrefix = authorityPrefix;
	}

	/**
	 * Sets the name of token claim to use for mapping {@link GrantedAuthority
	 * authorities} by this converter. Defaults to
	 * {@link JwtGrantedAuthoritiesConverter#WELL_KNOWN_AUTHORITIES_CLAIM_NAMES}.
	 * 
	 * @param authoritiesClaimName The token claim name to map authorities
	 * @since 5.2
	 */
	public void setAuthoritiesClaimName(String authoritiesClaimName) {
		Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
		this.authoritiesClaimName = authoritiesClaimName;
	}
	public void setAuthoritiesAudienceName(String authoritiesAudienceName) {
		Assert.hasText(authoritiesAudienceName, "authoritiesAudienceName cannot be empty");
		this.authoritiesAudienceName = authoritiesAudienceName;
	}

	private String getAuthoritiesClaimName(Jwt jwt) {
		if (this.authoritiesClaimName != null) {
			return this.authoritiesClaimName;
		}
		for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
			if (jwt.hasClaim(claimName)) {
				return claimName;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Collection<String> getAuthorities(Jwt jwt) {
		String claimName = getAuthoritiesClaimName(jwt);
		if (claimName == null) {
			this.logger.trace("Returning no authorities since could not find any claims that might contain scopes");
			return Collections.emptyList();
		}
		if (this.logger.isTraceEnabled()) {
			this.logger.trace(LogMessage.format("Looking for scopes in claim %s", claimName));
		}
		Map<String, Object> authoritiesMap = jwt.getClaimAsMap(claimName);
		Map<String,Object> c = (Map<String,Object>)authoritiesMap.get(authoritiesAudienceName);

		Object authorities = c.get("roles");
		authorities = c.get("roles");
		if (authorities instanceof String) {
			if (StringUtils.hasText((String) authorities)) {
				return Arrays.asList(((String) authorities).split(" "));
			}
			return Collections.emptyList();
		}
		if (authorities instanceof Collection) {
			return castAuthoritiesToCollection(authorities);
		}
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
		private Collection<String> castAuthoritiesToCollection(Object authorities) {
			return (Collection<String>) authorities;
		}
}
