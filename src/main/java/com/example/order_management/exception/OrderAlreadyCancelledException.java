package com.example.order_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class OrderAlreadyCancelledException extends RuntimeException {
    public OrderAlreadyCancelledException(UUID orderId) {
        super("Pedido com o id " + orderId + " já está cancelado");
    }
}
