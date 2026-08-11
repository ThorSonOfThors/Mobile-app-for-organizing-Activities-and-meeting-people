package com.example.springbackend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Set;
import org.springframework.stereotype.Service;

import com.example.springbackend.dto.FriendDto;
import com.example.springbackend.dto.FriendRequestDto;
import com.example.springbackend.dto.FriendshipStatusDto;
import com.example.springbackend.dto.ParticipantDto;
import com.example.springbackend.entity.Friendship;
import com.example.springbackend.entity.User;
import com.example.springbackend.model.FriendshipStatus;
import com.example.springbackend.repository.FriendshipRepository;
import com.example.springbackend.repository.UserRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipService(FriendshipRepository friendshipRepository , UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }


    public Friendship sendFriendRequest(
            Long senderId,
            Long receiverId
    ) {

        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot add yourself.");
        }

        friendshipRepository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
                        senderId,
                        receiverId,
                        receiverId,
                        senderId
                )
                .ifPresent(f -> {
                    throw new RuntimeException(
                            "Friendship already exists."
                    );
                });

        Friendship friendship = new Friendship();

        friendship.setCreatedAt(LocalDateTime.now());
        friendship.setSenderId(senderId);
        friendship.setReceiverId(receiverId);
        friendship.setStatus(FriendshipStatus.PENDING);

        return friendshipRepository.save(friendship);
    }


    public Friendship acceptFriendRequest(
            Long friendshipId,
            Long receiverId
    ) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Friend request not found"));

        if (!friendship.getReceiverId().equals(receiverId)) {
            throw new RuntimeException(
                    "You are not allowed to accept this friend request."
            );
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException(
                    "This friend request has already been processed."
            );
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);

        return friendshipRepository.save(friendship);
    }


    public Friendship declineFriendRequest(
            Long friendshipId,
            Long receiverId
    ) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Friend request not found"));

        if (!friendship.getReceiverId().equals(receiverId)) {
            throw new RuntimeException(
                    "You are not allowed to decline this friend request."
            );
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException(
                    "This friend request has already been processed."
            );
        }

        friendship.setStatus(FriendshipStatus.DECLINED);

        return friendshipRepository.save(friendship);
    }


    public void removeFriend(
            Long friendshipId,
            Long userId
    ) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new RuntimeException("Friendship not found"));

        boolean isParticipant =
                friendship.getSenderId().equals(userId) ||
                friendship.getReceiverId().equals(userId);

        if (!isParticipant) {
            throw new RuntimeException(
                    "You are not allowed to remove this friendship."
            );
        }

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new RuntimeException(
                    "Only accepted friendships can be removed."
            );
        }

        friendshipRepository.delete(friendship);
    }



    public List<FriendDto> getFriends(Long userId) {

        List<Friendship> friendships =
                friendshipRepository.findFriendships(
                        userId,
                        FriendshipStatus.ACCEPTED
                );

        List<Long> friendIds = friendships.stream()
                .map(friendship ->
                        friendship.getSenderId().equals(userId)
                                ? friendship.getReceiverId()
                                : friendship.getSenderId())
                .toList();

        List<User> users = userRepository.findAllById(friendIds);

        Map<Long, User> usersById = users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        return friendships.stream()
                .map(friendship -> {

                    Long friendId = friendship.getSenderId().equals(userId)
                            ? friendship.getReceiverId()
                            : friendship.getSenderId();

                    User friend = usersById.get(friendId);

                    FriendDto dto = new FriendDto();

                    dto.setFriendshipId(friendship.getFriendshipId());
                    dto.setUserId(friend.getId());
                    dto.setName(friend.getName());
                    dto.setProfileImageId(friend.getProfileImageId());

                    return dto;

                })
                .toList();
    }



    public List<FriendRequestDto> getPendingRequests(Long userId) {

        List<Friendship> requests =
                friendshipRepository.findByReceiverIdAndStatus(
                        userId,
                        FriendshipStatus.PENDING
                );

        List<Long> senderIds = requests.stream()
                .map(Friendship::getSenderId)
                .toList();

        List<User> users = userRepository.findAllById(senderIds);

        Map<Long, User> usersById = users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        return requests.stream()
                .map(request -> {

                    User sender = usersById.get(request.getSenderId());

                    FriendRequestDto dto = new FriendRequestDto();

                    dto.setFriendshipId(request.getFriendshipId());
                    dto.setSenderId(sender.getId());
                    dto.setName(sender.getName());
                    dto.setProfileImageId(sender.getProfileImageId());

                    return dto;

                })
                .toList();
    }


    public FriendshipStatusDto getFriendshipStatus(
            Long user1,
            Long user2
    ) {

        FriendshipStatusDto dto = new FriendshipStatusDto();

        friendshipRepository.findRelationship(user1, user2)
                .ifPresentOrElse(friendship -> {

                    dto.setStatus(friendship.getStatus());
                    dto.setFriendshipId(friendship.getFriendshipId());
                    dto.setSenderId(friendship.getSenderId());

                }, () -> {

                    dto.setStatus(FriendshipStatus.NONE);

                });

        return dto;
    }


    public List<FriendDto> getMutualFriends(Long user1Id, Long user2Id) {

        List<Friendship> user1Friendships =
                friendshipRepository.findFriendships(
                        user1Id,
                        FriendshipStatus.ACCEPTED
                );

        List<Friendship> user2Friendships =
                friendshipRepository.findFriendships(
                        user2Id,
                        FriendshipStatus.ACCEPTED
                );

        Set<Long> user1FriendIds = new HashSet<>();

        for (Friendship friendship : user1Friendships) {

            if (friendship.getSenderId().equals(user1Id)) {
                user1FriendIds.add(friendship.getReceiverId());
            } else {
                user1FriendIds.add(friendship.getSenderId());
            }

        }

        Set<Long> user2FriendIds = new HashSet<>();

        for (Friendship friendship : user2Friendships) {

            if (friendship.getSenderId().equals(user2Id)) {
                user2FriendIds.add(friendship.getReceiverId());
            } else {
                user2FriendIds.add(friendship.getSenderId());
            }

        }

        user1FriendIds.retainAll(user2FriendIds);

        if (user1FriendIds.isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.findAllById(user1FriendIds);

        List<FriendDto> result = new ArrayList<>();

        for (User user : users) {

            FriendDto dto = new FriendDto();

            dto.setFriendshipId(null); // not needed for mutual friends
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setProfileImageId(user.getProfileImageId());

            result.add(dto);

        }

        return result;
    }



}