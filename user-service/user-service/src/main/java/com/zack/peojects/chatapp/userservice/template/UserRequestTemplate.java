package com.zack.peojects.chatapp.userservice.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestTemplate {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
