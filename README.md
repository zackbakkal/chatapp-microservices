# Chat Application Using Spring Boot

In this project I am using microservices, so that any new function could be added as a new service without affecting other services.

## Services

  * service-registry
  * cloud-gateway
  * security-service
  * user-service
  * conversation-service
  * message-service
  * notification-service
  
### service-registry

Service registry is the Netflix Eureka service registry, which registers all other services used in the application.

    #### dependencies
	```xml
	    <dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
	    </dependency>
	```

### cloud-gateway

Cloud gateway service 
