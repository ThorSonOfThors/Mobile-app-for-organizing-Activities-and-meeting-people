package com.example.springbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendDto {

    private Long friendshipId;
    private Long userId;
    private String name;
    private Long profileImageId;

}
