package com.example.order_management.mapper;

import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper extends GenericMapper<OrderEntity, OrderResponseDTO> {
}
