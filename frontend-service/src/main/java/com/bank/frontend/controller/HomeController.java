package com.bank.frontend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

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
        return "redirect:/oauth2/authorization/keycloak?prompt=register";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        // Если не аутентифицирован, редиректим на login
        if (principal == null) {
            return "redirect:/login";
        }

        Map<String, Object> attributes = principal.getAttributes();

        model.addAttribute("user", Map.of(
            "name", attributes.getOrDefault("name", "Иван Иванов"),
            "email", attributes.getOrDefault("email", "ivan@bank.com"),
            "accountNumber", "40702810000000012345",
            "balance", "1 250 000 ₽",
            "currency", "RUB",
            "lastLogin", "Сегодня, 14:30"
        ));

        model.addAttribute("transactions", java.util.List.of(
            Map.of("date", "2024-01-15", "description", "Перевод на карту", "amount", "-15 000 ₽"),
            Map.of("date", "2024-01-14", "description", "Зарплата", "amount", "+85 000 ₽"),
            Map.of("date", "2024-01-10", "description", "Оплата услуг", "amount", "-5 200 ₽"),
            Map.of("date", "2024-01-05", "description", "Кэшбэк", "amount", "+1 500 ₽")
        ));

        return "dashboard";
    }
}