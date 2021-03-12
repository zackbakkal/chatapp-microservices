package com.zack.projects.chatapp.conversationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationId implements Serializable {

    private String userA;
    private String userB;

}
