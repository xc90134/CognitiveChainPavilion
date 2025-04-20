package org.chainpavilion.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 * 
 * 记录用户收藏的资源
 */
@Entity
@Table(name = "user_favorites")
public class UserFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;
    
    private LocalDateTime createdAt;
    
    // 构造函数
    public UserFavorite() {
    }
    
    public UserFavorite(User user, Resource resource) {
        this.user = user;
        this.resource = resource;
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