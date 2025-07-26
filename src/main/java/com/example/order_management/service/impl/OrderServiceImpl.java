package com.example.order_management.service.impl;

import com.example.order_management.dto.ItemDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.entity.ItemEntity;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.mapper.OrderMapper;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.repository.PartnerRepository;
import com.example.order_management.repository.specification.OrderSpecification;
import com.example.order_management.service.AbstractService;
import com.example.order_management.service.OrderService;
import com.example.order_management.service.strategy.OrderStatusStrategy;
import com.example.order_management.service.strategy.OrderStatusStrategyEnum;
import com.example.order_management.service.strategy.impl.ApprovedStatusStrategy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrderServiceImpl extends AbstractService<OrderEntity, OrderResponseDTO> implements OrderService {
    
    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    
    public OrderServiceImpl(final OrderRepository orderRepository, final PartnerRepository partnerRepository, final OrderMapper mapper) {
        super(mapper);
        this.orderRepository = orderRepository;
        this.partnerRepository = partnerRepository;
    }
    
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        PartnerEntity partner = partnerRepository.findById(dto.partnerId())
                .orElseThrow(() -> new RuntimeException("Partner not found"));
        
        BigDecimal total = calculateTotal(dto.items());
        
        if (partner.getCreditAvailable().compareTo(total) < 0) {
            throw new IllegalStateException("Insufficient credit for partner");
        }
        
        OrderEntity order = buildOrderEntity(dto, partner, total);
        
        return mapper.toResponse(orderRepository.save(order));
    }
    
    @Override
    public void cancelOrder(UUID orderId) {
        OrderEntity order = findOrderOrThrow(orderId);
        
        if (order.getStatus() == OrderStatusEnum.CANCELADO) {
            throw new IllegalStateException("Order is already cancelled");
        }
        
        if (order.getStatus() == OrderStatusEnum.APROVADO) {
            ApprovedStatusStrategy approvedStatusStrategy = new ApprovedStatusStrategy();
            approvedStatusStrategy.handleTransition(order, OrderStatusEnum.CANCELADO);
        }
        
        order.setStatus(OrderStatusEnum.CANCELADO);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
    }
    
    public List<OrderResponseDTO> findByFilter(OrderStatusEnum status, LocalDate start, LocalDate end) {
        List<OrderEntity> orders = orderRepository.findAll(OrderSpecification.filterBy(status, start, end));
        return mapper.toResponse(orders);
    }
    
    @Transactional
    public OrderResponseDTO updateStatus(UUID orderId, String newStatusStr) {
        OrderEntity order = findOrderOrThrow(orderId);
        OrderStatusEnum newStatus = parseStatus(newStatusStr);
        
        OrderStatusStrategy statusStrategy = OrderStatusStrategyEnum.getStrategyFor(order.getStatus());
        
        OrderStatusEnum correctNextStatus = statusStrategy.handleTransition(order, newStatus);
        
        order.setStatus(correctNextStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        return mapper.toResponse(order);
    }
    
    private List<ItemEntity> mapItems(List<ItemDTO> itemDTOs, OrderEntity order) {
        return itemDTOs.stream().map(dto ->
                ItemEntity.builder()
                    .order(order)
                    .product(dto.product())
                    .quantity(dto.quantity())
                    .unitPrice(dto.unitPrice())
                    .build()
        ).toList();
    }
    
    private BigDecimal calculateTotal(List<ItemDTO> items) {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private OrderEntity findOrderOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    private OrderStatusEnum parseStatus(String statusStr) {
        try {
            return OrderStatusEnum.valueOf(statusStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid order status: " + statusStr);
        }
    }
    
    private OrderEntity buildOrderEntity(OrderRequestDTO dto, PartnerEntity partner, BigDecimal total) {
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .partner(partner)
                .status(OrderStatusEnum.PENDENTE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .totalValue(total)
                .build();
        
        List<ItemEntity> items = mapItems(dto.items(), order);
        
        order.setItems(items);
        
        return order;
    }
}