package com.zack.projects.chatapp.security.service.service;

import com.netflix.discovery.converters.Auto;
import com.zack.projects.chatapp.security.service.Repository.UserRepository;
import com.zack.projects.chatapp.security.service.Template.UserRequestTemplate;
import com.zack.projects.chatapp.security.service.Template.UserResponseTemplate;
import com.zack.projects.chatapp.security.service.entity.User;
import com.zack.projects.chatapp.security.service.exception.UserNameExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ChatappLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    public boolean registerUser(UserRequestTemplate userRequestTemplate) {
        try {

            log.info(String.format("Checking if username [%s] already exists", userRequestTemplate.getUsername()));
            boolean userNameExists = userRepository.existsById(userRequestTemplate.getUsername());

            if(userNameExists) {
                log.info(String.format("username [%s] is taken", userRequestTemplate.getUsername()));
                throw new UserNameExistsException(
                        String.format("Username [%s] is taken, please try a different username", userRequestTemplate.getUsername()));
            }

            log.info(String.format("username [%s] is available: ", userRequestTemplate.getUsername()));
            User user = new User(userRequestTemplate);

            log.info(String.format("Encoding password for user [%s]", userRequestTemplate.getUsername()));
            String encodedPassword = passwordEncoder.encode(userRequestTemplate.getPassword());
            user.setPassword(encodedPassword);

            log.info("Activating user account");
            activateUserAccount(user);

            log.info("Initializing user availability to available");
            initializeUserAvailability(user);

            log.info(String.format("Registering user: [%s]", user));
            userRepository.save(user);

            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private void activateUserAccount(User user) {
        log.info(String.format("Activating User [%s] account", user));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
    }

    private void initializeUserAvailability(User user) {
        user.setAvailability("available");
    }

}
