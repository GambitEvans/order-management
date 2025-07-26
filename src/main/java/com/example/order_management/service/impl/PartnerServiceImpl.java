package com.example.order_management.service.impl;

import com.example.order_management.entity.PartnerEntity;
import com.example.order_management.repository.PartnerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PartnerServiceImpl {
    private final PartnerRepository partnerRepository;
    
    public PartnerServiceImpl(final PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }
    
    public PartnerEntity create(PartnerEntity partner) {
        partner.setId(UUID.randomUUID());
        return partnerRepository.save(partner);
    }
    
    public Optional<PartnerEntity> findById(UUID id) {
        return partnerRepository.findById(id);
    }
}