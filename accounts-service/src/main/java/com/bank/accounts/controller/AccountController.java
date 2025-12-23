package com.bank.accounts.controller;

import com.bank.accounts.model.Account;
import com.bank.accounts.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/") // Изменено с "/api/accounts" на "/"
public class AccountController {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    
    @Autowired
    private AccountService accountService;
    
    @PostMapping("/balance/update") // Изменено с "/api/accounts/balance/update" на "/balance/update"
    public ResponseEntity<Map<String, Object>> updateBalance(@RequestBody Map<String, Object> request) {
        logger.info("Получен запрос на обновление баланса: {}", request);
        try {
            String userId = (String) request.get("userId");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            
            logger.info("Обновление баланса для пользователя {} на сумму {}", userId, amount);
            
            // Обновляем баланс через сервисный слой
            Account updatedAccount = accountService.updateBalance(userId, amount);
            
            logger.info("Баланс успешно обновлен. Новый баланс: {}", updatedAccount.getBalance());
            
            // Подготовка ответа
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Баланс успешно обновлен");
            response.put("newBalance", updatedAccount.getBalance());
            response.put("transactionId", UUID.randomUUID().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении баланса", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Ошибка при обновлении баланса: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/balance/{userId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String userId) {
        logger.info("Получен запрос на получение баланса для пользователя {}", userId);
        try {
            java.util.Optional<Account> accountOpt = accountService.getAccountByUserId(userId);
            if (accountOpt.isEmpty()) {
                logger.warn("Аккаунт не найден для пользователя {}", userId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Аккаунт не найден");
                return ResponseEntity.badRequest().body(response);
            }
            
            Account account = accountOpt.get();
            logger.info("Получен баланс для пользователя {}: {}", userId, account.getBalance());
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("userId", userId);
            response.put("balance", account.getBalance());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении баланса для пользователя {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Ошибка при получении баланса: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable String userId) {
        logger.info("Получен запрос на получение данных аккаунта для пользователя {}", userId);
        try {
            java.util.Optional<Account> accountOpt = accountService.getAccountByUserId(userId);
            if (accountOpt.isEmpty()) {
                logger.warn("Аккаунт не найден для пользователя {}", userId);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Аккаунт не найден");
                return ResponseEntity.badRequest().body(response);
            }
            
            Account account = accountOpt.get();
            logger.info("Получены данные аккаунта для пользователя {}", userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("userId", account.getUserId());
            response.put("firstName", account.getFirstName());
            response.put("lastName", account.getLastName());
            response.put("birthDate", account.getBirthDate());
            response.put("balance", account.getBalance());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при получении данных аккаунта для пользователя {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Ошибка при получении данных аккаунта: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateAccount(@PathVariable String userId, @RequestBody Map<String, Object> request) {
        logger.info("Получен запрос на обновление данных аккаунта для пользователя {}: {}", userId, request);
        try {
            String firstName = (String) request.get("firstName");
            String lastName = (String) request.get("lastName");
            String birthDate = (String) request.get("birthDate");
            
            Account updatedAccount = accountService.updateAccount(userId, firstName, lastName, birthDate);
            
            logger.info("Данные аккаунта успешно обновлены для пользователя {}", userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Данные аккаунта успешно обновлены");
            response.put("userId", updatedAccount.getUserId());
            response.put("firstName", updatedAccount.getFirstName());
            response.put("lastName", updatedAccount.getLastName());
            response.put("birthDate", updatedAccount.getBirthDate());
            response.put("balance", updatedAccount.getBalance());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении данных аккаунта для пользователя {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Ошибка при обновлении данных аккаунта: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}