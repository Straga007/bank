package com.bank.transfer.controller;

import com.bank.transfer.dto.TransferRequestDTO;
import com.bank.transfer.service.AccountServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class TransferController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    private final AccountServiceClient accountServiceClient;

    public TransferController(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    // Новый эндпоинт для создания перевода по username получателя
    @PostMapping("/create-by-username")
    public Mono<ResponseEntity<Object>> createTransferByUsername(
            @AuthenticationPrincipal Jwt jwt,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient,
            @RequestBody TransferRequestDTO transferRequest) {
        
        logger.info("Получен запрос на создание перевода по username: {}", transferRequest);
        logger.info("recipientUsername из запроса: {}", transferRequest.getRecipientUsername());
        logger.info("recipientUserId из запроса: {}", transferRequest.getRecipientUserId());
        logger.info("amount из запроса: {}", transferRequest.getAmount());
        
        // Получаем ID отправителя из JWT
        String senderUserId = jwt.getSubject(); // subject (sub) содержит ID пользователя
        // Получаем токен из authorizedClient
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        
        String recipientUsername = transferRequest.getRecipientUsername();
        // Если recipientUsername null или пустой, пробуем использовать recipientUserId как username
        if (recipientUsername == null || recipientUsername.isEmpty()) {
            recipientUsername = transferRequest.getRecipientUserId();
        }
        
        return accountServiceClient.transferBetweenAccountsByUsername(accessToken, senderUserId, recipientUsername, transferRequest.getAmount())
                .map(response -> {
                    // Преобразуем ответ от accounts-service в Map для совместимости с frontend
                    Map<String, Object> result = new HashMap<>();
                    result.put("status", response.get("status"));
                    result.put("transactionId", UUID.randomUUID().toString());
                    result.put("senderUserId", senderUserId);
                    result.put("recipientUsername", transferRequest.getRecipientUsername()); // передаем username
                    result.put("amount", response.get("amount"));
                    result.put("description", transferRequest.getDescription());
                    return ResponseEntity.ok((Object) result);
                })
                .onErrorReturn(ResponseEntity.badRequest().body((Object) Map.of(
                    "status", "error",
                    "message", "Не удалось выполнить перевод по username",
                    "transactionId", UUID.randomUUID().toString()
                )));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Transfer Service");
        return ResponseEntity.ok(response);
    }
}