package com.example.springbackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.springbackend.dto.ActivityDetailsDto;
import com.example.springbackend.dto.ActivityResponse;
import com.example.springbackend.dto.CreateActivityRequest;
import com.example.springbackend.dto.ParticipantDto;
import com.example.springbackend.entity.Activity;
import com.example.springbackend.entity.Chat;
import com.example.springbackend.entity.User;
import com.example.springbackend.repository.ActivityRepository;
import com.example.springbackend.repository.ChatRepository;
import com.example.springbackend.repository.ChatUserRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.entity.NotificationType;
import com.example.springbackend.service.NotificationService;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatService chatService;
    private final NotificationService notificationService;

    public ActivityService(
            ActivityRepository activityRepository,
            UserRepository userRepository,
            ChatRepository chatRepository,
            ChatUserRepository chatUserRepository,
            ChatService chatService,
            NotificationService notificationService
    ) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.chatUserRepository = chatUserRepository;
        this.chatService = chatService;
        this.notificationService = notificationService;
    }

    // CREATE - Returns ActivityResponse with creator name
    public ActivityResponse create(CreateActivityRequest dto) {

        Chat chat = chatService.createChat(
            dto.getTitle(),
            true,
            dto.getCreatorId()
        );

        Activity activity = new Activity();
        activity.setChatId(chat.getChatId());
        activity.setCreatorId(dto.getCreatorId());
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setLatitude(dto.getLatitude());
        activity.setLongitude(dto.getLongitude());
        activity.setEventTime(dto.getEventTime());
        activity.setIsCancelled(false);

        User creator = userRepository.findById(dto.getCreatorId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        activity.getParticipants().add(creator);
        
        Activity savedActivity = activityRepository.save(activity);

        // Map to ActivityResponse DTO with creator name
        return mapToActivityResponse(savedActivity, creator);
    }

    // DELETE ACTIVITY
    public void deleteActivity(Long activityId, Long userId) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getCreatorId().equals(userId)) {
            throw new RuntimeException("Only creator can delete activity");
        }

        activityRepository.delete(activity);
    }

    // JOIN ACTIVITY - Returns ActivityResponse
    // JOIN ACTIVITY - Returns ActivityResponse
    public ActivityResponse joinActivity(Long activityId, Long userId) {

        System.out.println("Join Activity");

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // prevent duplicate joins
        if (!activity.getParticipants().contains(user)) {

            activity.getParticipants().add(user);

            System.out.println("Before addUserToChat");

            chatService.addUserToChat(
                    activity.getChatId(),
                    userId
            );

            System.out.println("After addUserToChat");

            activityRepository.save(activity);

            // Notify activity creator that someone joined
            // Notify activity creator that someone joined
            if (!activity.getCreatorId().equals(userId)) {

                notificationService.createNotification(
                        activity.getCreatorId(),
                        NotificationType.ACTIVITY_JOINED,
                        "Activity joined",
                        user.getName() + " joined your " + activity.getTitle() + " activity",
                        userId,
                        activity.getActivityId(),
                        activity.getChatId(),
                        null
                );
            }
        }

        // Get creator for the response
        User creator = userRepository.findById(activity.getCreatorId())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        return mapToActivityResponse(activity, creator);
    }

    // GET ALL ACTIVITIES - Returns List of ActivityResponse
    public List<ActivityResponse> getAllActivities() {

        List<Activity> activities = activityRepository.findAll();

        return activities.stream().map(activity -> {

            User creator = userRepository.findById(activity.getCreatorId())
                    .orElse(null);

            ActivityResponse dto = new ActivityResponse();

            dto.setActivityId(activity.getActivityId());
            dto.setCreatorId(activity.getCreatorId());
            dto.setCreatorName(
                    creator != null ? creator.getName() : "Unknown"
            );
            dto.setChatId(activity.getChatId());
            dto.setTitle(activity.getTitle());
            dto.setDescription(activity.getDescription());
            dto.setLatitude(activity.getLatitude());
            dto.setLongitude(activity.getLongitude());
            dto.setEventTime(activity.getEventTime());
            dto.setIsCancelled(activity.getIsCancelled());

            return dto;

        }).collect(Collectors.toList());
    }

    // LEAVE ACTIVITY - Returns ActivityResponse
    public ActivityResponse leaveActivity(Long activityId, Long userId) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        activity.getParticipants().remove(user);
        activityRepository.save(activity);

        // Get creator for the response
        User creator = userRepository.findById(activity.getCreatorId())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        return mapToActivityResponse(activity, creator);
    }

    // GET ACTIVITY DETAILS BY CHAT ID
    public ActivityDetailsDto getActivityDetails(Long chatId) {

        Activity activity = activityRepository.findByChatId(chatId)
                .orElseThrow(() ->
                        new RuntimeException("Activity not found"));

        ActivityDetailsDto dto = new ActivityDetailsDto();

        dto.setActivityId(activity.getActivityId());
        dto.setChatId(activity.getChatId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setEventTime(activity.getEventTime());

        List<ParticipantDto> participantDtos = activity.getParticipants()
                .stream()
                .map(user -> {
                    ParticipantDto participant = new ParticipantDto();
                    participant.setId(user.getId());
                    participant.setName(user.getName());
                    participant.setProfileImageId(user.getProfileImageId());
                    return participant;
                })
                .collect(Collectors.toList());

        dto.setParticipants(participantDtos);
        dto.setParticipantCount(participantDtos.size());

        return dto;
    }

    // Helper method to map Activity entity to ActivityResponse DTO
    private ActivityResponse mapToActivityResponse(Activity activity, User creator) {
        ActivityResponse response = new ActivityResponse();
        response.setActivityId(activity.getActivityId());
        response.setCreatorId(activity.getCreatorId());
        response.setCreatorName(creator != null ? creator.getName() : "Unknown");
        response.setTitle(activity.getTitle());
        response.setDescription(activity.getDescription());
        response.setLatitude(activity.getLatitude());
        response.setLongitude(activity.getLongitude());
        response.setEventTime(activity.getEventTime());
        response.setIsCancelled(activity.getIsCancelled());
        response.setChatId(activity.getChatId());
        
       
        
        return response;
    }
}