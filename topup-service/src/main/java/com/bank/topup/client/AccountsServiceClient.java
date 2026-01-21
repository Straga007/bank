package com.bank.topup.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class AccountsServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AccountsServiceClient.class);

    private final WebClient webClient;

    public AccountsServiceClient(WebClient.Builder webClientBuilder,
                               @Value("${account.service.url:http://localhost:8000/api/accounts}") String accountServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(accountServiceUrl)
                .build();
    }

    public Mono<Map> updateAccountBalance(String accessToken, String userId, String username, BigDecimal amount) {
        logger.info("Отправка запроса на обновление баланса: userId={}, username={}, amount={}", userId, username, amount);
        
        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "username", username,
                "amount", amount
        );

        logger.info("Тело запроса: {}", requestBody);

        return webClient.post()
                .uri("/balance/update")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnSuccess(response -> logger.info("Получен ответ от Accounts сервиса: {}", response))
                .doOnError(error -> logger.error("Ошибка при вызове Accounts сервиса", error));
    }
}