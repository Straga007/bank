package com.bank.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountDto {
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private BigDecimal balance;

    // Constructors
    public AccountDto() {}

    public AccountDto(String login, String firstName, String lastName, LocalDate birthDate, BigDecimal balance) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.balance = balance;
    }

    // Getters and setters
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}