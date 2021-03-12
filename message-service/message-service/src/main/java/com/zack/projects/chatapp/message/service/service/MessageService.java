package com.zack.projects.chatapp.message.service.service;

import com.zack.projects.chatapp.message.service.template.UserResponseTemplate;
import com.zack.projects.chatapp.message.service.entity.Message;
import com.zack.projects.chatapp.message.service.exception.UserNameNotFoundException;
import com.zack.projects.chatapp.message.service.repository.MessageRepository;
import com.zack.projects.chatapp.message.service.template.MessageNotificationRequestTemplate;
import com.zack.projects.chatapp.message.service.template.MessageRequestTemplate;
import com.zack.projects.chatapp.message.service.template.MessageResponseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MessageService {

    private final int MAX_MESSAGES = 10;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RestTemplate restTemplate;

    public MessageResponseTemplate sendMessage(MessageRequestTemplate messageRequestTemplate) throws UserNameNotFoundException {

        String sender = messageRequestTemplate.getSenderRecipient().getSender();
        String recipient = messageRequestTemplate.getSenderRecipient().getRecipient();

        log.info(String.format("Checking if users [%s] and [%s] exist", sender, recipient));
        boolean userAIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + sender, Boolean.class);

        boolean userBIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + recipient, Boolean.class);

        if(userAIsRegistered && userBIsRegistered) {
            log.info("Both users exist");

            log.info(String.format("Retrieving the recipient user [%s]", recipient));
            UserResponseTemplate recipientUserResponseTemplate =
                    restTemplate.getForObject("http://USER-SERVICE/users/" + recipient, UserResponseTemplate.class);

            log.info(String.format("Creating new message with now's date"));
            Message message = new Message(messageRequestTemplate);

            log.info("Saving message");
            messageRepository.save(message);

            // TODO: rest call to notification service to notify the recipient
            MessageNotificationRequestTemplate messageNotificationRequestTemplate =
                    new MessageNotificationRequestTemplate();
            messageNotificationRequestTemplate.setMessage(message);

            boolean isRecipientOnline = recipientUserResponseTemplate.isOnline();
            messageNotificationRequestTemplate.setRecipientIsOnline(isRecipientOnline);

            log.info(String.format("Calling nothification service to notify user [%s] has a new message", recipient));
            restTemplate
                    .postForObject(
                            "http://NOTIFICATION-SERVICE/notifications/newMessage",
                            messageNotificationRequestTemplate,
                            boolean.class);

            return new MessageResponseTemplate(message);
        }

        log.info("One or both users do not exist, throwing an exception");
        StringBuilder message = new StringBuilder();
        message.append(!userAIsRegistered ? String.format("User [%s] does not exist\n", sender).toString() : "");
        message.append(!userBIsRegistered ? String.format("User [%s] does not exist\n", recipient).toString() : "");

        throw new UserNameNotFoundException(message.toString());

    }

    public List<MessageResponseTemplate> loadMessages(String userA, String userB) throws UserNameNotFoundException {

        log.info(String.format("Checking if users [%s] and [%s] exist", userA, userB));
        boolean userAIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userA, Boolean.class);

        boolean userBIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userB, Boolean.class);

        if(userAIsRegistered && userBIsRegistered) {
            log.info("Both users exist");

            log.info(String.format("Loading messages between users [%s] and [%s]", userA, userB));
            List<MessageResponseTemplate> messages = messageRepository.findAllBySenderRecipient(userA, userB);

            int from = Math.max(messages.size() - MAX_MESSAGES, 0);

            return messages.subList(from, messages.size());
        }

        log.info("One or both users do not exist, throwing an exception");
        StringBuilder message = new StringBuilder();
        message.append(!userAIsRegistered ? String.format("User [%s] does not exist\n", userA).toString() : "");
        message.append(!userBIsRegistered ? String.format("User [%s] does not exist\n", userB).toString() : "");

        throw new UserNameNotFoundException(message.toString());

    }

    public List<MessageResponseTemplate> loadMessagesFromTo(String userA, String userB, int from, int to)
            throws UserNameNotFoundException {

        log.info("Checking if the messages indexes requested are in range");
        if(from >= 0 && from <= to) {
            boolean userAIsRegistered =
                    restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userA, Boolean.class);

            boolean userBIsRegistered =
                    restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userB, Boolean.class);

            if(userAIsRegistered && userBIsRegistered) {
                log.info("Both users exist");

                log.info(String.format("Loading messages between users [%s] and [%s]", userA, userB));
                List<MessageResponseTemplate> messages = messageRepository.findAllBySenderRecipient(userA, userB);

                if(from <= messages.size()) {
                    to = Math.min(to, messages.size());

                    log.info("Retrieving a sublist of messages in the requested range");
                    return messageRepository
                            .findAllBySenderRecipient(userA, userB)
                            .subList(messages.size() - to, messages.size() - from);
                }

                log.info("No more messages to retrieve");
                return new ArrayList<>();
            }

            log.info("One or both users do not exist, throwing an exception");
            StringBuilder message = new StringBuilder();
            message.append(!userAIsRegistered ? String.format("User [%s] does not exist\n", userA).toString() : "");
            message.append(!userBIsRegistered ? String.format("User [%s] does not exist\n", userB).toString() : "");

            throw new UserNameNotFoundException(message.toString());
        }

        return new ArrayList<>();
    }
}
