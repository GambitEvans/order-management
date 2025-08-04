package com.example.order_management.exception;

import com.example.order_management.entity.enums.OrderStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderStatusTransitionException extends RuntimeException {
    
    public InvalidOrderStatusTransitionException(OrderStatusEnum currentStatus, OrderStatusEnum targetStatus) {
        super("Tramsição inválida do status " + currentStatus + " para " + targetStatus);
    }
}