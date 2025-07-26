package com.example.order_management.service.strategy.impl;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.repository.PartnerRepository;
import com.example.order_management.service.strategy.OrderStatusStrategy;
import org.springframework.stereotype.Component;

@Component
public class ApprovedStatusStrategy implements OrderStatusStrategy {
    
    private PartnerRepository partnerRepository;
    
    @Override
    public OrderStatusEnum handleTransition(final OrderEntity order, final OrderStatusEnum targetStatus) {
        if (targetStatus == OrderStatusEnum.PENDENTE) {
            throw new IllegalStateException("Cannot revert approved order to pending.");
        } else if (targetStatus == OrderStatusEnum.CANCELADO) {
            PartnerEntity partner = order.getPartner();
            partner.setCreditAvailable(partner.getCreditAvailable().add(order.getTotalValue()));
            
            partnerRepository.save(partner);
            
            return OrderStatusEnum.CANCELADO;
        } else {
            throw new IllegalStateException("Invalid transition from APPROVED to " + targetStatus);
        }
    }
}
