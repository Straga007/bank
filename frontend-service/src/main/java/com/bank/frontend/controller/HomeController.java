package com.bank.frontend.controller;

import com.bank.frontend.service.UserDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    
    private final UserDataService userDataService;

    public HomeController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

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
    public String dashboard(
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient,
            Model model) {
        // Если не аутентифицирован, редиректим на login
        if (principal == null) {
            return "redirect:/login";
        }

        // Получаем данные пользователя через сервис
        Map<String, Object> dashboardData = userDataService.getUserDashboardData(principal, authorizedClient);

        // Добавляем основные данные пользователя
        model.addAllAttributes(dashboardData);

        // Для новых пользователей список транзакций пуст
        model.addAttribute("transactions", java.util.List.of());

        return "dashboard";
    }
}