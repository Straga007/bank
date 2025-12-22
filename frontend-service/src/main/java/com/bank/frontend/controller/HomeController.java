package com.bank.frontend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal OAuth2User principal, 
                           @RequestParam(value = "error", required = false) String error,
                           Model model) {
        // Если пользователь уже аутентифицирован, редиректим на dashboard
        if (principal != null) {
            return "redirect:/dashboard";
        }
        
        // Если есть параметр error, передаем его в представление
        if (error != null) {
            model.addAttribute("error", true);
        }
        
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal OAuth2User principal) {
        // Если пользователь уже аутентифицирован, редиректим на dashboard
        if (principal != null) {
            return "redirect:/dashboard";
        }
        
        // Перенаправляем на стандартную форму регистрации Keycloak
        return "redirect:http://localhost:8180/realms/bank/protocol/openid-connect/registrations?client_id=bank-frontend&response_type=code&scope=openid%20profile%20email&redirect_uri=http://localhost:8080/login/oauth2/code/keycloak";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        // Если не аутентифицирован, редиректим на login
        if (principal == null) {
            return "redirect:/login";
        }

        Map<String, Object> attributes = principal.getAttributes();

        // Генерируем уникальный номер счета на основе имени пользователя
        String username = (String) attributes.getOrDefault("preferred_username", "unknown");
        String accountNumber = "40702810" + Math.abs(username.hashCode()) % 10000000000L;
        if (accountNumber.length() > 20) {
            accountNumber = accountNumber.substring(0, 20);
        }

        model.addAttribute("user", Map.of(
            "name", attributes.getOrDefault("name", username),
            "email", attributes.getOrDefault("email", ""),
            "accountNumber", accountNumber,
            "balance", "0 ₽",
            "currency", "RUB",
            "lastLogin", "Сегодня"
        ));

        // Для новых пользователей список транзакций пуст
        model.addAttribute("transactions", java.util.List.of());

        return "dashboard";
    }
}