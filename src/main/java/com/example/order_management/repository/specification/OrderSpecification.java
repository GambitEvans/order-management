package com.example.order_management.repository.specification;

import com.example.order_management.dto.DateRangeDTO;
import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import jakarta.persistence.criteria.Predicate;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class OrderSpecification {
    
    public static Specification<OrderEntity> filterBy(List<OrderStatusEnum> statuses, DateRangeDTO dateRange) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(root.get("status").in(statuses));

            predicates.add(cb.between(root.get("createdAt"), dateRange.start(), dateRange.end()));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
