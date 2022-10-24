package ca.alberta.services.sithdfca.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.alberta.services.sithdfca.model.SubscriptionInfo;

public interface SubscriptionInfoJpaRepository extends JpaRepository<SubscriptionInfo, Long>{
	
	public SubscriptionInfo getByClientId(String clientId);
	Optional<SubscriptionInfo> findByClientId(String clientId);
	
	public SubscriptionInfo getByClientIdAndAudience(String clientId, String audience);
	Optional<SubscriptionInfo> findByClientIdAndAudience(String clientId, String audience);
	
	public SubscriptionInfo getById(Long subscriptionId);
	Optional<SubscriptionInfo> findById(Long subscriptionId);

}