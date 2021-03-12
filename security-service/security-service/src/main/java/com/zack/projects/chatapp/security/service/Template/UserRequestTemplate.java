package com.zack.projects.chatapp.security.service.Template;

import com.zack.projects.chatapp.security.service.entity.User;
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

    public UserRequestTemplate(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

}
