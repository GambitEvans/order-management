package com.example.order_management.consumer;

import com.example.order_management.config.RabbitMQConfig;
import com.example.order_management.dto.OrderNotificationDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveNotification(OrderNotificationDTO notification) {
        System.out.println("Notificação recebida: " + notification);
    }
}
