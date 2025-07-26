package com.example.order_management.service.strategy;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;

public interface OrderStatusStrategy {
    OrderStatusEnum handleTransition(OrderEntity order, OrderStatusEnum targetStatus);
}