package com.example.order_management.dto;

import java.math.BigDecimal;

public record PartnerDTO(
        String name,
        BigDecimal creditLimit
) {}
