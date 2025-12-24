package com.bank.topup.controller;

import com.bank.topup.client.AccountsServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class TopUpController {

    private static final Logger logger = LoggerFactory.getLogger(TopUpController.class);

    private final AccountsServiceClient accountsServiceClient;

    public TopUpController(AccountsServiceClient accountsServiceClient) {
        this.accountsServiceClient = accountsServiceClient;
    }

    @PostMapping("/balance")
    public Mono<ResponseEntity<Map<String, Object>>> topUpBalance(
            @AuthenticationPrincipal Jwt jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient,
            @RequestBody Map<String, Object> request) {
        
        logger.info("Получен запрос на пополнение баланса: {}", request);
        
        // Получаем сумму пополнения из запроса
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        
        // Получаем токен микросервиса
        String serviceToken = authorizedClient.getAccessToken().getTokenValue();
        
        // Получаем информацию о пользователе из JWT
        String userId = jwt.getSubject();
        String userEmail = jwt.getClaimAsString("email");
        String userName = jwt.getClaimAsString("preferred_username");
        
        logger.info("Пополнение баланса для пользователя: userId={}, userName={}, userEmail={}, amount={}", userId, userName, userEmail, amount);
        
        // Вызываем Accounts сервис для обновления баланса
        return accountsServiceClient.updateAccountBalance(serviceToken, userId, userName, amount)
                .doOnSuccess(accountsResponse -> logger.info("Баланс успешно обновлен для пользователя: {}", userId))
                .doOnError(error -> logger.error("Ошибка при обновлении баланса для пользователя: {}", userId, error))
                .map(accountsResponse -> {
                    logger.info("Получен ответ от Accounts сервиса: {}", accountsResponse);
                    
                    // Создаем ответ
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Баланс успешно пополнен");
                    response.put("amount", amount);
                    response.put("userId", userId);
                    response.put("userName", userName);
                    response.put("userEmail", userEmail);
                    response.put("transactionId", UUID.randomUUID().toString());
                    response.put("accountsServiceResponse", accountsResponse);
                    
                    logger.info("Отправляем ответ пользователю: {}", response);
                    return ResponseEntity.ok(response);
                })
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "TopUp Service");
        return ResponseEntity.ok(response);
    }
}