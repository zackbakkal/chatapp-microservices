package com.zack.projects.chatapp.notification.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private SenderRecipient senderRecipient;
    private String text;
    private Timestamp dateSent;
    private boolean isText;
    private boolean isImage;
    private boolean isFile;

}
