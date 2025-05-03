package com.chat.application.ChatApplication.repository;


import com.chat.application.ChatApplication.models.Messages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Messages, Integer> {
    // Additional query methods as needed
}
