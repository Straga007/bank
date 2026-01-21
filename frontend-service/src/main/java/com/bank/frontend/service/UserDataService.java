package com.bank.frontend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserDataService {

    private static final Logger logger = LoggerFactory.getLogger(UserDataService.class);

    private final AccountServiceClient accountServiceClient;

    public UserDataService(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    public Map<String, Object> getUserDashboardData(OAuth2User principal, OAuth2AuthorizedClient authorizedClient) {
        Map<String, Object> attributes = principal.getAttributes();

        // Генерируем уникальный номер счета на основе имени пользователя
        String username = (String) attributes.getOrDefault("preferred_username", "unknown");
        String userId = (String) attributes.get("sub"); // Используем subject (sub) как userId
        String accountNumber = "40702810" + Math.abs(username.hashCode()) % 10000000000L;
        if (accountNumber.length() > 20) {
            accountNumber = accountNumber.substring(0, 20);
        }

        // Получаем реальный баланс пользователя из аккаунт-сервиса
        String balance = "0 ₽"; // Значение по умолчанию
        String errorMessage = null;
        try {
            Map<String, Object> balanceResponse = accountServiceClient.getUserBalanceSync(userId, authorizedClient);

            if (balanceResponse != null && "success".equals(balanceResponse.get("status"))) {
                Object balanceValue = balanceResponse.get("balance");
                if (balanceValue != null) {
                    balance = balanceValue.toString() + " ₽";
                }
            } else if (balanceResponse != null) {
                // Если статус не success, проверяем наличие сообщения об ошибке
                Object message = balanceResponse.get("message");
                if (message != null) {
                    errorMessage = message.toString();
                }
            }
        } catch (Exception e) {
            // Если не удалось получить баланс, используем значение по умолчанию
            errorMessage = "Не удалось получить баланс: " + e.getMessage();
            logger.error("Ошибка при получении баланса пользователя {}", userId, e);
        }

        // Формируем данные пользователя
        Map<String, Object> userData = Map.of(
            "name", attributes.getOrDefault("name", username),
            "email", attributes.getOrDefault("email", ""),
            "accountNumber", accountNumber,
            "balance", balance,
            "currency", "RUB",
            "lastLogin", "Сегодня"
        );

        // Добавляем сообщение об ошибке, если оно есть
        if (errorMessage != null) {
            return Map.of(
                "user", userData,
                "balanceError", errorMessage
            );
        } else {
            return Map.of(
                "user", userData
            );
        }
    }
}