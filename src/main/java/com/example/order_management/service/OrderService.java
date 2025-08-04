package com.example.order_management.service;

import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO dto);
    Page<OrderResponseDTO> findByFilter(OrderParamsDTO orderParams, Pageable pageable);
    List<OrderResponseDTO> getLastDailyReport();
    OrderResponseDTO updateStatus(UUID orderId, String newStatusStr);
    void cancelOrder(UUID orderId);
}
