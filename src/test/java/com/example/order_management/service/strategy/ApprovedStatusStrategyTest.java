package com.example.order_management.service.strategy;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.service.strategy.impl.ApprovedStatusStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApprovedStatusStrategyTest {
    private final ApprovedStatusStrategy strategy = new ApprovedStatusStrategy();
    
    @Test
    void shouldAddCreditAndReturnCanceled_whenTargetIsCancelado() {
        PartnerEntity partner = PartnerEntity.builder()
                .id(UUID.randomUUID())
                .creditAvailable(BigDecimal.valueOf(500))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .partner(partner)
                .totalValue(BigDecimal.valueOf(200))
                .status(OrderStatusEnum.APROVADO)
                .build();
        
        SimpleEntry<OrderStatusEnum, PartnerEntity> result = strategy.handleTransition(order, OrderStatusEnum.CANCELADO);
        
        assertEquals(OrderStatusEnum.CANCELADO, result.getKey());
        assertEquals(BigDecimal.valueOf(700), result.getValue().getCreditAvailable());
    }
    
    @Test
    void shouldThrowException_whenTargetIsPendente() {
        PartnerEntity partner = PartnerEntity.builder()
                .id(UUID.randomUUID())
                .creditAvailable(BigDecimal.valueOf(500))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .partner(partner)
                .totalValue(BigDecimal.valueOf(200))
                .status(OrderStatusEnum.APROVADO)
                .build();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                strategy.handleTransition(order, OrderStatusEnum.PENDENTE)
        );
        
        assertEquals("Cannot revert approved order to pending.", exception.getMessage());
    }
    
    @Test
    void shouldThrowException_whenTargetIsInvalid() {
        PartnerEntity partner = PartnerEntity.builder()
                .id(UUID.randomUUID())
                .creditAvailable(BigDecimal.valueOf(500))
                .build();
        
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .partner(partner)
                .totalValue(BigDecimal.valueOf(200))
                .status(OrderStatusEnum.APROVADO)
                .build();
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                strategy.handleTransition(order, OrderStatusEnum.ENVIADO)
        );
        
        assertEquals("Invalid transition from APPROVED to ENVIADO", exception.getMessage());
    }
}
