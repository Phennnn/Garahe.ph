package com.garahe.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garahe.backend.entity.Booking;
import com.garahe.backend.entity.User;
import com.garahe.backend.entity.Zone;
import com.garahe.backend.repository.BookingRepository;
import com.garahe.backend.repository.ZoneRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ZoneRepository zoneRepository;

    public BookingService(BookingRepository bookingRepository, ZoneRepository zoneRepository) {
        this.bookingRepository = bookingRepository;
        this.zoneRepository = zoneRepository;
    }

    /**
     * Create a new booking
     */
    @Transactional
    public Booking createBooking(Booking booking) {
        // Generate unique booking code
        booking.setBookingCode(generateBookingCode());
        
        // Set booking date
        booking.setBookingDate(LocalDateTime.now());
        
        // Calculate end time based on duration
        booking.setEndTime(booking.getStartTime().plusHours(booking.getDuration()));
        
        // Calculate total amount
        Zone zone = booking.getZone();
        double totalAmount = zone.getHourlyRate() * booking.getDuration();
        booking.setTotalAmount(totalAmount);
        
        // Set initial status
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setPaymentStatus(Booking.PaymentStatus.UNPAID);
        
        // Generate QR code data
        booking.setQrCode(generateQRCodeData(booking));
        
        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update zone availability
        updateZoneAvailability(zone.getId());
        
        return savedBooking;
    }

    /**
     * Generate unique booking code (format: GRH-YYYY-NNNNNN)
     */
    private String generateBookingCode() {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        Random random = new Random();
        int randomNum = 100000 + random.nextInt(900000);
        return "GRH-" + year + "-" + randomNum;
    }

    /**
     * Generate QR code data
     */
    private String generateQRCodeData(Booking booking) {
        return booking.getBookingCode() + "|" + 
               booking.getStartTime().toString() + "|" + 
               booking.getZone().getId();
    }

    /**
     * Update zone availability after booking
     */
    @Transactional
    public void updateZoneAvailability(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
            .orElseThrow(() -> new RuntimeException("Zone not found"));
        
        // Count active bookings for this zone
        List<Booking> activeBookings = bookingRepository.findActiveBookingsForZone(
            zoneId,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1)
        );
        
        // Update available slots
        int bookedSlots = activeBookings.size();
        int availableSlots = Math.max(0, zone.getTotalCapacity() - bookedSlots);
        zone.setAvailableSlots(availableSlots);
        
        zoneRepository.save(zone);
    }

    /**
     * Confirm booking and mark as paid (mock payment)
     */
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        
        return bookingRepository.save(booking);
    }

    /**
     * Cancel booking
     */
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        
        Booking cancelledBooking = bookingRepository.save(booking);
        
        // Update zone availability
        updateZoneAvailability(booking.getZone().getId());
        
        return cancelledBooking;
    }

    /**
     * Get user bookings
     */
    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get guest bookings
     */
    public List<Booking> getGuestBookings(String email) {
        return bookingRepository.findByGuestEmailOrderByCreatedAtDesc(email);
    }

    /**
     * Find booking by code
     */
    public Booking findByBookingCode(String code) {
        return bookingRepository.findByBookingCode(code)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    /**
     * Get all bookings (for admin)
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Check if zone has available slots for a time period
     */
    public boolean isZoneAvailable(Long zoneId, LocalDateTime startTime, LocalDateTime endTime) {
        Zone zone = zoneRepository.findById(zoneId)
            .orElseThrow(() -> new RuntimeException("Zone not found"));
        
        List<Booking> activeBookings = bookingRepository.findActiveBookingsForZone(
            zoneId, startTime, endTime
        );
        
        return activeBookings.size() < zone.getTotalCapacity();
    }

    /**
     * Get today's bookings
     */
    public List<Booking> getTodaysBookings() {
        return bookingRepository.findTodaysBookings();
    }

    /**
     * Get total revenue (for analytics)
     */
    public Double getTotalRevenue() {
        Double revenue = bookingRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }
}