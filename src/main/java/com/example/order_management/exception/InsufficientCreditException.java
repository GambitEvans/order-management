package com.example.order_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InsufficientCreditException extends RuntimeException {
    public InsufficientCreditException(UUID partnerId) {
        super("Parceiro com o " + partnerId + " não tem crédito suficiente");
    }
}
