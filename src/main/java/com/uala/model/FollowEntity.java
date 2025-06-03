package com.uala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follows")
@IdClass(FollowId.class)
public class FollowEntity {

    @Id
    @Column(name = "follower_id", columnDefinition = "uuid")
    private UUID followerId;

    @Id
    @Column(name = "followed_id", columnDefinition = "uuid")
    private UUID followedId;
}
