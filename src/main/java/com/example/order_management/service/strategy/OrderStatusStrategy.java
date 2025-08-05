package com.example.order_management.service.strategy;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;

import java.util.AbstractMap.SimpleEntry;

public sealed interface OrderStatusStrategy permits ApprovedStatusStrategy, PendingStatusStrategy {
    SimpleEntry<OrderStatusEnum, PartnerEntity> handleTransition(OrderEntity order, OrderStatusEnum targetStatus);
}