package com.example.springbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class CreateActivityRequest {

    private Long creatorId;
    private Long chatId;
    private String title;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime eventTime;

}
