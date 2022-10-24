package ca.alberta.services.sithdfca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExternalHealthIndicator implements HealthIndicator {

	@Autowired
	private ExternalHealthState state;
	
	@Override
	public Health health() {
		return Health.status(state.isHealthy() ? Status.UP : Status.DOWN).build();
    }
}
