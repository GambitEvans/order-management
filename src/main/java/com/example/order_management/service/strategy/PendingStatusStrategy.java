package com.example.order_management.service.strategy;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.exception.InsufficientCreditException;
import com.example.order_management.exception.InvalidOrderStatusTransitionException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;

@Component
public final class PendingStatusStrategy implements OrderStatusStrategy {
    @Override
    public SimpleEntry<OrderStatusEnum, PartnerEntity> handleTransition(OrderEntity order, OrderStatusEnum targetStatus) {
        BigDecimal total = order.getTotalValue();
        PartnerEntity partner = order.getPartner();
        if (targetStatus == OrderStatusEnum.APROVADO) {
            
            if (partner.getCreditAvailable().compareTo(total) < 0) {
                throw new InsufficientCreditException(partner.getId());
            }
            
            BigDecimal updatedCredit = partner.getCreditAvailable().subtract(total);
            partner.setCreditAvailable(updatedCredit);
            
            return new SimpleEntry<>(OrderStatusEnum.APROVADO, partner);
        } else if (targetStatus == OrderStatusEnum.CANCELADO) {
            BigDecimal updatedCredit = partner.getCreditAvailable().add(total);
            partner.setCreditAvailable(updatedCredit);
            
            return new SimpleEntry<>(OrderStatusEnum.CANCELADO, partner);
        } else {
            throw new InvalidOrderStatusTransitionException(order.getStatus(), targetStatus);
        }
    }
}
