package com.bank.transfer.client;

import com.bank.transfer.dto.TransferRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class NotificationsServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsServiceClient.class);

    private final WebClient webClient;

    public NotificationsServiceClient(WebClient.Builder webClientBuilder,
                                     @Value("${notifications.service.url:http://localhost:8093/api/notifications}") String notificationsServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(notificationsServiceUrl)
                .build();
    }

    public Mono<Map<String, Object>> sendTransferNotification(String accessToken, String userId, String userName, Double amount, String description) {
        logger.info("Подготовка к отправке уведомления о переводе для пользователя {}: {}", userId, description);
        
        // Подготовим тело запроса для уведомления
        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "userName", userName,
                "type", "transfer",
                "amount", amount,
                "description", description != null ? description : "Перевод средств"
        );
        
        logger.info("Отправка уведомления о переводе: {}", requestBody);

        return webClient.post()
                .uri("/send")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSuccess(response -> logger.info("Уведомление о переводе успешно отправлено: {}", response))
                .doOnError(error -> logger.error("Ошибка при отправке уведомления о переводе", error));
    }
}