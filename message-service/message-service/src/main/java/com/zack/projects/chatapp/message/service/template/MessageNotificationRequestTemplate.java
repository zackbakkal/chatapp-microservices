package com.zack.projects.chatapp.message.service.template;

import com.zack.projects.chatapp.message.service.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotificationRequestTemplate {

    private Message message;
    private boolean recipientIsOnline;

}
