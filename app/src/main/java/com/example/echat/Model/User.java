package com.example.echat.Model;

public class User {
    private String id;
    private String username;
    private String profileImageUrl;
    private String email;
    private String number;
    private String status;

    public User() {
    }

    public User(String id, String username, String profileImageUrl, String email, String number, String status) {
        this.id = id;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.number = number;
        this.status = status;
    }

    public User(String id, String username, String profileImageUrl) {
        this.id = id;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String id, String username, String profileImageUrl, String email, String number) {
        this.id = id;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


