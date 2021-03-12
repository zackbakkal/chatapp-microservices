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

 * Dependencies
	```xml
	    <dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
	    </dependency>
	```

### cloud-gateway

  Cloud gateway service is the loadbalancer that routes requests to the application services depending on the path used in the URL. This service also registers with the service registry.

  * Dependencies
	```xml
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-gateway</artifactId>
	    	</dependency>
	    	<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	    	</dependency>
	```

  * Routes
  	```yaml
		spring:
		  cloud:
		    gateway:
		      routes:
		        - id: USER-SERVICE
		          uri: lb://USER-SERVICE
			  predicates:
			    - Path=/users/**
			- id: CONVERSATION-SERVICE
		      	  uri: lb://CONVERSATION-SERVICE
		      	  predicates:
		            - Path=/conversations/**
		    	- id: MESSAGE-SERVICE
		      	  uri: lb://MESSAGE-SERVICE
		      	  predicates:
		            - Path=/messages/**
		    	- id: NOTIFICATION-SERVICE
		      	  uri: lb://NOTIFICATION-SERVICE
		      	  predicates:
		            - Path=/notifications/**
		    	- id: SECURITY-SERVICE
		      	  uri: lb://SECURITY-SERVICE
		      	  predicates:
		            - Path=/**,/css/**,/js/**
        ```
