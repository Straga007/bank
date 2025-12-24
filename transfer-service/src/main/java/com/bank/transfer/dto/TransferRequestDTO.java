package com.bank.transfer.dto;

public class TransferRequestDTO {
    private String recipientUserId;
    private String recipientUsername;
    private Double amount;
    private String description;

    public TransferRequestDTO() {
    }

    public TransferRequestDTO(String recipientUserId, Double amount, String description) {
        this.recipientUserId = recipientUserId;
        this.amount = amount;
        this.description = description;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                "recipientUserId='" + recipientUserId + '\'' +
                ", recipientUsername='" + recipientUsername + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}