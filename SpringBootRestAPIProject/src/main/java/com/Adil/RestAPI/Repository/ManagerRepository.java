package com.Adil.RestAPI.Repository;

import com.Adil.RestAPI.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    Optional<Manager> findByManagerIdAndIsActiveTrue(UUID managerId);
}


