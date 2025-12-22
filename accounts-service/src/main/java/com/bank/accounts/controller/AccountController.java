package com.bank.accounts.controller;

import com.bank.accounts.model.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/") // Изменено с "/api/accounts" на "/"
public class AccountController {
    
    @PostMapping("/balance/update") // Изменено с "/api/accounts/balance/update" на "/balance/update"
    public ResponseEntity<Map<String, Object>> updateBalance(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            
            // Получаем или создаем аккаунт
            Account account = Account.ACCOUNTS.computeIfAbsent(userId, 
                k -> new Account(userId, new BigDecimal("0.00")));
            
            // Обновляем баланс
            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);
            
            // Подготовка ответа
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Баланс успешно обновлен");
            response.put("newBalance", newBalance);
            response.put("transactionId", UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Ошибка при обновлении баланса: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/balance/{userId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String userId) {
        Account account = Account.ACCOUNTS.get(userId);
        if (account == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Аккаунт не найден");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("userId", userId);
        response.put("balance", account.getBalance());
        return ResponseEntity.ok(response);
    }
}