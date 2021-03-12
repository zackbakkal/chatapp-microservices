package com.zack.projects.chatapp.message.service.template;

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
    private String password;
    private boolean isOnline;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private String availability;
    private String profileImageName;

}
