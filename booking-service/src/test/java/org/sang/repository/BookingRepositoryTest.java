package org.sang.repository;

import org.junit.jupiter.api.Test;
import org.sang.constant.BookingStatus;
import org.sang.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Booking booking = new Booking();
        booking.setClinicId(10L);
        booking.setCustomerId(1L);
        booking.setStartTime(LocalDateTime.now());
        booking.setEndTime(LocalDateTime.now().plusHours(1));
        booking.setServiceIds(Collections.singleton(100L));
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(100);

        // Act
        Booking saved = bookingRepository.save(booking);
        Booking found = bookingRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals(10L, found.getClinicId());
        assertEquals(1L, found.getCustomerId());
    }

    @Test
    void testFindByCustomerId() {
        // Arrange
        Booking b1 = new Booking(null, 10L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), Collections.singleton(100L), BookingStatus.PENDING, 100);
        Booking b2 = new Booking(null, 20L, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), Collections.singleton(100L), BookingStatus.PENDING, 100);
        bookingRepository.save(b1);
        bookingRepository.save(b2);

        // Act
        List<Booking> customer1Bookings = bookingRepository.findByCustomerId(1L);
        List<Booking> customer2Bookings = bookingRepository.findByCustomerId(2L);
        List<Booking> customer3Bookings = bookingRepository.findByCustomerId(3L);

        // Assert
        assertEquals(1, customer1Bookings.size());
        assertEquals(1L, customer1Bookings.get(0).getCustomerId());
        assertEquals(1, customer2Bookings.size());
        assertEquals(0, customer3Bookings.size());
    }

    @Test
    void testFindByClinicId() {
        // Arrange
        Booking b1 = new Booking(null, 10L, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), Collections.singleton(100L), BookingStatus.PENDING, 100);
        Booking b2 = new Booking(null, 10L, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), Collections.singleton(100L), BookingStatus.PENDING, 100);
        bookingRepository.save(b1);
        bookingRepository.save(b2);

        // Act
        List<Booking> clinic10Bookings = bookingRepository.findByClinicId(10L);
        List<Booking> clinic20Bookings = bookingRepository.findByClinicId(20L);

        // Assert
        assertEquals(2, clinic10Bookings.size());
        assertEquals(0, clinic20Bookings.size());
    }
}
