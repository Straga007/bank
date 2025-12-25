package com.bank.topup.dto;

import java.time.LocalDateTime;

public class NotificationRequestDTO {
    private String userId;
    private String userName;
    private String type;
    private String amount;
    private String description;
    private LocalDateTime timestamp;

    public NotificationRequestDTO() {
    }

    public NotificationRequestDTO(String userId, String userName, String type, String amount, String description, LocalDateTime timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}