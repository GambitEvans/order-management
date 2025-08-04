package com.example.order_management.repository;

import com.example.order_management.dto.DateRangeDTO;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import com.example.order_management.repository.specification.OrderSpecification;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {
    
    @Cacheable(
            value = "ordersReport",
            key = "'lastDailyReport:' + #dateRange.start() + '-' + #dateRange.end()",
            condition = "#statuses != null && #statuses.contains(T(com.example.order_management.entity.enums.OrderStatusEnum).ENTREGUE) && #statuses.contains(T(com.example.order_management.entity.enums.OrderStatusEnum).CANCELADO)"
    )
    default List<OrderEntity> findReportWithCache(List<OrderStatusEnum> statuses, DateRangeDTO dateRange) {
        return findAll(OrderSpecification.filterBy(statuses, dateRange));
    }
}
