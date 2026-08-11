package com.example.springbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "\"Users\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "profile_image_id")
    private Long profileImageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "bio", length = 1000)
    private String bio;

    public User() {}

   
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<Activity> joinedActivities = new HashSet<>();

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Long getProfileImageId() { return profileImageId; }
    public void setProfileImageId(Long profileImageId) { this.profileImageId = profileImageId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // @JsonIgnore
    // public Set<Activity> getJoinedActivities() {
    //     return joinedActivities;
    // }

    @JsonIgnore
    public Set<Activity> getActivities() {
        return joinedActivities;
    }
}