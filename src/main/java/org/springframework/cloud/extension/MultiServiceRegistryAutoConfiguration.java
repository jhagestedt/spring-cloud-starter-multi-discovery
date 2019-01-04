package org.springframework.cloud.extension;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({
    EurekaClientAutoConfiguration.class,
    ConsulServiceRegistryAutoConfiguration.class
})
@ConditionalOnProperty(value = "spring.cloud.service-registry.multi-registration.enabled", havingValue = "true")
public class MultiServiceRegistryAutoConfiguration {

    @ConditionalOnBean(ServiceRegistry.class)
    @ConditionalOnClass(Endpoint.class)
    protected class MultiServiceRegistryEndpointConfiguration {

        @Autowired(required = false)
        private List<Registration> registrations;

        @Bean
        public MultiServiceRegistryEndpoint serviceRegistryEndpoint(final List<ServiceRegistry> serviceRegistries) {
            MultiServiceRegistryEndpoint endpoint = new MultiServiceRegistryEndpoint(serviceRegistries);
            endpoint.setRegistrations(this.registrations);
            return endpoint;
        }
    }

}