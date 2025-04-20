package org.chainpavilion.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
    private String password;
    private String email;
    
    @OneToMany(mappedBy = "creator")
    private List<Resource> createdResources = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private List<Resource> favorites = new ArrayList<>();
    
    @ElementCollection
    private List<String> learningRecords = new ArrayList<>();
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;

    // Getters and Setters
}