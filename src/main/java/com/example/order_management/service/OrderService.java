package com.example.order_management.service;

import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderEntity createOrder(OrderRequestDTO dto);
    List<OrderEntity> findByFilter(OrderStatusEnum status, LocalDate start, LocalDate end);
    OrderEntity updateStatus(UUID orderId, String newStatusStr);
    void cancelOrder(UUID orderId);
}
