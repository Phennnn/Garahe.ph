package com.garahe.backend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garahe.backend.entity.Booking;
import com.garahe.backend.entity.User;
import com.garahe.backend.entity.Zone;
import com.garahe.backend.repository.UserRepository;
import com.garahe.backend.repository.ZoneRepository;
import com.garahe.backend.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ZoneRepository zoneRepository;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, 
                           ZoneRepository zoneRepository,
                           UserRepository userRepository) {
        this.bookingService = bookingService;
        this.zoneRepository = zoneRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new booking
     * POST /api/bookings
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> bookingData) {
        try {
            Booking booking = new Booking();
            
            // Get zone
            Long zoneId = Long.valueOf(bookingData.get("zoneId").toString());
            Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
            booking.setZone(zone);
            
            // Parse start time
            String startTimeStr = bookingData.get("startTime").toString();
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            booking.setStartTime(startTime);
            
            // Set duration
            int duration = Integer.parseInt(bookingData.get("duration").toString());
            booking.setDuration(duration);
            
            // Set vehicle info
            booking.setVehiclePlateNumber(bookingData.get("vehiclePlateNumber").toString());
            booking.setVehicleType(bookingData.get("vehicleType").toString());
            
            // Check if user is logged in or guest
            if (bookingData.containsKey("userId") && bookingData.get("userId") != null) {
                Long userId = Long.valueOf(bookingData.get("userId").toString());
                User user = userRepository.findById(userId).orElse(null);
                booking.setUser(user);
            } else {
                // Guest booking
                booking.setGuestName(bookingData.get("guestName").toString());
                booking.setGuestEmail(bookingData.get("guestEmail").toString());
                booking.setGuestPhone(bookingData.get("guestPhone").toString());
            }
            
            // Check availability
            if (!bookingService.isZoneAvailable(zoneId, startTime, startTime.plusHours(duration))) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No available slots for the selected time"));
            }
            
            // Create booking
            Booking savedBooking = bookingService.createBooking(booking);
            
            return ResponseEntity.ok(savedBooking);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Confirm booking (mock payment)
     * POST /api/bookings/{id}/confirm
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.confirmBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cancel booking
     * POST /api/bookings/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get booking by code
     * GET /api/bookings/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getBookingByCode(@PathVariable String code) {
        try {
            Booking booking = bookingService.findByBookingCode(code);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get user bookings
     * GET /api/bookings/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            List<Booking> bookings = bookingService.getUserBookings(user);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get guest bookings
     * GET /api/bookings/guest/{email}
     */
    @GetMapping("/guest/{email}")
    public ResponseEntity<?> getGuestBookings(@PathVariable String email) {
        try {
            List<Booking> bookings = bookingService.getGuestBookings(email);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all bookings (admin)
     * GET /api/bookings
     */
    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get today's bookings
     * GET /api/bookings/today
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodaysBookings() {
        try {
            List<Booking> bookings = bookingService.getTodaysBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get booking statistics (for dashboard)
     * GET /api/bookings/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getBookingStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRevenue", bookingService.getTotalRevenue());
            stats.put("todaysBookings", bookingService.getTodaysBookings().size());
            stats.put("totalBookings", bookingService.getAllBookings().size());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}