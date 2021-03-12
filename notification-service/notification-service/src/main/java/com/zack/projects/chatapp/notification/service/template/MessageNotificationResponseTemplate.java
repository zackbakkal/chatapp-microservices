package com.zack.projects.chatapp.notification.service.template;

import com.zack.projects.chatapp.notification.service.dto.SenderRecipient;

import java.sql.Timestamp;

public class MessageNotificationResponseTemplate {

    private SenderRecipient senderRecipient;
    private String text;
    private Timestamp dateSent;
    private boolean isText;
    private boolean isImage;
    private boolean isFile;

}
