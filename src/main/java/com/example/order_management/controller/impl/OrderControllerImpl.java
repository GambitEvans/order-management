package com.example.order_management.controller.impl;

import com.example.order_management.controller.AbstractController;
import com.example.order_management.controller.OrderController;
import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.dto.UpdateOrderStatusDTO;
import com.example.order_management.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderControllerImpl extends AbstractController implements OrderController {
    
    private final OrderService orderService;
    
    public OrderControllerImpl(final OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Override
    public ResponseEntity<OrderResponseDTO> createOrder(OrderRequestDTO dto) {
        return ok(orderService.createOrder(dto));
    }
    
    @Override
    public ResponseEntity<Page<OrderResponseDTO>> listOrders(
            OrderParamsDTO orderParams,
            Pageable pageable
    ) {
        return ok(orderService.findByFilter(orderParams, pageable));
    }
    
    @Override
    public ResponseEntity<List<OrderResponseDTO>> getLastDailyReport() {
        return ok(orderService.getLastDailyReport());
    }
    
    @Override
    public ResponseEntity<OrderResponseDTO> updateStatus(
            UUID id,
            UpdateOrderStatusDTO dto
    ) {
        return ok(orderService.updateStatus(id, dto.status()));
    }
    
    @Override
    public ResponseEntity<Void> cancelOrder(UUID id) {
        orderService.cancelOrder(id);
        return noContent();
    }
    
}