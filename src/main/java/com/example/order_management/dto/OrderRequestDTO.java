package com.example.order_management.dto;

import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(
        UUID partnerId,
        List<ItemDTO> items
) {}