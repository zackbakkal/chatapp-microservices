package com.zack.projects.chatapp.message.service.controller;

import com.zack.projects.chatapp.message.service.exception.UserNameNotFoundException;
import com.zack.projects.chatapp.message.service.service.MessageService;
import com.zack.projects.chatapp.message.service.template.MessageRequestTemplate;
import com.zack.projects.chatapp.message.service.template.MessageResponseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/alive")
    public String alive() {
        return "MESSAGE-SERVICE: (ok)";
    }

    @PostMapping("/send")
    public MessageResponseTemplate sendMessage(@RequestBody MessageRequestTemplate messageRequestTemplate) throws UserNameNotFoundException {
        return messageService.sendMessage(messageRequestTemplate);
    }

    @GetMapping("/load/{userA}/{userB}")
    public List<MessageResponseTemplate> loadMessages(@PathVariable String userA, @PathVariable String userB) throws UserNameNotFoundException {
        return messageService.loadMessages(userA, userB);
    }

    @GetMapping("/load/{userA}/{userB}/{from}/{to}")
    public List<MessageResponseTemplate> loadMessagesFromTo(
            @PathVariable String userA,
            @PathVariable String userB,
            @PathVariable int from,
            @PathVariable int to) throws UserNameNotFoundException {
        return messageService.loadMessagesFromTo(userA, userB, from, to);
    }

}
