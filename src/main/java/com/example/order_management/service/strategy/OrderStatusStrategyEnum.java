package com.example.order_management.service.strategy;

import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.exception.StrategyNotFoundException;

import java.util.function.Supplier;

public enum OrderStatusStrategyEnum {
    APROVADO(ApprovedStatusStrategy::new),
    PENDENTE(PendingStatusStrategy::new);
    
    private final Supplier<? extends OrderStatusStrategy> supplier;
    
    OrderStatusStrategyEnum(Supplier<? extends OrderStatusStrategy> supplier) {
        this.supplier = supplier;
    }
    
    public OrderStatusStrategy getInstance() {
        return supplier.get();
    }
    
    public static OrderStatusStrategy getStrategyFor(OrderStatusEnum status) {
        try {
            return OrderStatusStrategyEnum.valueOf(status.name()).getInstance();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new StrategyNotFoundException("Não foi encontrada nenhuma estratégia para o status : " + status);
        }
    }
}
