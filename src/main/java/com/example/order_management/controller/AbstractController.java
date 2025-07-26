package com.example.order_management.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;

public abstract class AbstractController {
    
    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
    
    protected <T> ResponseEntity<List<T>> ok(List<T> body) {
        return ResponseEntity.ok(body);
    }
    
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
