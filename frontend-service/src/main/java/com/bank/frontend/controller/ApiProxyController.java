package com.bank.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class ApiProxyController {

    private final WebClient webClient;
    
    @Value("${gateway.service.url:http://localhost:8000}")
    private String gatewayServiceUrl;

    public ApiProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostMapping("/topup/balance")
    @ResponseBody
    public Mono<ResponseEntity<Object>> topUpBalance(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody Map<String, Object> payload) {
        
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        
        return webClient
            .post()
            .uri(gatewayServiceUrl + "/api/topup/balance")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .bodyValue(payload)
            .retrieve()
            .toEntity(Object.class)
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    @PostMapping("/transfer/create")
    @ResponseBody
    public Mono<ResponseEntity<Object>> createTransfer(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody Map<String, Object> payload) {
        
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        
        // Проверим, передается ли username вместо userId
        String recipientId = (String) payload.get("recipientUserId");
        String recipientUsername = (String) payload.get("recipientUsername");
        
        String uri;
        Map<String, Object> transferPayload;
        
        if (recipientUsername != null && !recipientUsername.isEmpty()) {
            // Используем новый эндпоинт для перевода по username
            uri = gatewayServiceUrl + "/api/transfer/create-by-username";
            transferPayload = Map.of(
                    "senderUserId", oauth2User.getName(), // Используем subject (sub) из JWT токена как ID отправителя
                    "recipientUsername", recipientUsername, // передаем username как recipientUsername
                    "amount", payload.get("amount"),
                    "description", payload.get("description")
            );
        } else {
            // Используем старый эндпоинт для перевода по userId
            uri = gatewayServiceUrl + "/api/transfer/create";
            transferPayload = Map.of(
                    "senderUserId", oauth2User.getName(), // Используем subject (sub) из JWT токена как ID отправителя
                    "recipientUserId", recipientId,
                    "amount", payload.get("amount"),
                    "description", payload.get("description")
            );
        }
        
        return webClient
            .post()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .bodyValue(transferPayload)
            .retrieve()
            .toEntity(Object.class)  // Получаем полный ответ, включая тело
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}