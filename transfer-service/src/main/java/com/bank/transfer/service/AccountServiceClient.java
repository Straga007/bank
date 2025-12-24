package com.bank.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AccountServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceClient.class);

    private final WebClient webClient;

    public AccountServiceClient(WebClient.Builder webClientBuilder,
                               @Value("${account.service.url:http://localhost:8000/api/accounts}") String accountServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(accountServiceUrl)
                .build();
    }

    // Новый метод для перевода по username получателя
    public Mono<Map<String, Object>> transferBetweenAccountsByUsername(String accessToken, String senderUserId, String recipientUsername, Double amount) {
        logger.info("Подготовка к выполнению перевода от {} к пользователю с username {}, сумма: {}", senderUserId, recipientUsername, amount);
        
        // Подготовим тело запроса
        Map<String, Object> requestBody = Map.of(
                "senderUserId", senderUserId,
                "recipientUsername", recipientUsername,
                "amount", amount
        );
        
        logger.info("Отправка запроса на перевод по username: {}", requestBody);

        return webClient.post()
                .uri("/transfer-by-username") // эндпоинт для перевода по username
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSuccess(response -> logger.info("Получен ответ от accounts-service: {}", response))
                .doOnError(error -> {
                    logger.error("Ошибка при выполнении перевода по username1", error);
                    if (error instanceof WebClientResponseException) {
                        logger.error("Код ошибки: {}, Тело ошибки: {}", 
                            ((WebClientResponseException) error).getStatusCode(),
                            ((WebClientResponseException) error).getResponseBodyAsString());
                    }
                });
    }
}