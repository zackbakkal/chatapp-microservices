package com.zack.projects.chatapp.message.service.template;

import com.zack.projects.chatapp.message.service.dto.SenderRecipient;
import com.zack.projects.chatapp.message.service.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseTemplate {

    private SenderRecipient senderRecipient;
    private String text;
    private Timestamp dateSent;
    private boolean isImage;
    private boolean isFile;

    public MessageResponseTemplate(Message message) {
        this.senderRecipient = message.getSenderRecipient();
        this.text = message.getText();
        this.dateSent = message.getDateSent();
        this.setImage(message.isImage());
        this.setFile(message.isFile());
    }

}
