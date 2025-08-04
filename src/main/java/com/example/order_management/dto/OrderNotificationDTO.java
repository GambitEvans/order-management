package com.example.order_management.dto;

import java.io.Serializable;
import java.util.UUID;

public record OrderNotificationDTO(UUID orderId, String status) implements Serializable { }
