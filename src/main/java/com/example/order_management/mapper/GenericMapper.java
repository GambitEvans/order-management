package com.example.order_management.mapper;

import java.util.List;

public interface GenericMapper<D, R> {
    R toResponse(D domain);
    List<R> toResponse(List<D> domain);
}
