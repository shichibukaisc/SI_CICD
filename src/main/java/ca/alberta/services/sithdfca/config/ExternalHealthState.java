package ca.alberta.services.sithdfca.config;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class ExternalHealthState {

    private final AtomicBoolean healthy = new AtomicBoolean(true);

    public boolean isHealthy() {
        return this.healthy.get();
    }

    public void setUnhealthy() {
        this.healthy.set(false);
    }
}
