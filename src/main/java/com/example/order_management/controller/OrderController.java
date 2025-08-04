package com.example.order_management.controller;

import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.dto.UpdateOrderStatusDTO;
import com.example.order_management.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public ResponseEntity<Page<OrderResponseDTO>> listOrders(
            @ModelAttribute OrderParamsDTO orderParams,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ok(orderService.findByFilter(orderParams, pageable));
    }
    
    @GetMapping("/report")
    public ResponseEntity<List<OrderResponseDTO>> getLastDailyReport() {
        return ok(orderService.getLastDailyReport());
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