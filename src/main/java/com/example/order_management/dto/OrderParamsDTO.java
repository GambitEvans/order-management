package com.example.order_management.dto;

import com.example.order_management.entity.enums.OrderStatusEnum;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public record OrderParamsDTO(
        @RequestParam(required = false)
        List<OrderStatusEnum> statuses,
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @RequestParam(required = false)
        LocalDate start,
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @RequestParam(required = false)
        LocalDate end
) {}
