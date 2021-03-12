package com.zack.projects.chatapp.notification.service.template;

import com.zack.projects.chatapp.notification.service.dto.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageNotificationRequestTemplate {

    private Message message;
    private boolean isRecipientOnline;

}
