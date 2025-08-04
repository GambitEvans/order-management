package com.example.order_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PartnerNotFoundException extends RuntimeException {
    public PartnerNotFoundException(UUID partnerId){
        super("Parceiro com o id " + partnerId + " não foi encontrado");
    }
}
