package com.bank.accounts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NotificationService {
    
    @Value("${notifications.service.url:http://localhost:8093}")
    private String notificationServiceUrl;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public void sendNotification(String message) {
        WebClient webClient = webClientBuilder.build();
        
        webClient.post()
            .uri(notificationServiceUrl + "/api/notifications")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just("{\"message\": \"" + message + "\"}"), String.class)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe();
    }
}