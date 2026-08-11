package com.example.springbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbackend.dto.ActivityDetailsDto;
import com.example.springbackend.dto.ActivityResponse;
import com.example.springbackend.dto.CreateActivityRequest;
import com.example.springbackend.service.ActivityService;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(
            ActivityService activityService
    ) {
        this.activityService = activityService;
    }

    // CREATE - Returns ActivityResponse with creator name
    @PostMapping
    public ResponseEntity<ActivityResponse> create(
            @RequestBody CreateActivityRequest request
    ) {
        ActivityResponse response = activityService.create(request);
        return ResponseEntity.ok(response);
    }

    // JOIN ACTIVITY - Returns ActivityResponse
    @PostMapping("/{activityId}/join/{userId}")
    public ResponseEntity<ActivityResponse> join(
            @PathVariable Long activityId,
            @PathVariable Long userId
    ) {
        ActivityResponse response = activityService.joinActivity(activityId, userId);
        return ResponseEntity.ok(response);
    }

    // GET ALL ACTIVITIES - Returns List of ActivityResponse
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {
        List<ActivityResponse> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    // DELETE ACTIVITY
    @DeleteMapping("/{activityId}/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long activityId,
            @PathVariable Long userId
    ) {
        activityService.deleteActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }

    // LEAVE ACTIVITY - Returns ActivityResponse
    @PostMapping("/{activityId}/leave/{userId}")
    public ResponseEntity<ActivityResponse> leaveActivity(
            @PathVariable Long activityId,
            @PathVariable Long userId
    ) {
        ActivityResponse response = activityService.leaveActivity(activityId, userId);
        return ResponseEntity.ok(response);
    }

    // GET ACTIVITY DETAILS BY CHAT ID
    @GetMapping("/chat/{chatId}/details")
    public ResponseEntity<ActivityDetailsDto> getActivityDetails(
            @PathVariable Long chatId) {
        return ResponseEntity.ok(
                activityService.getActivityDetails(chatId)
        );
    }
}