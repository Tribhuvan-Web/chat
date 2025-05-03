package com.chat.application.ChatApplication.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String roomId;

    @JsonIgnore
    @OneToMany
    private List<Messages> messages = new ArrayList<>();
}
