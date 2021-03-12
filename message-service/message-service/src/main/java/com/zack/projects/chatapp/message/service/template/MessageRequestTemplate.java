package com.zack.projects.chatapp.message.service.template;

import com.zack.projects.chatapp.message.service.dto.SenderRecipient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestTemplate {

    private SenderRecipient senderRecipient;
    private String text;

}
