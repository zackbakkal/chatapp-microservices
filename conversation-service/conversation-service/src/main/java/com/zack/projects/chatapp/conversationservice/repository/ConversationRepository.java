package com.zack.projects.chatapp.conversationservice.repository;

import com.zack.projects.chatapp.conversationservice.dto.ConversationId;
import com.zack.projects.chatapp.conversationservice.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository extends JpaRepository<Conversation, ConversationId> {


    @Query("select c from Conversation c " +
            "where c.userA = :userA and c.userB = :userB " +
            "or c.userA = :userB and c.userB = :userA")
    Conversation findConversationByConversationId(String userA, String userB);

}
