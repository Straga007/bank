package com.bank.topup.controller;

import com.bank.topup.client.AccountsServiceClient;
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
@RequestMapping("/api/topup")
public class TopUpController {

    private final AccountsServiceClient accountsServiceClient;

    public TopUpController(AccountsServiceClient accountsServiceClient) {
        this.accountsServiceClient = accountsServiceClient;
    }

    @PostMapping("/balance")
    public Mono<ResponseEntity<Map<String, Object>>> topUpBalance(
            @AuthenticationPrincipal Jwt jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient,
            @RequestBody Map<String, Object> request) {
        
        // Получаем сумму пополнения из запроса
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        
        // Получаем токен микросервиса
        String serviceToken = authorizedClient.getAccessToken().getTokenValue();
        
        // Получаем информацию о пользователе из JWT
        String userId = jwt.getSubject();
        String userEmail = jwt.getClaimAsString("email");
        String userName = jwt.getClaimAsString("preferred_username");
        
        // Вызываем Accounts сервис для обновления баланса
        return accountsServiceClient.updateAccountBalance(serviceToken, userId, amount)
                .map(accountsResponse -> {
                    // Создаем ответ
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Баланс успешно пополнен");
                    response.put("amount", amount);
                    response.put("userId", userId);
                    response.put("userName", userName);
                    response.put("userEmail", userEmail);
                    response.put("transactionId", UUID.randomUUID().toString());
                    response.put("accountsServiceResponse", accountsResponse);
                    
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