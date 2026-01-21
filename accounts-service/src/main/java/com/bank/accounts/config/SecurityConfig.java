package com.bank.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/balance/update").hasAnyAuthority("SCOPE_openid", "SCOPE_email", "SCOPE_profile")
                .requestMatchers("/transfer-by-username").hasAnyAuthority("SCOPE_openid", "SCOPE_email", "SCOPE_profile")
                .requestMatchers("/balance/**").authenticated()
                .requestMatchers("/**").authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
            );
            
        return http.build();
    }
}