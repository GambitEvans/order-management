package com.example.order_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class PartnerEntity {
    @Id
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
}