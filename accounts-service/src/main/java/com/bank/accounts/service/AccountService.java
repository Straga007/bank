package com.bank.accounts.service;

import com.bank.accounts.dto.AccountDto;
import com.bank.accounts.dto.UpdateAccountDto;
import com.bank.accounts.model.Account;
import com.bank.accounts.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public AccountDto getAccountByLogin(String login) {
        return accountRepository.findByLogin(login)
            .map(this::convertToDto)
            .orElse(null);
    }
    
    public AccountDto updateAccount(String login, UpdateAccountDto updateAccountDto) {
        Account account = accountRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setFirstName(updateAccountDto.getFirstName());
        account.setLastName(updateAccountDto.getLastName());
        account.setBirthDate(updateAccountDto.getBirthDate());
        
        Account updatedAccount = accountRepository.save(account);
        return convertToDto(updatedAccount);
    }
    
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public AccountDto createAccount(String login, String firstName, String lastName, LocalDate birthDate) {
        Account account = new Account(login, firstName, lastName, birthDate, BigDecimal.ZERO);
        Account savedAccount = accountRepository.save(account);
        return convertToDto(savedAccount);
    }
    
    private AccountDto convertToDto(Account account) {
        return new AccountDto(
            account.getLogin(),
            account.getFirstName(),
            account.getLastName(),
            account.getBirthDate(),
            account.getBalance()
        );
    }
}