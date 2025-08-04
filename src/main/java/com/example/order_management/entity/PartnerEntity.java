package com.example.order_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class PartnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private BigDecimal creditLimit;
    private BigDecimal creditAvailable;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(final UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
    
    public void setCreditLimit(final BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
    
    public BigDecimal getCreditAvailable() {
        return creditAvailable;
    }
    
    public void setCreditAvailable(final BigDecimal creditAvailable) {
        this.creditAvailable = creditAvailable;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final PartnerEntity instance;
        
        public Builder() {
            instance = new PartnerEntity();
        }
        
        public Builder id(UUID id) {
            instance.id = id;
            return this;
        }
        
        public Builder name(String name) {
            instance.name = name;
            return this;
        }
        
        public Builder creditLimit(BigDecimal creditLimit) {
            instance.creditAvailable = creditLimit;
            return this;
        }
        
        public Builder creditAvailable(BigDecimal creditAvailable) {
            instance.creditAvailable = creditAvailable;
            return this;
        }
        
        public PartnerEntity build() {
            return instance;
        }
    }
}