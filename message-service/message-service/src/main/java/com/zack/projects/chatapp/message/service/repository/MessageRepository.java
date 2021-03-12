package com.zack.projects.chatapp.message.service.repository;

import com.zack.projects.chatapp.message.service.dto.SenderRecipient;
import com.zack.projects.chatapp.message.service.entity.Message;
import com.zack.projects.chatapp.message.service.template.MessageResponseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, SenderRecipient> {

    @Query("select m from Message m " +
            "where m.senderRecipient.sender = :userA and m.senderRecipient.recipient = :userB " +
            "or m.senderRecipient.sender = :userB and m.senderRecipient.recipient = :userA")
    List<MessageResponseTemplate> findAllBySenderRecipient(String userA, String userB);

}
