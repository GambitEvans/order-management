package com.example.order_management.controller;

import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.dto.UpdateOrderStatusDTO;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
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

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/orders")
public class OrderController extends AbstractController{
    
    private final OrderService orderService;
    
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Operation(summary = "Criar pedidos")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Validated OrderRequestDTO dto) {
        return ok(orderService.createOrder(dto));
    }
    
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> listOrders(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ok(orderService.findByFilter(status, start, end));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Validated UpdateOrderStatusDTO dto
    ) {
        return ok(orderService.updateStatus(id, dto.status()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
        orderService.cancelOrder(id);
        return noContent();
    }
    
}