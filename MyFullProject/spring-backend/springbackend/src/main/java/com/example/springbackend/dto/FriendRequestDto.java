package com.example.springbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDto {

    private Long friendshipId;

    private Long senderId;

    private Long profileImageId;

    private String name;

}
