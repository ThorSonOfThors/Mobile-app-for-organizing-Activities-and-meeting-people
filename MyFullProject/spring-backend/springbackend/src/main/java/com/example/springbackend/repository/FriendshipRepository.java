package com.example.springbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springbackend.entity.Friendship;
import com.example.springbackend.model.FriendshipStatus;

@Repository
public interface FriendshipRepository
        extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findBySenderIdAndReceiverId(
            Long senderId,
            Long receiverId
    );

    Optional<Friendship> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(
            Long sender1,
            Long receiver1,
            Long sender2,
            Long receiver2
    );

    List<Friendship> findByReceiverIdAndStatus(
            Long receiverId,
            FriendshipStatus status
    );

    List<Friendship> findBySenderIdAndStatus(
            Long senderId,
            FriendshipStatus status
    );


    @Query("""
        SELECT f
        FROM Friendship f
        WHERE f.status = :status
        AND (
            f.senderId = :userId
            OR f.receiverId = :userId
        )
    """)
    List<Friendship> findFriendships(
            @Param("userId") Long userId,
            @Param("status") FriendshipStatus status
    );


    @Query("""
        SELECT f
        FROM Friendship f
        WHERE
            (f.senderId = :user1 AND f.receiverId = :user2)
        OR
            (f.senderId = :user2 AND f.receiverId = :user1)
        """)
    Optional<Friendship> findRelationship(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

}