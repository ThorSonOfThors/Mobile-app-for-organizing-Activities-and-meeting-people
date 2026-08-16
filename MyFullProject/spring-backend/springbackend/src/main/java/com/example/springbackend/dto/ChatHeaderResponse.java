package com.example.springbackend.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class ChatHeaderResponse {

    private boolean privateChat;

    private Long activityId;

    private String activityTitle;

    private Integer participantCount;

    private boolean isGroup;

    private Long otherUserId;

    private String otherUserName;

    private Long otherUserProfileImageId;

    private List<ParticipantDto> participants;

 
    // getters/setters


    public boolean isGroup() {
    return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
}
