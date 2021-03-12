package com.zack.projects.chatapp.message.service.entity;

import com.zack.projects.chatapp.message.service.dto.SenderRecipient;
import com.zack.projects.chatapp.message.service.template.MessageRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "messages")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long messageId;

    private SenderRecipient senderRecipient;
    private String text;
    private Timestamp dateSent;
    private boolean isFile;
    private boolean isImage;

    public Message(MessageRequestTemplate messageRequestTemplate) {
        this.dateSent = new Timestamp(System.currentTimeMillis());
        this.senderRecipient = messageRequestTemplate.getSenderRecipient();
        this.text = messageRequestTemplate.getText();
    }

}
