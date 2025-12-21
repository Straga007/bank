package com.bank.accounts.controller;

import com.bank.accounts.dto.AccountDto;
import com.bank.accounts.dto.UpdateAccountDto;
import com.bank.accounts.service.AccountService;
import com.bank.accounts.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/me")
    public ResponseEntity<AccountDto> getMyAccount(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String login = jwt.getSubject();
        
        AccountDto account = accountService.getAccountByLogin(login);
        if (account == null) {
            // Если аккаунт не найден, создаем новый
            account = accountService.createAccount(login, "Имя", "Фамилия", java.time.LocalDate.now());
        }
        
        return ResponseEntity.ok(account);
    }
    
    @PutMapping("/me")
    public ResponseEntity<AccountDto> updateMyAccount(
            @RequestBody UpdateAccountDto updateAccountDto,
            Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String login = jwt.getSubject();
        
        AccountDto updatedAccount = accountService.updateAccount(login, updateAccountDto);
        
        // Отправляем уведомление об обновлении аккаунта
        notificationService.sendNotification("Аккаунт пользователя " + login + " был обновлен");
        
        return ResponseEntity.ok(updatedAccount);
    }
    
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

}