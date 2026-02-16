package com.garahe.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TEMPORARY DEBUG CONTROLLER
 * Use this to directly query bookings from database
 * Access at: /api/debug/bookings
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/bookings")
    public List<Map<String, Object>> getBookingsDirectly() {
        String sql = "SELECT " +
                "b.id, b.booking_code, b.vehicle_plate_number, b.vehicle_type, " +
                "b.start_time, b.duration, b.total_amount, b.status, b.created_at, " +
                "z.name as zone_name, z.mall_name " +
                "FROM bookings b " +
                "LEFT JOIN zones z ON b.zone_id = z.id " +
                "ORDER BY b.created_at DESC";
        
        return jdbcTemplate.queryForList(sql);
    }

    @GetMapping("/users")
    public List<Map<String, Object>> getUsersDirectly() {
        String sql = "SELECT id, full_name, email, phone, role, active, created_at FROM users ORDER BY created_at DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @GetMapping("/zones")
    public List<Map<String, Object>> getZonesDirectly() {
        String sql = "SELECT id, name, total_capacity, available_slots, type, mall_name, hourly_rate FROM zones ORDER BY mall_name, name";
        return jdbcTemplate.queryForList(sql);
    }
}