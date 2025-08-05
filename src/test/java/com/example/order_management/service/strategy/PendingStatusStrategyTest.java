package com.example.order_management.service.strategy;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.exception.InsufficientCreditException;
import com.example.order_management.exception.InvalidOrderStatusTransitionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PendingStatusStrategyTest {
    private final PendingStatusStrategy strategy = new PendingStatusStrategy();
    
    @Test
    void shouldApproveAndSubtractCredit_whenCreditIsSufficient() {
        PartnerEntity partner = PartnerEntity.builder()
                .id(UUID.randomUUID())
                .creditAvailable(BigDecimal.valueOf(500))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .partner(partner)
                .totalValue(BigDecimal.valueOf(200))
                .status(OrderStatusEnum.PENDENTE)
                .build();
        
        SimpleEntry<OrderStatusEnum, PartnerEntity> result = strategy.handleTransition(order, OrderStatusEnum.APROVADO);
        
        assertEquals(OrderStatusEnum.APROVADO, result.getKey());
        assertEquals(BigDecimal.valueOf(300), result.getValue().getCreditAvailable());
    }
    
    @Test
    void shouldThrowException_whenApprovingWithInsufficientCredit() {
        PartnerEntity partner = PartnerEntity.builder()
                .id(UUID.randomUUID())
                .creditAvailable(BigDecimal.valueOf(100))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .partner(partner)
                .totalValue(BigDecimal.valueOf(200))
                .status(OrderStatusEnum.PENDENTE)
                .build();
        
        Exception ex = assertThrows(InsufficientCreditException.class, () ->
                strategy.handleTransition(order, OrderStatusEnum.APROVADO)
        );
        
        assertEquals("Parceiro com o " + partner.getId() + " não tem crédito suficiente", ex.getMessage());
    }
    
    @Test
    void shouldCancelAndRestoreCredit() {
        PartnerEntity partner = PartnerEntity.builder()
                .creditAvailable(BigDecimal.valueOf(300))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .partner(partner)
                .totalValue(BigDecimal.valueOf(100))
                .status(OrderStatusEnum.PENDENTE)
                .build();
        
        SimpleEntry<OrderStatusEnum, PartnerEntity> result = strategy.handleTransition(order, OrderStatusEnum.CANCELADO);
        
        assertEquals(OrderStatusEnum.CANCELADO, result.getKey());
        assertEquals(BigDecimal.valueOf(400), result.getValue().getCreditAvailable());
    }
    
    @Test
    void shouldThrowException_whenTargetIsInvalid() {
        PartnerEntity partner = PartnerEntity.builder()
                .creditAvailable(BigDecimal.valueOf(300))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .partner(partner)
                .totalValue(BigDecimal.valueOf(100))
                .status(OrderStatusEnum.PENDENTE)
                .build();
        
        Exception ex = assertThrows(InvalidOrderStatusTransitionException.class, () ->
                strategy.handleTransition(order, OrderStatusEnum.ENTREGUE)
        );
        
        assertEquals("Tramsição inválida do status " + order.getStatus() + " para " + OrderStatusEnum.ENTREGUE, ex.getMessage());
    }
}
