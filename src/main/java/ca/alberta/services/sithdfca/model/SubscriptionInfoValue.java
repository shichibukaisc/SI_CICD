package ca.alberta.services.sithdfca.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="SUBSCRIPTION_INFO_VALUES")
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class SubscriptionInfoValue {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false,name="SUBSCRIPTION_INFO_ID", insertable=false, updatable=false)
	private Long subscriptionInfoId;
	
	@Column(nullable=false,name="KEY")
	private String key;
	
	@Column(nullable=false,name="VALUE")
	private String value;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_INFO_ID")
    @JsonIgnore
    private SubscriptionInfo subscriptionInfo;
	
}
