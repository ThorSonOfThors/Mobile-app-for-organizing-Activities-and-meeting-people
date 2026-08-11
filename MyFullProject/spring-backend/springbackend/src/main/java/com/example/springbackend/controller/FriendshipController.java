package com.example.springbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbackend.dto.FriendDto;
import com.example.springbackend.dto.FriendRequestDto;
import com.example.springbackend.dto.FriendshipStatusDto;
import com.example.springbackend.dto.ParticipantDto;
import com.example.springbackend.entity.Friendship;
import com.example.springbackend.service.FriendshipService;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }



    @PostMapping("/request")
    public ResponseEntity<Friendship> sendFriendRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId
    ) {

        return ResponseEntity.ok(
                friendshipService.sendFriendRequest(senderId, receiverId)
        );
    }


    @PostMapping("/{friendshipId}/accept")
    public ResponseEntity<Friendship> acceptFriendRequest(
            @PathVariable Long friendshipId,
            @RequestParam Long receiverId
    ) {

        return ResponseEntity.ok(
                friendshipService.acceptFriendRequest(
                        friendshipId,
                        receiverId
                )
        );
    }


    @PostMapping("/{friendshipId}/decline")
    public ResponseEntity<Friendship> declineFriendRequest(
            @PathVariable Long friendshipId,
            @RequestParam Long receiverId
    ) {

        return ResponseEntity.ok(
                friendshipService.declineFriendRequest(
                        friendshipId,
                        receiverId
                )
        );
    }


    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable Long friendshipId,
            @RequestParam Long userId
    ) {

        friendshipService.removeFriend(
                friendshipId,
                userId
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendDto>> getFriends(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                friendshipService.getFriends(userId)
        );
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<FriendRequestDto>> getPendingRequests(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                friendshipService.getPendingRequests(userId)
        );
    }

    @GetMapping("/status")
    public ResponseEntity<FriendshipStatusDto> getFriendshipStatus(
            @RequestParam Long user1,
            @RequestParam Long user2
    ) {

        return ResponseEntity.ok(
                friendshipService.getFriendshipStatus(
                        user1,
                        user2
                )
        );
    }


    @GetMapping("/mutual")
    public ResponseEntity<List<FriendDto>> getMutualFriends(
            @RequestParam Long user1,
            @RequestParam Long user2
    ) {
        return ResponseEntity.ok(

                friendshipService.getMutualFriends(
                        user1,
                        user2
                )
        );
    }


}
