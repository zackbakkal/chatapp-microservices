package com.zack.peojects.chatapp.userservice.service;

import com.zack.peojects.chatapp.userservice.template.UserResponseTemplate;
import com.zack.peojects.chatapp.userservice.exception.UserNameNotFoundException;
import com.zack.peojects.chatapp.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final String NOTIFY_USERS_URL = "http://NOTIFICATION-SERVICE/notifications/notifyUsers/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    public List<UserResponseTemplate> getAllUsers() {
        List<UserResponseTemplate> userResponseTemplates = new ArrayList<>();

        log.info((String.format("Retrieving all users")));
        userRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .forEach(user ->
                        userResponseTemplates.add(new UserResponseTemplate(user)));

        return userResponseTemplates;
    }

    public UserResponseTemplate getUser(String username) throws UserNameNotFoundException {

        log.info(String.format("Retrieving user [%s]", username));
        UserResponseTemplate userResponseTemplate = userRepository.findUserByUsername(username);

        if(userResponseTemplate != null) {
            return userResponseTemplate;
        }

        throw new UserNameNotFoundException(String.format("Username [%s] does not exist", username));
    }


    public boolean setUserOnline(String username) {
        log.info(String.format("Updating user [%s] online status to online", username));
        return userRepository.updateUserOnlineStatus(username, true) == 1;
    }

    public boolean setUserOffline(String username) {
        log.info(String.format("Updating user [%s] online status to offline", username));
        return userRepository.updateUserOnlineStatus(username, false) == 1;
    }

    public boolean setUserAvailability(String username, String availability) {
        log.info(String.format("Updating user [%s] availability to [%s]", username, availability));
        boolean availabilityChanged = userRepository.updateUserAvailability(username, availability) == 1;

        if(availabilityChanged) {
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("notifyUsers");
            log.info("Notify all users that user [%s] changed status to [%s]", username, availability);
            circuitBreaker
                    .run(notifyUsersSupplier(username), t -> {
                        log.warn("Notifying users failed", t);
                        return false;
                    });
        }

        return availabilityChanged;
    }

    private Supplier<Boolean> notifyUsersSupplier(String username) {
        return () -> notifyUsersBy(username);
    }

    private boolean notifyUsersBy(String username) {
        Map< String, String > params = new HashMap< String, String >();
        params.put("username", username);

        HttpEntity<Map> request = new HttpEntity<>(params);
        ResponseEntity<Boolean> response =
                restTemplate.exchange(NOTIFY_USERS_URL + username, HttpMethod.PUT, request, Boolean.class);

        return response.getBody();
    }

    public boolean userIsRegistered(String username) {
        log.info(String.format("Checking if user [%s] is registered", username));
        return userRepository.findUserByUsername(username) != null;
    }

    public List<UserResponseTemplate> searchUsersStartWith(String username) {

        log.info((String.format("Searching username [%s]", username)));
        return userRepository.findByUsernameStartingWith(username);

    }

}
