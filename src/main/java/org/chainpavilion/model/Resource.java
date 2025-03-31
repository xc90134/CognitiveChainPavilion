package org.chainpavilion.model;

import javax.persistence.*;
import java.util.Set;

/**
 * 学习资源实体类
 * 
 * 表示系统中的学习资源，包括：
 * - 资源基本信息（标题、描述、链接）
 * - 资源分类
 * - 收藏此资源的用户集合
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
    @Column(nullable = false)
    private String description;

    /**
     * 资源链接，不能为空
     */
    @Column(nullable = false)
    private String url;

    /**
     * 资源分类，不能为空
     */
    @Column(nullable = false)
    private String category;

    /**
     * 收藏此资源的用户集合
     * 由User实体的resources字段维护关联关系
     */
    @ManyToMany(mappedBy = "resources")
    private Set<User> users;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}