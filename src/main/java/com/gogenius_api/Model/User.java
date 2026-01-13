package com.gogenius_api.Model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@Table(name = "users")
public class User {
    @Id
    private String id; // UUID String au lieu de Long
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String login; // Pseudonyme unique
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    
    private String userType; // "tourist" ou "partner"
    
    @Column(nullable = false)
    private boolean termsAccepted;
    
    private String company; // Null si userType = "tourist"
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime lastActivity;
    
    private String resetToken;
    
    private LocalDateTime resetTokenExpiry;
    
    // Constructeurs
    public User() {}
    
    public User(String id, String email, String login, String firstName, String lastName, 
                String password, String userType, boolean termsAccepted, String company) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userType = userType;
        this.termsAccepted = termsAccepted;
        this.company = company;
        this.createdAt = LocalDateTime.now();
    }
    
    // Builder
    public static UserBuilder builder() {
        return new UserBuilder();
    }
    
    // Getters et Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getLogin() {
        return login;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public boolean isTermsAccepted() {
        return termsAccepted;
    }
    
    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
  
  
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public String getResetToken() {
        return resetToken;
    }
    
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    
    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }
    
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}