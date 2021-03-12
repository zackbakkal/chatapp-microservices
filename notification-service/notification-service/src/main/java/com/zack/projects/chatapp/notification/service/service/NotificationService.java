package com.zack.projects.chatapp.notification.service.service;

import com.zack.projects.chatapp.notification.service.dto.Message;
import com.zack.projects.chatapp.notification.service.dto.SenderRecipient;
import com.zack.projects.chatapp.notification.service.template.MessageNotificationRequestTemplate;
import com.zack.projects.chatapp.notification.service.template.UserResponseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    private Map<String, SseEmitter> sseEmitters = new HashMap<>();

    public static List<SenderRecipient> missedEmitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribeUser(String username) {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sendInitEvent(sseEmitter);

        sseEmitters.put(username, sseEmitter);

        sseEmitter.onTimeout(() -> sseEmitters.remove(sseEmitter));
        sseEmitter.onError((e) -> sseEmitters.remove(sseEmitter));

        log.info(String.format("Sending notifications to [%s]", username));
        deliverMissedMessages(username);

        return sseEmitter;
    }

    private void sendInitEvent(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean deliverMessage(MessageNotificationRequestTemplate messageNotificationRequestTemplate) {

        String sender = messageNotificationRequestTemplate.getMessage().getSenderRecipient().getSender();
        String recipient = messageNotificationRequestTemplate.getMessage().getSenderRecipient().getRecipient();
        Message message = messageNotificationRequestTemplate.getMessage();

        log.info(String.format("Retrieving recipient user [%s] emitter", recipient));
        SseEmitter sseEmitter = sseEmitters.get(recipient);

        log.info(String.format("Calling user service to retrieve user [%s]", recipient));
        UserResponseTemplate recipientUser = restTemplate.getForObject("http://USER-SERVICE/users/" + recipient,
                UserResponseTemplate.class);

        boolean recipientIsOnline = recipientUser.isOnline();

        log.info(String.format("Checking if user [%s] is online and has an emitter", recipient));
        if(sseEmitter != null && recipientIsOnline) {
            log.info(String.format("User [%s] is online and has an emitter", recipient));
            try {
                log.info(String.format("Sending new message notification to user [%s]", recipient));
                sseEmitter.send(SseEmitter.event().name("newMessage").data(message));
                return true;
            } catch (IOException e) {
                log.info(String.format("Unable to send new message notification to user [%s]", recipient));
                sseEmitters.remove(sseEmitter);
                return false;
            }
        }

        log.info(String.format("User [%s] is offline, saving the notification for later", recipient));
        SenderRecipient senderRecipient =
                new SenderRecipient(sender, recipient);

        missedEmitters.add(senderRecipient);

        return true;

    }

    public void deliverMissedMessages(String recipient) {

        log.info(String.format("Retrieving list of missed Emitters"));
        List<SenderRecipient> senderRecipients = missedEmitters.stream()
                .filter(senderRecipient ->
                        senderRecipient.getRecipient().equals(recipient))
                .collect(Collectors.toList());

        if(!senderRecipients.isEmpty()) {

            log.info(String.format("Retrieving the emitter for [%s]", recipient));
            SseEmitter sseEmitter = sseEmitters.get(recipient);

            log.info(String.format("Sending notifications to [%s] from [%s]", recipient, senderRecipients.size()));
            senderRecipients.forEach(senderRecipient -> {

                try {
                    sseEmitter.send(SseEmitter.event().name("missedMessages").data(senderRecipient));
                    log.info(String.format("Notifications from [%s] sent successful", senderRecipient.getSender()));
                } catch (IOException e) {
                    log.info(String.format("Notifications from [%s] failed", senderRecipient.getSender()));
                    sseEmitters.remove(sseEmitter);
                }
            });

        }

    }

    public void notificationReceived(String sender, String recipient) {

        SenderRecipient senderRecipient = new SenderRecipient(sender, recipient);

        log.info(String.format("removing notifications from [%s]", sender));
        missedEmitters.removeAll(Collections.singleton(senderRecipient));

    }

    public boolean updateUsersList(String username) {

        Collection<SseEmitter> sseEmittersValues = sseEmitters.values();

        log.info(String.format("Calling user service to retrieve user [%s]", username));
        UserResponseTemplate userResponseTemplate =
                restTemplate.getForObject("http://USER-SERVICE/users/" + username, UserResponseTemplate.class);

        log.info(String.format("Sending status update notifications to all users from [%s]", username));
        sseEmittersValues
                .forEach(sseEmitter ->
                {
                    try {
                        sseEmitter
                                .send(SseEmitter
                                        .event()
                                        .name("updateUsersList").data(userResponseTemplate));
                    } catch (IOException e) {
                        sseEmitters.remove(sseEmitter);
                    }
                });

        return true;
    }

    public boolean unsubscribeUser(String username) {
        log.info(String.format("Removing user [%s] from sse Emitters", username));
        return sseEmitters.remove(username) != null;
    }
}

