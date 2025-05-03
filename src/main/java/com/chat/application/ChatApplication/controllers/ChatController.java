package com.chat.application.ChatApplication.controllers;

import com.chat.application.ChatApplication.models.MessageRequest;
import com.chat.application.ChatApplication.models.Messages;
import com.chat.application.ChatApplication.models.Room;
import com.chat.application.ChatApplication.repository.MessagesRepository;
import com.chat.application.ChatApplication.repository.RoomRepository;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessagesRepository messagesRepository;

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Messages sendMessage(@DestinationVariable String roomId, @RequestBody MessageRequest messageRequest) {
        // Use the roomId from the path variable instead of from the request body
        Room room = roomRepository.findByRoomId(roomId);

        if (room == null) {
            // Create a new room if it doesn't exist
            room = new Room();
            room.setRoomId(roomId);
            roomRepository.save(room);
        }

        Messages messages = new Messages();
        messages.setContent(messageRequest.getContent());
        messages.setSender(messageRequest.getSender());
        messages.setTimestamp(LocalDateTime.now());
        messages.setRoom(room);

        // Save the message
        messagesRepository.save(messages);

        // Add the message to the room's messages list
        room.getMessages().add(messages);
        roomRepository.save(room);

        return messages;
    }
}
