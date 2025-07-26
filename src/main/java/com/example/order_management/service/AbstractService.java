package com.example.order_management.service;

import com.example.order_management.mapper.GenericMapper;

public abstract class AbstractService<D, R> {
    
    protected final GenericMapper<D, R> mapper;
    
    protected AbstractService(final GenericMapper<D, R> mapper) {
        this.mapper = mapper;
    }
    
}
