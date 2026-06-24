package org.sang.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sang.constant.BookingStatus;
import org.sang.model.Booking;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BookingChartServiceTest {

    private BookingChartService bookingChartService;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        bookingChartService = new BookingChartService();
        baseTime = LocalDateTime.of(2026, 6, 24, 10, 0);
    }

    @Test
    void generateEarningsChartData() {
        // Arrange
        Booking b1 = new Booking(1L, 10L, 1L, baseTime, baseTime.plusHours(1), null, BookingStatus.CONFIRMED, 100);
        Booking b2 = new Booking(2L, 10L, 2L, baseTime.plusDays(1), baseTime.plusDays(1).plusHours(1), null, BookingStatus.CONFIRMED, 150);
        Booking b3 = new Booking(3L, 10L, 3L, baseTime, baseTime.plusHours(1), null, BookingStatus.CANCELLED, 50);

        List<Booking> bookings = Arrays.asList(b2, b1, b3); // Out of order list to check sorting

        // Act
        List<Map<String, Object>> result = bookingChartService.generateEarningsChartData(bookings);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Two distinct days: 2026-06-24 and 2026-06-25

        // Verify sorted order (June 24 should be first)
        assertEquals("2026-06-24", result.get(0).get("daily"));
        assertEquals(150, result.get(0).get("earnings")); // b1 (100) + b3 (50) = 150

        assertEquals("2026-06-25", result.get(1).get("daily"));
        assertEquals(150, result.get(1).get("earnings")); // b2 (150)
    }

    @Test
    void generateBookingCountChartData() {
        // Arrange
        // Only CONFIRMED bookings should be counted
        Booking b1 = new Booking(1L, 10L, 1L, baseTime, baseTime.plusHours(1), null, BookingStatus.CONFIRMED, 100);
        Booking b2 = new Booking(2L, 10L, 2L, baseTime.plusDays(1), baseTime.plusDays(1).plusHours(1), null, BookingStatus.CONFIRMED, 150);
        Booking b3 = new Booking(3L, 10L, 3L, baseTime, baseTime.plusHours(1), null, BookingStatus.CANCELLED, 50);
        Booking b4 = new Booking(4L, 10L, 4L, baseTime, baseTime.plusHours(1), null, BookingStatus.CONFIRMED, 100);

        List<Booking> bookings = Arrays.asList(b4, b3, b2, b1);

        // Act
        List<Map<String, Object>> result = bookingChartService.generateBookingCountChartData(bookings);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Two distinct days with confirmed bookings

        // Verify counts and sorting
        assertEquals("2026-06-24", result.get(0).get("daily"));
        assertEquals(2L, result.get(0).get("count")); // b1 and b4 are CONFIRMED on June 24

        assertEquals("2026-06-25", result.get(1).get("daily"));
        assertEquals(1L, result.get(1).get("count")); // b2 is CONFIRMED on June 25
    }
}
