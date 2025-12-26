package com.bank.notifications.service;

import com.bank.notifications.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    // Временное хранилище для уведомлений (в реальном используем базу данных)
    private final List<Notification> notifications = new ArrayList<>();
    
    public void sendNotification(Notification notification) {
        logger.info("Отправка уведомления: {}", notification);
        
        // Здесь должна быть логика отправки уведомления
        notifications.add(notification);
        logger.info("Уведомление успешно отправлено для пользователя {}: {}", notification.getUserId(), notification.getDescription());
        
        // заглушка для тестирования
    }
    
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }
    
    public List<Notification> getNotificationsByUserId(String userId) {
        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .toList();
    }
}