package com.example.springbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ActivityResponse {

    private Long activityId;
    private Long creatorId;
    private String creatorName;

    private Long chatId;
    private String title;
    private String description;
    private Boolean isCancelled;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime eventTime;

    // Generate getters and setters
}