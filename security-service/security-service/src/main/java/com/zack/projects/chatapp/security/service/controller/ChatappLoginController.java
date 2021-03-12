package com.zack.projects.chatapp.security.service.controller;

import com.zack.projects.chatapp.security.service.Template.UserResponseTemplate;
import com.zack.projects.chatapp.security.service.Template.UserRequestTemplate;
import com.zack.projects.chatapp.security.service.entity.User;
import com.zack.projects.chatapp.security.service.service.ChatappLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping
@Slf4j
public class ChatappLoginController {

    @Autowired
    private ChatappLoginService chatappLoginService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/alive")
    public String alive() {
        return "SECURITY-SERVICE: (ok)";
    }

    // login
    @GetMapping
    public String getLoginView1() {

        return "login";

    }

    // login
    @GetMapping("login")
    public String getLoginView2() {

        return "login";

    }

    // login-error
    @GetMapping("login-error")
    public String getLoginErrorView(Model model) {

        model.addAttribute("loginError", true);
        return "login";

    }

    // Open registration form
    @GetMapping("register")
    public String getRegistrationFormView(Model model) {

        model.addAttribute("user", new User());
        return "register";

    }

    @PostMapping("register")
    public String registerUser(
            @ModelAttribute("user") UserRequestTemplate userRequestTemplate,
            final RedirectAttributes redirectAttributes,
            Model model) {

        boolean success = chatappLoginService.registerUser(userRequestTemplate);
        log.info(String.format("Creating response"));
        UserResponseTemplate user = new UserResponseTemplate(userRequestTemplate);
        user.setOnline(false);
        user.setAvailability("available");
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEnabled(true);
        if(success) {
            return "registersuccessful";
        }

        model.addAttribute("error", true);

        return "register";

    }

    @GetMapping("chatapp")
    public String getChatapp(Model model,
                             HttpServletRequest httpServletRequest) {

        UserResponseTemplate userResponseTemplate =
                restTemplate.getForObject(
                        "http://USER-SERVICE/users/" + httpServletRequest.getRemoteUser(),
                        UserResponseTemplate.class);


        model.addAttribute("user", userResponseTemplate);
        return "chatapp";
    }

    @GetMapping("logout")
    public String logout() {
        return getLoginView2();
    }

}
