package com.uala.repository;

import com.uala.model.FollowEntity;
import com.uala.model.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataFollowRepository extends JpaRepository<FollowEntity, FollowId> {
    List<FollowEntity> findByFollowerId(UUID followerId);
}
