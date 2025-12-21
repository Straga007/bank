package com.bank.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Controller
public class HomeController {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak-admin.username}")
    private String adminUsername;

    @Value("${keycloak-admin.password}")
    private String adminPassword;

    @Value("${keycloak-admin.client-id}")
    private String adminClientId;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(keycloakServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
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
    public String showRegistrationForm() {
        // Показываем нашу собственную форму регистрации
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {
        try {
            // Проверяем совпадение паролей
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                return "register";
            }
            
            // Получаем токен администратора
            String adminToken = getAdminAccessToken();
            
            if (adminToken == null) {
                model.addAttribute("error", "Не удалось получить доступ к системе регистрации");
                return "register";
            }
            
            // Создаем пользователя через REST API
            boolean success = createUserViaRest(adminToken, username, firstName, lastName, email, password);
            
            if (success) {
                model.addAttribute("success", true);
            } else {
                model.addAttribute("error", "Не удалось зарегистрировать пользователя. Возможно, такой пользователь уже существует.");
            }

            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при регистрации пользователя: " + e.getMessage());
            return "register";
        }
    }

    private String getAdminAccessToken() {
        String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";
        
        String requestBody = "grant_type=password&client_id=" + adminClientId + 
                            "&username=" + adminUsername + 
                            "&password=" + adminPassword;
        
        try {
            String response = webClient.post()
                    .uri(tokenUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Простой парсинг JSON для извлечения токена
            if (response != null && response.contains("\"access_token\":\"")) {
                int start = response.indexOf("\"access_token\":\"") + 16;
                int end = response.indexOf("\"", start);
                return response.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private boolean createUserViaRest(String accessToken, String username, String firstName, 
                                     String lastName, String email, String password) {
        String createUserUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";
        
        // Подготавливаем данные пользователя
        Map<String, Object> userRepresentation = new HashMap<>();
        userRepresentation.put("username", username);
        userRepresentation.put("firstName", firstName);
        userRepresentation.put("lastName", lastName);
        userRepresentation.put("email", email);
        userRepresentation.put("enabled", true);
        
        // Подготавливаем учетные данные
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);
        
        List<Map<String, Object>> credentialsList = new ArrayList<>();
        credentialsList.add(credentials);
        userRepresentation.put("credentials", credentialsList);
        
        try {
            webClient.post()
                    .uri(createUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue(userRepresentation)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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