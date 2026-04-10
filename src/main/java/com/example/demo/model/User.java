package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(unique = true, nullable = false)
	    private String email;

	    @Column(unique = true, nullable = false)
	    private String username;
	    
	    @Column(nullable = false)
	    private String password;
	    
	    private String fullName;
	    private String phoneNumber;
	    
	    @Enumerated(EnumType.STRING)
	    private Role role;
	    
	    private boolean active = true;
	    
	    @Column(updatable = false)
	    private LocalDateTime createdAt;
	    
	    private LocalDateTime updatedAt;
	    
	    @PrePersist
	    protected void onCreate() {
	        createdAt = LocalDateTime.now();
	        updatedAt = LocalDateTime.now();
	    }
	    
	    @PreUpdate
	    protected void onUpdate() {
	        updatedAt = LocalDateTime.now();
	    }
	    
	    // Getters and Setters
	    public Long getId() {
	        return id;
	    }
	    
	    public void setId(Long id) {
	        this.id = id;
	    }
	    
	    public String getEmail() {
	        return email;
	    }
	    
	    public void setEmail(String email) {
	        this.email = email;
	    }
	    
	    public String getPassword() {
	        return password;
	    }
	    
	    public String getUsername() {
	        return username;
	    }
	    
	    public void setUsername(String username) {
	        this.username = username;
	    }
	    
	    public void setPassword(String password) {
	        this.password = password;
	    }
	    
	    public String getFullName() {
	        return fullName;
	    }
	    
	    public void setFullName(String fullName) {
	        this.fullName = fullName;
	    }
	    
	    public String getPhoneNumber() {
	        return phoneNumber;
	    }
	    
	    public void setPhoneNumber(String phoneNumber) {
	        this.phoneNumber = phoneNumber;
	    }
	    
	    public Role getRole() {
	        return role;
	    }
	    
	    public void setRole(Role role) {
	        this.role = role;
	    }
	    
	    public boolean isActive() {
	        return active;
	    }
	    
	    public void setActive(boolean active) {
	        this.active = active;
	    }
	    
	    public LocalDateTime getCreatedAt() {
	        return createdAt;
	    }
	    
	    public LocalDateTime getUpdatedAt() {
	        return updatedAt;
	    }
	

}
