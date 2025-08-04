package com.example.order_management.service.strategy.impl;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.exception.InvalidOrderStatusTransitionException;
import com.example.order_management.exception.OrderAlreadyCancelledException;
import com.example.order_management.service.strategy.OrderStatusStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;

@Component
public class ApprovedStatusStrategy implements OrderStatusStrategy {
    @Override
    public SimpleEntry<OrderStatusEnum, PartnerEntity> handleTransition(final OrderEntity order, final OrderStatusEnum targetStatus) {
        if (targetStatus == OrderStatusEnum.PENDENTE) {
            throw new OrderAlreadyCancelledException(order.getId());
        } else if (targetStatus == OrderStatusEnum.CANCELADO) {
            PartnerEntity partner = order.getPartner();
            BigDecimal updatedCredit = partner.getCreditAvailable().add(order.getTotalValue());
            partner.setCreditAvailable(updatedCredit);
            
            return new SimpleEntry<>(OrderStatusEnum.CANCELADO, partner);
        } else {
            throw new InvalidOrderStatusTransitionException(order.getStatus(), targetStatus);
        }
    }
}
