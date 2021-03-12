package com.zack.projects.chatapp.security.service.entity;

import com.zack.projects.chatapp.security.service.Template.UserRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
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

    public User(UserRequestTemplate userRequestTemplate) {
        this.username = userRequestTemplate.getUsername();
        this.firstName = userRequestTemplate.getFirstName();
        this.lastName = userRequestTemplate.getLastName();
        this.email = userRequestTemplate.getEmail();
    }

}
