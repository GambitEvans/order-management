package com.example.order_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class ItemEntity {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private OrderEntity order;
    
    private String product;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final ItemEntity item = new ItemEntity();
        
        public Builder id(Long id) {
            item.id = id;
            return this;
        }
        
        public Builder order(OrderEntity order) {
            item.order = order;
            return this;
        }
        
        public Builder product(String product) {
            item.product = product;
            return this;
        }
        
        public Builder quantity(Integer quantity) {
            item.quantity = quantity;
            return this;
        }
        
        public Builder unitPrice(BigDecimal unitPrice) {
            item.unitPrice = unitPrice;
            return this;
        }
        
        public ItemEntity build() {
            return item;
        }
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public OrderEntity getOrder() {
        return order;
    }
    
    public void setOrder(final OrderEntity order) {
        this.order = order;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(final String product) {
        this.product = product;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(final BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
