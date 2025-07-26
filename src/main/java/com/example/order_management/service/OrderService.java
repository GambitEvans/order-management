package com.example.order_management.service;

import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.entity.enums.OrderStatusEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO dto);
    List<OrderResponseDTO> findByFilter(OrderStatusEnum status, LocalDate start, LocalDate end);
    OrderResponseDTO updateStatus(UUID orderId, String newStatusStr);
    void cancelOrder(UUID orderId);
}
