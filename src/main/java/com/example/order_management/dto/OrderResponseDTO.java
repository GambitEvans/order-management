package com.example.order_management.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        UUID partnerId,
        List<ItemDTO> items,
        BigDecimal totalValue,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
