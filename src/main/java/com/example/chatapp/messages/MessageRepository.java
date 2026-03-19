package com.example.chatapp.messages;

import com.example.chatapp.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE " +
            "(m.owner.username = :username AND m.receiver.username = :otherUsername) OR " +
            "(m.owner.username = :otherUsername AND m.receiver.username = :username) " +
            "ORDER BY m.createdAt ASC")
    List<Message> findChatMessages(@Param("username") String userUsername, @Param("otherUsername") String otherUsername);

    @Query("SELECT DISTINCT u FROM User u WHERE " +
            "u.username IN (SELECT m.receiver.username FROM Message m WHERE m.owner.username = :username) OR " +
            "u.username IN (SELECT m.owner.username FROM Message m WHERE m.receiver.username = :username)")
    List<User> findAllConversationPartners(@Param("username") String username);
}
