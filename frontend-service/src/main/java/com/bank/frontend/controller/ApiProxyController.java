package com.bank.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
}