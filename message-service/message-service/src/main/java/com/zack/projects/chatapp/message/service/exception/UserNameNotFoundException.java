package com.zack.projects.chatapp.message.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNameNotFoundException extends Exception{

    public UserNameNotFoundException(String message) {
        super(message);
    }

}
