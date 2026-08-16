package com.example.springbackend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.springbackend.dto.ChatHeaderResponse;
import com.example.springbackend.dto.MessageDto;
import com.example.springbackend.dto.ParticipantDto;
import com.example.springbackend.dto.SendMessageRequest;
import com.example.springbackend.dto.PrivateChatLookupResponse;
import com.example.springbackend.entity.Activity;
import com.example.springbackend.entity.Chat;
import com.example.springbackend.entity.ChatUser;
import com.example.springbackend.entity.Message;
import com.example.springbackend.entity.MessageSeen;
import com.example.springbackend.entity.User;
import com.example.springbackend.repository.ActivityRepository;
import com.example.springbackend.repository.ChatRepository;
import com.example.springbackend.repository.ChatUserRepository;
import com.example.springbackend.repository.MessageRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.repository.MessageSeenRepository;
import com.example.springbackend.repository.NotificationRepository;
import com.example.springbackend.entity.NotificationType;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageSeenRepository messageSeenRepository;
    private final NotificationService notificationService;


    public ChatService(
            ChatRepository chatRepository,
            ChatUserRepository chatUserRepository,
            ActivityRepository activityRepository,
            UserRepository userRepository,
            MessageRepository messageRepository,
            MessageSeenRepository messageSeenRepository,
            NotificationService notificationService
    ) {
        this.chatRepository = chatRepository;
        this.chatUserRepository = chatUserRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.messageSeenRepository = messageSeenRepository;
        this.notificationService = notificationService;
    }

    public Chat createChat(
            String name,
            boolean isGroup,
            Long creatorId
    ) {

        Chat chat = new Chat();

        chat.setName(name);
        chat.setIsGroup(isGroup);

        chat = chatRepository.save(chat);

        System.out.println("Chat saved: " + chat.getChatId());

        ChatUser member = new ChatUser();

        member.setChatId(chat.getChatId());
        member.setUserId(creatorId);
        member.setJoinedAt(LocalDateTime.now());

        System.out.println("Saving ChatUser...");
        chatUserRepository.save(member);
        System.out.println("ChatUser saved.");

        return chat;
    }



    public void addUserToChat(Long chatId, Long userId) {


        System.out.println("Adding user " + userId + " to chat " + chatId);

        if (chatUserRepository.existsByChatIdAndUserId(chatId, userId)) {
            return;
        }

        ChatUser member = new ChatUser();

        member.setChatId(chatId);
        member.setUserId(userId);
        member.setJoinedAt(LocalDateTime.now());

        chatUserRepository.save(member);

        System.out.println("User added to chat.");
    }




   public List<Chat> getUserChats(Long userId) {

        List<Chat> chats = chatRepository.findChatsByUserId(userId);

        for (Chat chat : chats) {

                // Default values
                chat.setLastMessage(null);
                chat.setUnread(false);
                chat.setOtherUserProfilePhoto(null);

                // Find latest message
                Optional<Message> latestMessage =
                        messageRepository.findTopByChatIdOrderBySentAtDesc(
                                chat.getChatId()
                        );

                if (latestMessage.isPresent()) {

                Message message = latestMessage.get();

                chat.setLastMessage(message.getContent());

                // Only messages from other users can be unread
                if (!message.getSenderId().equals(userId)) {

                        boolean seen =
                                messageSeenRepository.existsByMessageIdAndUserId(
                                        message.getMessageId(),
                                        userId
                                );

                        chat.setUnread(!seen);
                }
                }

                // Private chat
                if (!chat.getIsGroup()) {

                List<ChatUser> members =
                        chatUserRepository.findByChatId(chat.getChatId());

                for (ChatUser member : members) {

                        if (!member.getUserId().equals(userId)) {

                        User otherUser = userRepository
                                .findById(member.getUserId())
                                .orElseThrow();

                        chat.setName(otherUser.getName());

                        chat.setOtherUserProfilePhoto(
                                otherUser.getProfileImageId() != null
                                        ? otherUser.getProfileImageId().toString()
                                        : null
                        );

                        break;
                        }
                }
                }
        }

        return chats;
        }


    //Some help
    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                .getAuthentication();

        String username = authentication.getName();

        User user = userRepository
                .findByEmail(username)
                .orElseThrow();

        return user.getId();
    }


    public ChatHeaderResponse getChatHeader(Long chatId) {

    System.out.println("========== CHAT HEADER START ==========");
    System.out.println("Requested chatId: " + chatId);


    Chat chat = chatRepository
            .findById(chatId)
            .orElseThrow(() -> {
                System.out.println("ERROR: Chat not found for id " + chatId);
                return new RuntimeException("Chat not found");
            });


    System.out.println();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!! CHAT ENTITY DEBUG !!!");
        System.out.println("!!! chatId   = " + chat.getChatId());
        System.out.println("!!! name     = " + chat.getName());
        System.out.println("!!! isGroup  = " + chat.getIsGroup());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println();


    List<ChatUser> chatUsers =
            chatUserRepository.findByChatId(chatId);


    System.out.println(
            "Chat users found: " + chatUsers.size()
    );


    List<ParticipantDto> participants = new ArrayList<>();


    for (ChatUser chatUser : chatUsers) {

        System.out.println(
                "Loading user id: " + chatUser.getUserId()
        );


        User user = userRepository
                .findById(chatUser.getUserId())
                .orElseThrow(() -> {
                    System.out.println(
                            "ERROR: User not found id="
                            + chatUser.getUserId()
                    );
                    return new RuntimeException("User not found");
                });


        System.out.println(
                "User loaded: "
                + user.getName()
                + " imageId="
                + user.getProfileImageId()
        );


        ParticipantDto dto = new ParticipantDto();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setProfileImageId(user.getProfileImageId());

        participants.add(dto);
    }


    System.out.println(
            "Total participants DTO created: "
            + participants.size()
    );


    ChatHeaderResponse response =
            new ChatHeaderResponse();



    response.setIsGroup(Boolean.TRUE.equals(chat.getIsGroup()));

    response.setParticipantCount(participants.size());
    response.setParticipants(participants);



    if (Boolean.TRUE.equals(chat.getIsGroup())) {


        System.out.println("CHAT TYPE: GROUP");


        Activity activity = activityRepository
                .findByChatId(chatId)
                .orElseThrow(() -> {
                    System.out.println(
                            "ERROR: No activity found for group chat "
                            + chatId
                    );
                    return new RuntimeException("Activity not found");
                });


        System.out.println(
                "Activity found: "
                + activity.getTitle()
        );


        response.setPrivateChat(false);
        response.setActivityId(activity.getActivityId());
        response.setActivityTitle(activity.getTitle());


    } else {


        System.out.println("CHAT TYPE: PRIVATE");


        response.setPrivateChat(true);
        response.setActivityId(null);
        response.setActivityTitle(null);



        try {

            Long currentUserId = getCurrentUserId();


            System.out.println(
                    "Current logged user id: "
                    + currentUserId
            );


            for (ParticipantDto participant : participants) {


                System.out.println(
                        "Checking participant: "
                        + participant.getId()
                        + " "
                        + participant.getName()
                );


                if (!participant.getId().equals(currentUserId)) {


                    System.out.println(
                            "OTHER USER FOUND: "
                            + participant.getName()
                    );


                    response.setOtherUserId(
                            participant.getId()
                    );


                    response.setOtherUserName(
                            participant.getName()
                    );


                    response.setOtherUserProfileImageId(
                            participant.getProfileImageId()
                    );


                    break;
                }
            }


        } catch (Exception e) {

            System.out.println(
                    "ERROR getting current user: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    System.out.println();
        System.out.println("1!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!! CHAT RESPONSE DEBUG !!!");
        System.out.println("!!! isGroup       = " + response.isGroup());
        System.out.println("!!! privateChat   = " + response.isPrivateChat());
        System.out.println("!!! activityId    = " + response.getActivityId());
        System.out.println("!!! activityTitle = " + response.getActivityTitle());
        System.out.println("!!! otherUserId   = " + response.getOtherUserId());
        System.out.println("1!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println();

    System.out.println("========== CHAT HEADER END ==========");


    return response;
}


    public List<MessageDto> getMessages(Long chatId) {

        List<Message> messages =
                messageRepository.findByChatIdOrderBySentAtAsc(chatId);

        List<MessageDto> result = new ArrayList<>();

        for (Message message : messages) {

            User sender = userRepository
                    .findById(message.getSenderId())
                    .orElseThrow();

            MessageDto dto = new MessageDto();

            dto.setId(message.getMessageId());
            dto.setSenderId(sender.getId());
            dto.setSenderName(sender.getName());
            dto.setProfileImageId(sender.getProfileImageId());
            dto.setStatus(message.getStatus());


            dto.setContent(message.getContent());
            dto.setSentAt(message.getSentAt());


            dto.setReplyToMessageId(message.getReplyToMessageId());

            if (message.getReplyToMessageId() != null) {

                Message repliedMessage = messageRepository
                        .findById(message.getReplyToMessageId())
                        .orElse(null);

                if (repliedMessage != null) {

                    User repliedSender = userRepository
                            .findById(repliedMessage.getSenderId())
                            .orElse(null);

                    dto.setReplyPreview(repliedMessage.getContent());

                    if (repliedSender != null) {
                        dto.setReplySenderName(repliedSender.getName());
                    }
                }
            }



            result.add(dto);
        }

        return result;
    }


    public MessageDto sendMessage(
                Long chatId,
                SendMessageRequest request
        ) {

                User sender = userRepository
                        .findById(request.getSenderId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Message message = new Message();

                System.out.println("replyToMessageId = " + request.getReplyToMessageId());
                System.out.println("replyPreview = " + request.getReplyPreview());
                System.out.println("replySenderName = " + request.getReplySenderName());

                message.setChatId(chatId);
                message.setSenderId(request.getSenderId());
                message.setContent(request.getContent());

                if (request.getReplyToMessageId() != null) {
                        message.setReplyToMessageId(request.getReplyToMessageId());
                        message.setReplyPreview(request.getReplyPreview());
                        message.setReplySenderName(request.getReplySenderName());
                }

                message.setStatus("sent");
                message.setSentAt(LocalDateTime.now());

                // Save message first
                message = messageRepository.save(message);


                // ============================================================
                // CREATE NOTIFICATIONS
                // ============================================================

                List<ChatUser> chatUsers =
                        chatUserRepository.findByChatId(chatId);

                for (ChatUser chatUser : chatUsers) {

                        Long recipientId = chatUser.getUserId();

                        // Do not notify the person who sent the message
                        if (recipientId.equals(sender.getId())) {
                        continue;
                        }

                        notificationService.createNotification(
                                recipientId,
                                NotificationType.NEW_MESSAGE,
                                "New message",
                                sender.getName() + " sent you a message",
                                sender.getId(),
                                null,
                                chatId,
                                message.getMessageId()
                        );
                }


                // ============================================================
                // BUILD RESPONSE DTO
                // ============================================================

                MessageDto dto = new MessageDto();

                dto.setId(message.getMessageId());
                dto.setSenderId(sender.getId());
                dto.setSenderName(sender.getName());
                dto.setProfileImageId(sender.getProfileImageId());

                dto.setReplyToMessageId(message.getReplyToMessageId());
                dto.setReplyPreview(message.getReplyPreview());
                dto.setReplySenderName(message.getReplySenderName());

                dto.setContent(message.getContent());
                dto.setStatus(message.getStatus());
                dto.setSentAt(message.getSentAt());

                return dto;
        }


    public void markMessagesAsSeen(Long chatId, Long viewerId) {

        List<Message> messages =
                messageRepository.findByChatIdOrderBySentAtAsc(chatId);

        int participants =
        chatUserRepository.findByChatId(chatId).size();

        for (Message message : messages) {

            if (message.getSenderId().equals(viewerId))
                continue;

            if (!messageSeenRepository.existsByMessageIdAndUserId(
                    message.getMessageId(),
                    viewerId
            )) {

                MessageSeen seen = new MessageSeen();

                seen.setMessageId(message.getMessageId());
                seen.setUserId(viewerId);
                seen.setSeenAt(LocalDateTime.now());

                messageSeenRepository.save(seen);
            }

            long seenCount =
                    messageSeenRepository.countByMessageId(
                            message.getMessageId());

            // Everyone except the sender has seen it
            if (seenCount >= participants - 1
                    && !"seen".equals(message.getStatus())) {

                message.setStatus("seen");

                messageRepository.save(message);
            }
        }
    }




        public Chat createPrivateChat(Long user1Id,Long user2Id,String firstMessage) {

                List<Chat> existingChats =
                        chatRepository.findPrivateChatBetweenUsers(
                                user1Id,
                                user2Id
                        );

                if (!existingChats.isEmpty()) {
                        return existingChats.get(0);
                }

                User user1 = userRepository
                        .findById(user1Id)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                User user2 = userRepository
                        .findById(user2Id)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Chat chat = new Chat();

                chat.setName(user1.getName() + " + " + user2.getName());
                chat.setIsGroup(false);

                chat = chatRepository.save(chat);

                ChatUser first = new ChatUser();
                first.setChatId(chat.getChatId());
                first.setUserId(user1Id);
                first.setJoinedAt(LocalDateTime.now());

                ChatUser second = new ChatUser();
                second.setChatId(chat.getChatId());
                second.setUserId(user2Id);
                second.setJoinedAt(LocalDateTime.now());

                chatUserRepository.save(first);
                chatUserRepository.save(second);

                Message message = new Message();
                message.setChatId(chat.getChatId());
                message.setSenderId(user1Id);
                message.setContent(firstMessage);
                message.setStatus("sent");
                message.setSentAt(LocalDateTime.now());

                messageRepository.save(message);

                return chat;
        }



        public PrivateChatLookupResponse findExistingPrivateChat(Long otherUserId) {

                Long currentUserId = getCurrentUserId();

                List<Chat> chats =
                        chatRepository.findPrivateChatBetweenUsers(
                                currentUserId,
                                otherUserId
                        );

                if (chats.isEmpty()) {

                        return new PrivateChatLookupResponse(
                                false,
                                null
                        );
                }

                return new PrivateChatLookupResponse(
                        true,
                        chats.get(0).getChatId()
                );
        }

}