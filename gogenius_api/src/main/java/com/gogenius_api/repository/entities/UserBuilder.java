package com.gogenius_api.repository.entities;


import java.time.LocalDateTime;

public class UserBuilder {
    private String id;
    private String email;
    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private String userType;
    private boolean termsAccepted;
    private String company;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastActivity;

    public UserBuilder id(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder login(String login) {
        this.login = login;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder userType(String userType) {
        this.userType = userType;
        return this;
    }

    public UserBuilder termsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
        return this;
    }

    public UserBuilder company(String company) {
        this.company = company;
        return this;
    }

    public UserBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder lastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
        return this;
    }

    public com.gogenius_api.repository.entities.User build() {
        if (id == null || id.isEmpty()) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        User user = new User(id, email, login, firstName, lastName, password, userType, termsAccepted, company);
        user.setCreatedAt(createdAt);
        user.setLastActivity(lastActivity);
        return user;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public String getCompany() {
        return company;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
}