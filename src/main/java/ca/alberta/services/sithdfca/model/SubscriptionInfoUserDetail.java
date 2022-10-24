package ca.alberta.services.sithdfca.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubscriptionInfoUserDetail implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8069736189757745992L;
	private SubscriptionInfo subInfo;
	private String userName;
	private List<GrantedAuthority> roles;
	private boolean active;
	
	public SubscriptionInfoUserDetail(SubscriptionInfo subInfo) {
		this.subInfo = subInfo;
		userName = subInfo.getClientId();
		//password = "{noop}test";
		//roles = null;
		//if we get a comma separated string we could...
		if(subInfo.getRoles() != null) {
			roles = Stream.of(subInfo.getRoles().split(",")).map(e-> new SimpleGrantedAuthority("ROLE_"+e)).collect(Collectors.toList());
		}
		active = true;
	}
	
	public SubscriptionInfoUserDetail(SubscriptionInfo subInfo, List<GrantedAuthority> roles) {
		this.subInfo = subInfo;
		userName = subInfo.getClientId();
		//password = "{noop}test";
		//roles = null;
		//if we get a comma separated string we could...
		this.roles = roles;
		active = true;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return active;
	}

	@Override
	public boolean isAccountNonLocked() {
		return active;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return active;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}
	
	public SubscriptionInfo getSubscriptionInfo() {
		return subInfo;
	}

}
