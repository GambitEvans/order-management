package com.example.order_management.repository.specification;

import com.example.order_management.entity.OrderEntity;
import com.example.order_management.entity.enums.OrderStatusEnum;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class OrderSpecification {
    
    public static Specification<OrderEntity> filterBy(OrderStatusEnum status, LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if(nonNull(status)) predicates.add(cb.equal(root.get("status"), status));
            if(nonNull(start)) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start.atStartOfDay()));
            if(nonNull(end)) predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end.atTime(LocalTime.MAX)));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
