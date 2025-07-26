package com.example.order_management.service.strategy.impl;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.repository.PartnerRepository;
import com.example.order_management.service.strategy.OrderStatusStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PendingStatusStrategy implements OrderStatusStrategy {
    
    private PartnerRepository partnerRepository;
    
    @Override
    public OrderStatusEnum handleTransition(OrderEntity order, OrderStatusEnum targetStatus) {
        if (targetStatus == OrderStatusEnum.APROVADO) {
            BigDecimal total = order.getTotalValue();
            PartnerEntity partner = order.getPartner();
            
            if (partner.getCreditAvailable().compareTo(total) < 0) {
                throw new IllegalStateException("Insufficient credit to approve the order.");
            }
            
            partner.setCreditAvailable(partner.getCreditAvailable().subtract(total));
            partnerRepository.save(partner);
            return OrderStatusEnum.APROVADO;
        } else if (targetStatus == OrderStatusEnum.CANCELADO) {
            return OrderStatusEnum.CANCELADO;
        } else {
            throw new IllegalStateException("Invalid transition from PENDING to " + targetStatus);
        }
    }
}
