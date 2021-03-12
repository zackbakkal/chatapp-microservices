package com.zack.projects.chatapp.security.service.custom;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final String UNSUBSCRIBE_USER_URL = "http://NOTIFICATION-SERVICE/notifications/unsubscribe/";
    private final String NOTIFY_USERS_URL = "http://NOTIFICATION-SERVICE/notifications/notifyUsers/";
    private final String CHANGE_USER_STATUS_URL = "http://USER-SERVICE/users/status/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @SneakyThrows
    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        log.info("Create path variable to send with the put notification methods");

        String username = authentication.getName();
        Map< String, String > params = new HashMap< String, String >();
        params.put("username", authentication.getName());

        CircuitBreaker circuitBreaker;

        log.info(String.format("Set the user [%s] status to offline", username));
        circuitBreaker = circuitBreakerFactory.create("changeStatus");
        circuitBreaker
                .run(changeUserStatusSupplier(username, "offline"), t -> {
                    log.warn("Notifying users failed", t);
                    return false;
                });

        log.info(String.format("Logout successful with user [%s]", username));
        response.setStatus(HttpServletResponse.SC_OK);

        log.info(String.format("Calling the notification service to unsubscribe user [%s]", username));
        circuitBreaker = circuitBreakerFactory.create("unsubscribeUser");
        log.info(String.format("Unsubscribe user [%s] from emitters", username));
        circuitBreaker
                .run(unsubscribeUserSupplier(username), t -> {
                    log.warn("Unsubscribing user failed", t);
                    return false;
                });


        log.info("Calling the notification service to notify all users");
        circuitBreaker = circuitBreakerFactory.create("notifyUsers");
        log.info(String.format("Notify all users that user [%s] changed status to offline", username));
        circuitBreaker
                .run(notifyUsersSupplier(username), t -> {
                    log.warn("Notifying users failed", t);
                    return false;
                });

        log.info(String.format("Redirecting to login page"));
        response.sendRedirect("/login");

    }

    private Supplier<Boolean> changeUserStatusSupplier(String username, String status) {
        return () -> changeUserStatus(username, status);
    }

    private boolean changeUserStatus(String username, String status) {
        Map< String, String > params = new HashMap< String, String >();
        params.put("username", username);

        HttpEntity<Map> request = new HttpEntity<>(params);
        ResponseEntity<Boolean> response =
                restTemplate.exchange(CHANGE_USER_STATUS_URL + username
                        + "/" + status, HttpMethod.PUT, request, Boolean.class);

        return response.getBody();
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

    private Supplier<Boolean> unsubscribeUserSupplier(String username) {
        return () -> notifyUsersBy(username);
    }

    private boolean unsubscribeUser(String username) {
        Map< String, String > params = new HashMap< String, String >();
        params.put("username", username);

        HttpEntity<Map> request = new HttpEntity<>(params);
        ResponseEntity<Boolean> response =
                restTemplate.exchange(UNSUBSCRIBE_USER_URL + username, HttpMethod.PUT, request, Boolean.class);

        return response.getBody();
    }


}
