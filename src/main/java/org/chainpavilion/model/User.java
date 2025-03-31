package org.chainpavilion.model;

import javax.persistence.*;
import java.util.Set;

/**
 * 用户实体类
 * 
 * 表示系统中的用户账户信息，包括：
 * - 基本账户信息（用户名、密码、邮箱）
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
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * 密码，加密存储，不能为空
     */
    @Column(nullable = false)
    private String password;

    /**
     * 电子邮箱，不能为空
     */
    @Column(nullable = false)
    private String email;

    /**
     * 用户收藏的资源集合
     * 通过中间表user_resources关联
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_resources",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<Resource> resources;

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

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }
}