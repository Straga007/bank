package com.bank.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtTokenRelayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Получаем токен из заголовка Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        ServerHttpRequest.Builder mutate = request.mutate();
        
        // Если есть токен, пробрасываем его дальше
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            mutate.header("Authorization", authHeader);
        }
        
        ServerHttpRequest modifiedRequest = mutate.build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
        
        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}