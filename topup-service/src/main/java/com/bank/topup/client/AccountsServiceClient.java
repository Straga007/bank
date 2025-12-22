package com.bank.topup.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class AccountsServiceClient {

    private final WebClient webClient;

    public AccountsServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8000/api/accounts") // Используем gateway
                .build();
    }

    public Mono<Map> updateAccountBalance(String accessToken, String userId, BigDecimal amount) {
        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "amount", amount
        );

        return webClient.post()
                .uri("/balance/update")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
    }
}