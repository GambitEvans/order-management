package com.example.order_management.dto;

import java.math.BigDecimal;

public record ItemDTO(
        String product,
        int quantity,
        BigDecimal unitPrice
) {}
