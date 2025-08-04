package com.example.order_management.service;

import com.example.order_management.BaseTest;
import com.example.order_management.dto.ItemDTO;
import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.mapper.OrderMapper;
import com.example.order_management.repository.OrderRepository;
import com.example.order_management.repository.PartnerRepository;
import com.example.order_management.service.impl.OrderServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceImplTest extends BaseTest {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PartnerRepository partnerRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private OrderMapper mapper;
    
    @Autowired
    private OrderServiceImpl orderService;
    
    private UUID partnerId;
    
    private final List<ItemDTO> items = List.of(
            new ItemDTO("Product A", 2, BigDecimal.valueOf(100)) // total = 200
    );
    
    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        partnerRepository.deleteAll();
    }
    
    @Test
    void createOrder_success() {
        PartnerEntity partner = PartnerEntity.builder()
                .name("Partner Test")
                .creditAvailable(BigDecimal.valueOf(1000))
                .build();
        partnerId = partnerRepository.save(partner).getId();
        
        OrderRequestDTO dto = new OrderRequestDTO(partnerId, items);
        
        OrderResponseDTO response = orderService.createOrder(dto);
        
        assertNotNull(response);
        assertEquals(partnerId, response.partnerId());
        
        Optional<OrderEntity> savedOrder = orderRepository.findById(response.id());
        assertTrue(savedOrder.isPresent());
        assertEquals(BigDecimal.valueOf(200).setScale(2, HALF_UP), savedOrder.get().getTotalValue());
    }
    
    @Test
    void createOrder_insufficientCredit_throwsException() {
        PartnerEntity partner = PartnerEntity.builder()
                .name("Low Credit")
                .creditAvailable(BigDecimal.valueOf(100))
                .build();
        partnerId = partnerRepository.save(partner).getId();
        
        OrderRequestDTO dto = new OrderRequestDTO(partnerId, items);
        
        Exception ex = assertThrows(IllegalStateException.class, () -> orderService.createOrder(dto));
        assertEquals("Insufficient credit for partner", ex.getMessage());
    }
    
    @Test
    void cancelOrder_shouldSetCancelled_when_different_of_approved_and_cancelled() {
        PartnerEntity partner = partnerRepository.save(PartnerEntity.builder()
                .name("Partner X")
                .creditAvailable(BigDecimal.valueOf(1000))
                .build());
        
        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .partner(partner)
                .status(OrderStatusEnum.PENDENTE)
                .totalValue(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        orderService.cancelOrder(order.getId());
        
        OrderEntity updated = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatusEnum.CANCELADO, updated.getStatus());
    }
    
    @Test
    void cancelOrder_alreadyCancelled_shouldThrow() {
        PartnerEntity partner = partnerRepository.save(PartnerEntity.builder()
                .name("Partner Cancelado")
                .creditAvailable(BigDecimal.valueOf(1000))
                .build());
        
        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .partner(partner)
                .status(OrderStatusEnum.CANCELADO)
                .totalValue(BigDecimal.valueOf(100))
                .build());
        
        Exception ex = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(order.getId()));
        assertEquals("Order is already cancelled", ex.getMessage());
    }
    
    @Test
    @Transactional
    void findByFilter_shouldReturnPagedResults() {
        LocalDate createdAt = LocalDate.now();
        PartnerEntity partner = partnerRepository.save(PartnerEntity.builder()
                .name("Partner Filter")
                .creditAvailable(BigDecimal.valueOf(1000))
                .build());
        
        orderRepository.save(OrderEntity.builder()
                .partner(partner)
                .status(OrderStatusEnum.PENDENTE)
                .totalValue(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        OrderParamsDTO params = new OrderParamsDTO(List.of(OrderStatusEnum.PENDENTE), createdAt, createdAt.plusDays(1));
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<OrderResponseDTO> page = orderService.findByFilter(params, pageable);
        
        assertFalse(page.isEmpty());
    }
    
    @Test
    void updateStatus_shouldTransitionAndSendNotification() {
        PartnerEntity partner = partnerRepository.save(PartnerEntity.builder()
                .name("Partner Status")
                .creditAvailable(BigDecimal.valueOf(1000))
                .build());
        
        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .partner(partner)
                .status(OrderStatusEnum.PENDENTE)
                .totalValue(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        OrderResponseDTO response = orderService.updateStatus(order.getId(), "aprovado");
        
        assertNotNull(response);
        OrderEntity updated = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatusEnum.APROVADO, updated.getStatus());
    }
    
    @Test
    void updateStatus_shouldntUpdateWrongStatus() {
        PartnerEntity partner = partnerRepository.save(PartnerEntity.builder()
                .name("Partner Invalid")
                .creditAvailable(BigDecimal.valueOf(1000))
                .build());
        
        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .partner(partner)
                .status(OrderStatusEnum.PENDENTE)
                .totalValue(BigDecimal.valueOf(100))
                .build());
        
        String statusStr = "PROCESSADO";
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.updateStatus(order.getId(), statusStr));
        assertEquals("Invalid order status: " + statusStr, ex.getMessage());
    }
    
    private OrderEntity buildFakeOrder() {
        return OrderEntity.builder()
                .id(UUID.randomUUID())
                .status(OrderStatusEnum.ENTREGUE)
                .totalValue(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .partner(PartnerEntity.builder()
                        .id(UUID.randomUUID())
                        .name("Fake Partner")
                        .creditAvailable(BigDecimal.valueOf(1000))
                        .build())
                .build();
    }
}
