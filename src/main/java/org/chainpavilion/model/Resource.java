package org.chainpavilion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.chainpavilion.model.enums.ResourceCategory;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 资源实体类
 * 
 * 表示系统中的学习资源，包括：
 * - 基本信息(标题、描述、URL等)
 * - 分类信息
 * - 统计信息(浏览量、收藏量)
 * - 关联信息(创建者、收藏用户)
 * 
 * @author 知链智阁团队
 */
@Entity
@Table(name = "resources")
public class Resource {
    /**
     * 资源ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 资源标题，不能为空
     */
    @Column(nullable = false)
    private String title;

    /**
     * 资源描述，不能为空
     */
    @Column(length = 1000)
    private String description;

    /**
     * 资源链接，不能为空
     */
    @Column(nullable = false)
    private String url;

    /**
     * 资源分类，不能为空
     */
    @Enumerated(EnumType.STRING)
    private ResourceCategory category;

    private String coverImage;

    /**
     * 浏览量
     */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 收藏量
     */
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /**
     * 创建此资源的用户
     */
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * 收藏此资源的用户集合
     */
    @ManyToMany(mappedBy = "favoriteResources")
    @JsonIgnore
    private Set<User> favoritedBy = new HashSet<>();
    
    @Transient
    private boolean favorited;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ResourceCategory getCategory() {
        return category;
    }

    public void setCategory(ResourceCategory category) {
        this.category = category;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<User> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(Set<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
    }
    
    public boolean isFavorited() {
        return favorited;
    }
    
    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
}