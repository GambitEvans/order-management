package com.example.order_management.mapper;

import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper extends GenericMapper<OrderEntity, OrderResponseDTO> {
    
    @Override
    @Mapping(source = "partner.id", target = "partnerId")
    OrderResponseDTO toResponse(OrderEntity entity);
}
