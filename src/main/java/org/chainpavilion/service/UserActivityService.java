package org.chainpavilion.service;

import org.chainpavilion.model.Resource;
import org.chainpavilion.model.User;
import org.chainpavilion.model.UserActivity;
import org.chainpavilion.repository.ResourceRepository;
import org.chainpavilion.repository.UserActivityRepository;
import org.chainpavilion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 用户活动服务类
 */
@Service
public class UserActivityService {
    
    @Autowired
    private UserActivityRepository userActivityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    // 活动类型常量
    public static final String ACTIVITY_VIEW = "VIEW";
    public static final String ACTIVITY_FAVORITE = "FAVORITE";
    
    /**
     * 记录用户浏览资源活动
     */
    public void recordViewActivity(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElse(null);
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        
        if (user != null && resource != null) {
            UserActivity activity = new UserActivity(user, resource, ACTIVITY_VIEW);
            userActivityRepository.save(activity);
        }
    }
    
    /**
     * 记录用户收藏资源活动
     */
    public void recordFavoriteActivity(Long userId, Long resourceId) {
        User user = userRepository.findById(userId).orElse(null);
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        
        if (user != null && resource != null) {
            UserActivity activity = new UserActivity(user, resource, ACTIVITY_FAVORITE);
            userActivityRepository.save(activity);
        }
    }
    
    /**
     * 获取用户的所有活动
     */
    public Page<UserActivity> getUserActivities(Long userId, Pageable pageable) {
        return userActivityRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * 获取用户的特定类型活动
     */
    public Page<UserActivity> getUserActivitiesByType(Long userId, String activityType, Pageable pageable) {
        return userActivityRepository.findByUserIdAndActivityTypeOrderByCreatedAtDesc(userId, activityType, pageable);
    }
} 