package com.bank.notifications.controller;

import com.bank.notifications.dto.NotificationRequestDTO;
import com.bank.notifications.model.Notification;
import com.bank.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody NotificationRequestDTO request) {
        logger.info("Получен запрос на отправку уведомления: {}", request);
        
        try {
            // Создаем модель уведомления из DTO
            Notification notification = new Notification(
                    request.getUserId(),
                    request.getUserName(),
                    request.getType(),
                    request.getAmount(),
                    request.getDescription()
            );
            
            // Отправляем уведомление через сервис
            notificationService.sendNotification(notification);
            
            // Подготавливаем успешный ответ
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Уведомление успешно отправлено");
            response.put("notificationId", UUID.randomUUID().toString());
            response.put("timestamp", notification.getTimestamp());
            
            logger.info("Уведомление успешно отправлено: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Ошибка при отправке уведомления", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Ошибка при отправке уведомления: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Notifications Service");
        return ResponseEntity.ok(response);
    }
}