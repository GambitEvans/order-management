package com.example.order_management.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDTO(
        @NotNull(message = "O status não pode ser nulo") String status
) {}