package org.chainpavilion.repository;

import org.chainpavilion.model.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户活动数据访问接口
 */
@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    
    /**
     * 查询用户的活动记录（分页）
     */
    Page<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 查询特定类型的用户活动
     */
    Page<UserActivity> findByUserIdAndActivityTypeOrderByCreatedAtDesc(
            Long userId, String activityType, Pageable pageable);
} 