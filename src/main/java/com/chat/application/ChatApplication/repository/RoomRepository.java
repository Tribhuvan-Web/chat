package com.chat.application.ChatApplication.repository;

import com.chat.application.ChatApplication.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    //Get room using Room Id

    Room findByRoomId(String roomId);
}
