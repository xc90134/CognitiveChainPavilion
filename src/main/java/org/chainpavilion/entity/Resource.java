package org.chainpavilion.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


public class Resource {
    @Column(length = 500)
    private String url;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;
    
    @Enumerated(EnumType.STRING)

    // Getters and Setters
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Setter
    @Getter
    private String title;
    @Getter
    @Setter
    private String content;
    @Getter
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}