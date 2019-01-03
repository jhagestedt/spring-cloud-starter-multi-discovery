package org.springframework.cloud.extension;

import static java.lang.Character.isUpperCase;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.extension.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
@Slf4j
@Endpoint(id = "service-registry")
public class MultiServiceRegistryEndpoint {

    private static final Comparator<ServiceRegistry> SERVICE_REGISTRY_COMPARATOR = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
        } else if (o1 != null) {
            return -1;
        } else {
            return 1;
        }
    };

    private final Map<String, ServiceRegistry> serviceRegistries;

    private List<Pair<String, Pair<Registration, ServiceRegistry>>> pairs = new LinkedList<>();

    public MultiServiceRegistryEndpoint(List<ServiceRegistry> serviceRegistries) {
        if (serviceRegistries != null) {
            this.serviceRegistries = serviceRegistries.stream()
                .sorted(SERVICE_REGISTRY_COMPARATOR)
                .collect(Collectors.toMap(
                    key -> discoveryServiceName(key.getClass()), value -> value,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                ));
        } else {
            this.serviceRegistries = new HashMap<>();
        }
    }

    static String discoveryServiceName(final Class<?> registryOrRegistration) {
        if (registryOrRegistration != null) {
            final String simpleName = registryOrRegistration.getSimpleName();
            final OptionalInt secondUpperCase = IntStream.range(1, simpleName.length())
                .filter(i -> isUpperCase(simpleName.charAt(i)))
                .findFirst();
            return simpleName.substring(0, secondUpperCase.orElse(simpleName.length())).toLowerCase();
        } else {
            return null;
        }
    }

    public void setRegistrations(List<Registration> registrations) {
        if (registrations != null) {
            registrations.forEach(registration -> {
                final String name = discoveryServiceName(registration.getClass());
                final ServiceRegistry<?> registry = this.serviceRegistries.get(name);
                if (registry != null) {
                    this.pairs.add(Pair.of(name, Pair.of(registration, registry)));
                } else {
                    log.warn("corresponding service registry for registration {} not found.", registration);
                }
            });
        }
    }

    @WriteOperation
    public ResponseEntity setStatus(String status) {
        Assert.notNull(status, "status may not by null");
        if (this.pairs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no registration found");
        }
        this.pairs.forEach(pair -> {
            final Registration registration = pair.getRight().getLeft();
            final ServiceRegistry serviceRegistry = pair.getRight().getRight();
            serviceRegistry.setStatus(registration, status);
        });
        return ResponseEntity.ok().build();
    }

    @ReadOperation
    public ResponseEntity getStatus() {
        if (this.pairs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no registration found");
        }
        return ResponseEntity.ok().body(
            pairs.stream()
                .map(pair -> pair.getLeft() + ": " + pair.getRight().getRight().getStatus(pair.getRight().getLeft()))
                .collect(Collectors.joining(","))
        );
    }

}