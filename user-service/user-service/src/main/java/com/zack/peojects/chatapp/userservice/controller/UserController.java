package com.zack.peojects.chatapp.userservice.controller;

import com.zack.peojects.chatapp.userservice.template.UserRequestTemplate;
import com.zack.peojects.chatapp.userservice.template.UserResponseTemplate;
import com.zack.peojects.chatapp.userservice.exception.UserNameExistsException;
import com.zack.peojects.chatapp.userservice.exception.UserNameNotFoundException;
import com.zack.peojects.chatapp.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping("/alive")
    public String alive() {
        return "USER-SERVICE: (ok)";
    }

    @GetMapping("/all")
    public List<UserResponseTemplate> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public UserResponseTemplate getUser(@PathVariable String username) throws UserNameNotFoundException {
        return userService.getUser(username);
    }

    @PutMapping("/status/{username}/online")
    public boolean setUserOnline(@PathVariable String username)
            throws UserNameNotFoundException {
        return userService.setUserOnline(username);
    }

    @PutMapping("/status/{username}/offline")
    public boolean setUserOffline(@PathVariable String username) {
        return userService.setUserOffline(username);
    }

    @PutMapping("/availability/{username}/{availability}")
    public boolean updateUserAvailability(@PathVariable String username, @PathVariable String availability) {
        return userService.setUserAvailability(username, availability);
    }

    @GetMapping("/registered/{username}")
    public boolean userIsRegistered(@PathVariable String username) {
        return userService.userIsRegistered(username);
    }

    @GetMapping("/startwith/{username}")
    public List<UserResponseTemplate> searchUsersStartWith(@PathVariable String username) {
        return userService.searchUsersStartWith(username);
    }

}
