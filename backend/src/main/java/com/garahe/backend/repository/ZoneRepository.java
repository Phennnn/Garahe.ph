package com.garahe.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garahe.backend.entity.Zone; // <--- This import is important!

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    
    // This is the line your Controller is looking for:
    List<Zone> findByMallName(String mallName);
}