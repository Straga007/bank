package com.bank.frontend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AccountServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceClient.class);

    private final WebClient webClient;
    
    @Value("${gateway.service.url:http://localhost:8000}")
    private String gatewayServiceUrl;

    public AccountServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<Map<String, Object>> getUserBalance(String userId, OAuth2AuthorizedClient authorizedClient) {
        logger.info("Получение баланса для пользователя: {}", userId);
        
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        
        return webClient
            .get()
            .uri(gatewayServiceUrl + "/api/accounts/balance/" + userId)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono((Class<Map<String, Object>>) (Class<?>) Map.class)
            .doOnSuccess(response -> logger.info("Получен баланс для пользователя {}: {}", userId, response))
            .doOnError(error -> logger.error("Ошибка при получении баланса для пользователя {}", userId, error));
    }
    
    public Map<String, Object> getUserBalanceSync(String userId, OAuth2AuthorizedClient authorizedClient) {
        logger.info("Получение баланса для пользователя (синхронно): {}", userId);
        
        try {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            
            Map<String, Object> response = webClient
                .get()
                .uri(gatewayServiceUrl + "/api/accounts/balance/" + userId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono((Class<Map<String, Object>>) (Class<?>) Map.class)
                .block(); // Блокируем до получения результата
                
            logger.info("Получен баланс для пользователя {}: {}", userId, response);
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при получении баланса для пользователя {}", userId, e);
            return null;
        }
    }
}