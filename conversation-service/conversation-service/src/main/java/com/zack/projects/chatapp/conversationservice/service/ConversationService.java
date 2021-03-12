package com.zack.projects.chatapp.conversationservice.service;

import com.zack.projects.chatapp.conversationservice.dto.Message;
import com.zack.projects.chatapp.conversationservice.entity.Conversation;
import com.zack.projects.chatapp.conversationservice.repository.ConversationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Supplier;

@Service
@Slf4j
public class ConversationService {

    String LOAD_MESSAGES_URL = "http://MESSAGE-SERVICE/messages/load/";

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    public Collection<Message> getConversation(String userA, String userB) {

        boolean userAIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userA, Boolean.class);

        boolean userBIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userB, Boolean.class);

        if(userAIsRegistered && userBIsRegistered) {
            Conversation conversation;
            Timestamp dateStarted = new Timestamp(System.currentTimeMillis());

            log.info(String.format("Retrieving conversation between [%s] and [%s]", userA, userB));
            conversation = conversationRepository.findConversationByConversationId(userA, userB);

            if(conversation != null) {
                return restTemplate.getForObject(
                        "http://MESSAGE-SERVICE/messages/load/" + userA + "/" + userB, List.class);
            }

            log.info(String.format
                    ("Conversation between [%s] and [%s] does not exist yet, " +
                            "creating one and returning an empty list of messages", userA, userB));

            conversation = new Conversation(userA, userB, dateStarted);
            conversationRepository.save(conversation);
            return new ArrayList<>();
        }

        return null;

    }

    public Collection<Message> getMessagesByNumber(String userA, String userB, int from, int to) {

        boolean userAIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userA, Boolean.class);

        boolean userBIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userB, Boolean.class);

        if(userAIsRegistered && userBIsRegistered) {
            Conversation conversation;
            Timestamp dateStarted = new Timestamp(System.currentTimeMillis());

            log.info(String.format("Retrieving conversation between [%s] and [%s]", userA, userB));
            conversation = conversationRepository.findConversationByConversationId(userA, userB);

            CircuitBreaker circuitBreaker;

            if(conversation != null) {
                circuitBreaker = circuitBreakerFactory.create("loadMessages");

                return circuitBreaker.run(loadMessagesSupplier(userA, userB, from, to), t -> {
                    log.warn("Notifying users failed", t);
                    return new ArrayList<>();
                });
            }

            log.info(String.format
                    ("Conversation between [%s] and [%s] does not exist yet, " +
                            "creating one and returning an empty list of messages", userA, userB));

            conversation = new Conversation(userA, userB, dateStarted);
            conversationRepository.save(conversation);
            return new ArrayList<>();
        }

        return null;

    }

    private Supplier<List<Message>> loadMessagesSupplier(String userA, String userB, int from, int to) {
        return () -> loadMessages(userA, userB, String.valueOf(from), String.valueOf(to));
    }

    private List<Message> loadMessages(String userA, String userB, String from, String to) {
        Map< String, String > params = new HashMap< String, String >();
        params.put("userA", userA);
        params.put("userB", userB);
        params.put("from", from);
        params.put("to", to);

        HttpEntity<Map> request = new HttpEntity<>(params);
        ResponseEntity<List> response =
                restTemplate.exchange(LOAD_MESSAGES_URL
                        + userA + "/" + userB + "/" + from + "/" + to, HttpMethod.GET, request, List.class);

        return response.getBody();
    }

}
