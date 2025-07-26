package com.example.order_management.controller;

import com.example.order_management.dto.ItemDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.dto.UpdateOrderStatusDTO;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Validated OrderRequestDTO dto) {
        var created = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }
    
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> listOrders(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        var list = orderService.findByFilter(status, start, end);
        return ResponseEntity.ok(list.stream().map(this::toDTO).toList());
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Validated UpdateOrderStatusDTO dto
    ) {
        var updated = orderService.updateStatus(id, dto.status());
        return ResponseEntity.ok(toDTO(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    private OrderResponseDTO toDTO(OrderEntity order) {
        List<ItemDTO> items = order.getItems().stream()
                .map(i -> new ItemDTO(i.getProduct(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        
        return new OrderResponseDTO(
                order.getId(),
                order.getPartner().getId(),
                items,
                order.getTotalValue(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}