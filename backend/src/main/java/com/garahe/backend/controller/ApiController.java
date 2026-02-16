package com.garahe.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.garahe.backend.entity.Zone;
import com.garahe.backend.repository.ZoneRepository;

@RestController 
@RequestMapping("/api")
public class ApiController {

    private final ZoneRepository zoneRepository;

    public ApiController(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    // Example URL: localhost:8080/api/zones?mall=Divisoria Area
    @GetMapping("/zones")
    public List<Zone> getZones(@RequestParam String mall) {
        return zoneRepository.findByMallName(mall);
    }
}