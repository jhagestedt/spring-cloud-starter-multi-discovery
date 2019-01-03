package org.springframework.cloud.extension;

import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AutoMultiServiceRegistrationConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-multi-registration.enabled", havingValue = "true")
public class AutoMultiServiceRegistrationAutoConfiguration {

    @Autowired
    private AutoServiceRegistrationProperties properties;

    @Autowired(required = false)
    private List<AutoServiceRegistration> autoServiceRegistrations;

    @PostConstruct
    protected void init() {
        if ((autoServiceRegistrations == null || autoServiceRegistrations.isEmpty()) && properties.isFailFast()) {
            throw new IllegalStateException("No AutoServiceRegistration bean.");
        }
    }
}