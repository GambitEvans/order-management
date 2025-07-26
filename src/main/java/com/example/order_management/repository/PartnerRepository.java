package com.example.order_management.repository;

import com.example.order_management.entity.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, UUID> {

}
