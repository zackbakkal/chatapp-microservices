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

### security-service
  This service is used to authenticate users and register new users. When a user first opens the chat application, a login page is provided. In this login page the user can either login or create a new account then login. This service also registers with the service registry.
  ![loginpage](https://user-images.githubusercontent.com/47879637/110915035-68ed6c80-82d4-11eb-8f91-c8230cf700f8.PNG)
  
  * Dependencies
	```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
	```
	
### user-service
  The user service handles REST API request to retrieve user(s) info and status (e.g online, offline) , availability (e.g busy, away, etc...), as well as updating these info. Each user has a username that is unique.

### conversation-service
  The conversation service handles REST API requests to retrieve conversation between two users, and create new conversation for first contact.
  
### message-service
  The message service handles REST API requests to send and retrieve messages sent between users in a conversation.
  
### notification-service
  This service is used to notify users of new messages, and of other user's status and availability.

	
