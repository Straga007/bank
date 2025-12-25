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
    
    // Временное хранилище для уведомлений (в реальном приложении использовалась бы база данных)
    private final List<Notification> notifications = new ArrayList<>();
    
    public void sendNotification(Notification notification) {
        logger.info("Отправка уведомления: {}", notification);
        
        // Здесь может быть логика отправки уведомления (email, SMS, push-уведомление и т.д.)
        // Для простоты в этом примере мы просто добавляем уведомление в список и логируем его
        
        notifications.add(notification);
        logger.info("Уведомление успешно отправлено для пользователя {}: {}", notification.getUserId(), notification.getDescription());
        
        // В реальном приложении здесь может быть:
        // - Отправка email
        // - Отправка SMS
        // - Отправка push-уведомления
        // - Запись в базу данных
        // - Отправка в очередь (например, RabbitMQ или Kafka)
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