package com.bank.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponseDTO {
    private Long id;
    private String senderUserId;
    private String recipientUserId;
    private BigDecimal amount;
    private LocalDateTime transferDate;
    private String description;

    public TransferResponseDTO() {
    }

    public TransferResponseDTO(Long id, String senderUserId, String recipientUserId, 
                              BigDecimal amount, LocalDateTime transferDate, String description) {
        this.id = id;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.amount = amount;
        this.transferDate = transferDate;
        this.description = description;
    }

    // Геттеры и сеттеры
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}