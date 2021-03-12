package com.zack.projects.chatapp.conversationservice.entity;

import com.zack.projects.chatapp.conversationservice.dto.ConversationId;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ConversationId.class)
public class Conversation {

    @Id
    private String userA;

    @Id
    private String userB;

    private Timestamp dateStarted;

}
