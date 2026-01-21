package com.bank.transfer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {
    
    private Long id;
    
    private String senderUserId;
    
    private String recipientUserId;
    
    private BigDecimal amount;
    
    private LocalDateTime transferDate;
    
    private String status; // COMPLETED, FAILED, PENDING
    
    private String description;
    
    // Constructors
    public Transfer() {
        this.transferDate = LocalDateTime.now();
    }
    
    public Transfer(String senderUserId, String recipientUserId, BigDecimal amount, String status, String description) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.amount = amount;
        this.status = status;
        this.description = description;
        this.transferDate = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSenderUserId() {
        return senderUserId;
    }
    
    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }
    
    public String getRecipientUserId() {
        return recipientUserId;
    }
    
    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTransferDate() {
        return transferDate;
    }
    
    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}