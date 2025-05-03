package com.chat.application.ChatApplication.controllers;

import com.chat.application.ChatApplication.models.Messages;
import com.chat.application.ChatApplication.models.Room;
import com.chat.application.ChatApplication.models.RoomRequest;
import com.chat.application.ChatApplication.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Create rooms
    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest request) {
        String roomId = request.getRoomId();

        if (roomRepository.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        Room room = new Room();
        room.setRoomId(roomId);
        Room savedRoom = roomRepository.save(room);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    // Get rooms

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {
        Room room = roomRepository.findByRoomId(roomId);
        return (room == null) ? ResponseEntity.badRequest().body("Room not found") : ResponseEntity.ok(room);
    }

    // Get messages of the room

    @GetMapping("/{roomId}/messages/room")
    public ResponseEntity<List<Messages>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().build();
        }
        // get messages :
        // pagination
        List<Messages> messages = room.getMessages();
        int start = Math.max(0, messages.size() - (page + 1) * size);
        int end = Math.min(messages.size(), start + size);
        List<Messages> paginatedMessages = messages.subList(start, end);
        return ResponseEntity.ok(paginatedMessages);

    }
}