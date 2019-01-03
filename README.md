# spring-cloud-starter-multi-discovery

If you want to connect from one spring boot service
to an other this little library is what you searched for.

It allows you to connect for example to Eureka and Consul
at one time.

To use this library just check source out and build it 
with `mvn clean install` that you have the dependency in your
maven repository.

In the microservice that should connect to both discoveries 
just add the dependency.

```xml
<dependency>
  <groupId>org.springframework.cloud.extension</groupId>
  <artifactId>spring-cloud-starter-multi-discovery</artifactId>
  <version>0.0.1</version>
</dependency>
```

After this you have to exclude spring clouds single service registry
auto configuration and add the property
`spring.cloud.service-registry.auto-multi-registration` with value 
`true`.

```yaml
spring:
  autoconfigure:
    # important
    exclude:
    - org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration
    - org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration
  cloud:
    service-registry:
      auto-registration:
        enabled: true
      # important  
      auto-multi-registration:
        enabled: true
        
    # default consul configurations
    consul:
      host: localhost
      port: 8500
      
# default eureka configuration
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8762/eureka
```