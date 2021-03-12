package com.zack.peojects.chatapp.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNameNotFoundException extends Exception{

    public UserNameNotFoundException(String message) {
        super(message);
    }

}
