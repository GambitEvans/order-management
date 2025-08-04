package com.example.order_management.service.impl;

import com.example.order_management.config.RabbitMQConfig;
import com.example.order_management.dto.DateRangeDTO;
import com.example.order_management.dto.ItemDTO;
import com.example.order_management.dto.OrderNotificationDTO;
import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.entity.ItemEntity;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.exception.InsufficientCreditException;
import com.example.order_management.exception.InvalidOrderStatusException;
import com.example.order_management.exception.OrderAlreadyCancelledException;
import com.example.order_management.exception.OrderNotFoundException;
import com.example.order_management.exception.PartnerNotFoundException;
import com.example.order_management.mapper.OrderMapper;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.repository.PartnerRepository;
import com.example.order_management.repository.specification.OrderSpecification;
import com.example.order_management.service.AbstractService;
import com.example.order_management.service.OrderService;
import com.example.order_management.service.strategy.OrderStatusStrategy;
import com.example.order_management.service.strategy.OrderStatusStrategyEnum;
import com.example.order_management.service.strategy.impl.ApprovedStatusStrategy;
import com.example.order_management.util.DateUtil;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.requireNonNullElse;

@Service
public class OrderServiceImpl extends AbstractService<OrderEntity, OrderResponseDTO> implements OrderService {
    
    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
   
    private final RabbitTemplate rabbitTemplate;
    
    public OrderServiceImpl(final OrderMapper mapper, final OrderRepository orderRepository, final PartnerRepository partnerRepository, final RabbitTemplate rabbitTemplate) {
        super(mapper);
        this.orderRepository = orderRepository;
        this.partnerRepository = partnerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        UUID partnerId = dto.partnerId();
        PartnerEntity partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerNotFoundException(partnerId));
        
        BigDecimal total = calculateTotal(dto.items());
        
        if (partner.getCreditAvailable().compareTo(total) < 0) {
            throw new InsufficientCreditException(partnerId);
        }
        
        OrderEntity order = buildOrderEntity(dto, partner, total);
        
        return mapper.toResponse(orderRepository.save(order));
    }
    
    @Override
    public void cancelOrder(UUID orderId) {
        OrderEntity order = findOrderOrThrow(orderId);
        
        if (order.getStatus() == OrderStatusEnum.CANCELADO) {
            throw new OrderAlreadyCancelledException(orderId);
        }
        
        if (order.getStatus() == OrderStatusEnum.APROVADO) {
            ApprovedStatusStrategy approvedStatusStrategy = new ApprovedStatusStrategy();
            approvedStatusStrategy.handleTransition(order, OrderStatusEnum.CANCELADO);
        }
        
        order.setStatus(OrderStatusEnum.CANCELADO);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
    }
    
    @Override
    public Page<OrderResponseDTO> findByFilter(OrderParamsDTO orderParams, Pageable pageable) {
        List<OrderStatusEnum> statuses = requireNonNullElse(orderParams.statuses(), Collections.emptyList());
        
        DateRangeDTO dateRange = DateUtil.normalizeDateRange(orderParams.start(), orderParams.end());
        
        Page<OrderEntity> orders = orderRepository.findAll(OrderSpecification.filterBy(statuses, dateRange), pageable);
        
        return orders.map(mapper::toResponse);
    }
    
    @Override
    public List<OrderResponseDTO> getLastDailyReport() {
        OrderParamsDTO orderParams = initializeOrderParams();
        
        DateRangeDTO dateRange = DateUtil.normalizeDateRange(orderParams.start(), orderParams.end());
        
        List<OrderEntity> orders = orderRepository.findReportWithCache(orderParams.statuses(), dateRange);
        
        return mapper.toResponse(orders);
    }
    
    private OrderParamsDTO initializeOrderParams() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        return new OrderParamsDTO(
                List.of(OrderStatusEnum.ENTREGUE, OrderStatusEnum.CANCELADO),
                yesterday,
                yesterday
        );
    }
    
    @Transactional
    @Override
    public OrderResponseDTO updateStatus(UUID orderId, String newStatusStr) {
        OrderEntity order = findOrderOrThrow(orderId);
        OrderStatusEnum newStatus = parseStatus(newStatusStr);
        
        OrderStatusStrategy statusStrategy = OrderStatusStrategyEnum.getStrategyFor(order.getStatus());
        
        SimpleEntry<OrderStatusEnum, PartnerEntity> result = statusStrategy.handleTransition(order, newStatus);
        
        OrderStatusEnum nextStatus = result.getKey();
        PartnerEntity partner = result.getValue();
        
        partnerRepository.save(partner);
        
        order.setStatus(nextStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                new OrderNotificationDTO(order.getId(), order.getStatus().name()));
        
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
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
    
    private OrderStatusEnum parseStatus(String statusStr) {
        try {
            return OrderStatusEnum.valueOf(statusStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidOrderStatusException(statusStr);
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