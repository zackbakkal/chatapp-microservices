package com.zack.projects.chatapp.conversationservice.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationRequestTemplate {

    private String userA;
    private String userB;

}
