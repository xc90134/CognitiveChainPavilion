package org.chainpavilion.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户活动实体类
 * 
 * 记录用户的各种活动，如浏览资源、收藏资源等
 */
@Entity
@Table(name = "user_activities")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;
    
    // 活动类型：VIEW(浏览)、FAVORITE(收藏)
    private String activityType;
    
    private LocalDateTime createdAt;
    
    // 构造函数
    public UserActivity() {
    }
    
    public UserActivity(User user, Resource resource, String activityType) {
        this.user = user;
        this.resource = resource;
        this.activityType = activityType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
} 