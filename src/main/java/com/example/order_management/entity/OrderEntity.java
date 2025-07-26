package com.example.order_management.entity;

import com.example.order_management.entity.enums.OrderStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class OrderEntity {
    @Id
    private UUID id;
    
    @ManyToOne
    private PartnerEntity partner;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<ItemEntity> items;
    
    private BigDecimal totalValue;
    
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final OrderEntity order = new OrderEntity();
        
        public Builder id(UUID id) {
            order.id = id;
            return this;
        }
        
        public Builder partner(PartnerEntity partner) {
            order.partner = partner;
            return this;
        }
        
        public Builder items(List<ItemEntity> items) {
            order.items = items;
            return this;
        }
        
        public Builder totalValue(BigDecimal totalValue) {
            order.totalValue = totalValue;
            return this;
        }
        
        public Builder status(OrderStatusEnum status) {
            order.status = status;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            order.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            order.updatedAt = updatedAt;
            return this;
        }
        
        public OrderEntity build() {
            return order;
        }
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(final UUID id) {
        this.id = id;
    }
    
    public PartnerEntity getPartner() {
        return partner;
    }
    
    public void setPartner(final PartnerEntity partner) {
        this.partner = partner;
    }
    
    public List<ItemEntity> getItems() {
        return items;
    }
    
    public void setItems(final List<ItemEntity> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(final BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
    
    public OrderStatusEnum getStatus() {
        return status;
    }
    
    public void setStatus(final OrderStatusEnum status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
