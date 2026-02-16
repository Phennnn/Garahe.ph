package com.garahe.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.garahe.backend.entity.Booking;
import com.garahe.backend.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Find booking by unique code
    Optional<Booking> findByBookingCode(String bookingCode);
    
    // Find all bookings for a user
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    
    // Find bookings by status
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    // Find bookings by guest email (for guests without accounts)
    List<Booking> findByGuestEmailOrderByCreatedAtDesc(String guestEmail);
    
    // Find active bookings for a zone (to check availability)
    @Query("SELECT b FROM Booking b WHERE b.zone.id = :zoneId " +
           "AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND b.startTime <= :endTime AND b.endTime >= :startTime")
    List<Booking> findActiveBookingsForZone(
        @Param("zoneId") Long zoneId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    // Get all bookings for today
    @Query("SELECT b FROM Booking b WHERE DATE(b.startTime) = CURRENT_DATE")
    List<Booking> findTodaysBookings();
    
    // Count bookings by zone (for analytics)
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.zone.id = :zoneId AND b.status = 'COMPLETED'")
    Long countCompletedBookingsByZone(@Param("zoneId") Long zoneId);
    
    // Get total revenue (for analytics)
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.paymentStatus = 'PAID'")
    Double getTotalRevenue();
}