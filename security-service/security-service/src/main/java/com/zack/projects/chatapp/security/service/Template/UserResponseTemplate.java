package com.zack.projects.chatapp.security.service.Template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseTemplate {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isOnline;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private String availability;
    private String profileImageUrl;

    public UserResponseTemplate(UserRequestTemplate userRequestTemplate) {
        this.username = userRequestTemplate.getUsername();
        this.firstName = userRequestTemplate.getFirstName();
        this.lastName = userRequestTemplate.getLastName();
        this.email = userRequestTemplate.getEmail();
    }

}
