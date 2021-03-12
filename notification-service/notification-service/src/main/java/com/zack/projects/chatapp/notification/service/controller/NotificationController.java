package com.zack.projects.chatapp.notification.service.controller;

import com.zack.projects.chatapp.notification.service.service.NotificationService;
import com.zack.projects.chatapp.notification.service.template.MessageNotificationRequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/alive")
    public String alive() {
        return "NOTIFICATION-SERVICE: (ok)";
    }

    @GetMapping("/subscribe/{username}")
    public SseEmitter subscribe(@PathVariable String username) {
        return notificationService.subscribeUser(username);
    }

    @PutMapping("/unsubscribe/{username}")
    public boolean unsubscribeUser(@PathVariable String username) {
        return notificationService.unsubscribeUser(username);
    }

    @PostMapping("/newMessage")
    public boolean deliverMessage(@RequestBody MessageNotificationRequestTemplate messageNotificationRequestTemplate) {
        return notificationService.deliverMessage(messageNotificationRequestTemplate);
    }

    @PostMapping("/deliverMissed")
    public void deliverMissedMessage(String recipient) {
        notificationService.deliverMissedMessages(recipient);
    }

    @PutMapping("/notifyUsers/{username}")
    public boolean updateUsersList(@PathVariable String username) {
        return notificationService.updateUsersList(username);
    }

    @PutMapping("/clear")
    public void notificationReceived(String sender, String recipient) {
        notificationService.notificationReceived(sender, recipient);
    }

}
