package org.chainpavilion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 * 
 * 表示系统中的用户，包括：
 * - 用户基本信息（用户名、密码、邮箱等）
 * - 用户角色
 * - 用户收藏的资源集合
 * 
 * @author 知链智阁团队
 */
@Entity
@Table(name = "users")
public class User {
    /**
     * 用户ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名，唯一且不能为空
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 密码，加密存储，不能为空
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    /**
     * 电子邮箱，不能为空
     */
    @Column(nullable = false, unique = true)
    private String email;

    private String role;

    /**
     * 用户收藏的资源集合
     */
    @ManyToMany
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    @JsonIgnore
    private Set<Resource> favoriteResources = new HashSet<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Resource> getFavoriteResources() {
        return favoriteResources;
    }

    public void setFavoriteResources(Set<Resource> favoriteResources) {
        this.favoriteResources = favoriteResources;
    }
}