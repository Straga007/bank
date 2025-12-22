package com.bank.accounts.model;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class Account {
    private String userId;
    private BigDecimal balance;
    
    public Account(String userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }
    
    // Геттеры и сеттеры
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    // In-memory storage for demonstration purposes
    public static final Map<String, Account> ACCOUNTS = new ConcurrentHashMap<>();
    
    static {
        ACCOUNTS.put("user1", new Account("user1", new BigDecimal("0.00")));
    }
}