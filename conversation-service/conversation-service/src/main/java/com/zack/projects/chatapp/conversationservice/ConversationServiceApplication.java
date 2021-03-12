package com.zack.projects.chatapp.conversationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ConversationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConversationServiceApplication.class, args);
	}

}
