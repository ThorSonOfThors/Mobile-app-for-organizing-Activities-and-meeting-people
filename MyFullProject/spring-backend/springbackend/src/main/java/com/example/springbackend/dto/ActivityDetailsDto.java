package com.example.springbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ActivityDetailsDto {

    private Long activityId;

    private Long chatId;

    private String title;

    private String description;

    private LocalDateTime eventTime;

    private Integer participantCount;

    private List<ParticipantDto> participants;
}
