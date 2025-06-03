package com.uala.repository;

import com.uala.model.TweetEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTweetRepository extends JpaRepository<TweetEntity, UUID> {
    @Query("""
                SELECT t FROM TweetEntity t
                WHERE t.userId IN ( :userIds )
                ORDER BY t.createdAt DESC
            """)
    List<TweetEntity> findTimelineForUser(@Param("userIds") List<UUID> userIds, Pageable pageable);
}

