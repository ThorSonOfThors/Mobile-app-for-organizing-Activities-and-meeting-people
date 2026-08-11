package com.example.springbackend.dto;

import com.example.springbackend.model.FriendshipStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipStatusDto {

    private FriendshipStatus status;

    private Long friendshipId;

    private Long senderId;
}