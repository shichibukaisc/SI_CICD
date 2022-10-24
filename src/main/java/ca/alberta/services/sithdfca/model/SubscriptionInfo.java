package ca.alberta.services.sithdfca.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "SUBSCRIPTION_INFO",
	uniqueConstraints = { @UniqueConstraint(name="UniqueClientIdAndAudience", columnNames={"CLIENT_ID", "AUDIENCE"}) }
)
@Data @NoArgsConstructor @AllArgsConstructor @Builder(access = AccessLevel.PUBLIC)
public class SubscriptionInfo {
	
	@Id
	@Column(nullable=false, name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, name="CLIENT_ID")
	private String clientId;
	@Column(nullable=false, name="AUDIENCE")
	private String audience;
	@Column(nullable=true, name="SECRET")
	private String secret;
	@Column(nullable=true, name="ROLES")
	private String roles;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="subscriptionInfoId")
	private List<SubscriptionInfoValue> subscriptionInfoValues;
		
	/**
	 * Here we will add Service specific getters for the Subscription Info Values
	 */
	
	private String getKeyValue(String keyToFind) {
		for(SubscriptionInfoValue v:subscriptionInfoValues) {
			if(v.getKey().equalsIgnoreCase(keyToFind))
				 return v.getValue();
		}
		return null;
	}
	
	/**
	 * This allows us to easily access the service specific metadata associated
	 * with the JWT token.  For example:
	 * PaymentAPI token uses 'getMerchantId()' here to retrieve the merchantId value from the SubscriptionInfoValue map
	 * @JsonIgnore is used so the SubscriptionInfo object doesn't have these values added at the root level
	 */

	@JsonIgnore
	public String getCustomValue1() {
		return getKeyValue("custom.value.1");
	}
	
	@JsonIgnore
	public String getCustomValue2() {
		return getKeyValue("custom.value.2");
	}
	
	
}
