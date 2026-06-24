package org.sang.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sang.constant.BookingStatus;
import org.sang.model.Booking;
import org.sang.model.ClinicReport;
import org.sang.model.PaymentOrder;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceOfferingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.BookingRequest;
import org.sang.repository.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private UserDTO user;
    private ClinicDTO clinic;
    private ServiceOfferingDTO serviceOffering;
    private Set<ServiceOfferingDTO> services;
    private BookingRequest bookingRequest;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2026, 6, 24, 10, 0); // 10:00 AM

        user = new UserDTO();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john.doe@example.com");

        clinic = new ClinicDTO();
        clinic.setId(10L);
        clinic.setName("Wellness Clinic");
        clinic.setOpenTime(LocalTime.of(8, 0));  // 08:00 AM
        clinic.setCloseTime(LocalTime.of(18, 0)); // 06:00 PM

        serviceOffering = new ServiceOfferingDTO();
        serviceOffering.setId(100L);
        serviceOffering.setName("General Consultation");
        serviceOffering.setPrice(50);
        serviceOffering.setDuration(30); // 30 mins

        services = new HashSet<>(Collections.singletonList(serviceOffering));

        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(baseTime);
        bookingRequest.setServiceIds(new HashSet<>(Collections.singletonList(100L)));
    }

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
        when(bookingRepository.findByClinicId(clinic.getId())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        // Act
        Booking result = bookingService.createBooking(bookingRequest, user, clinic, services);

        // Assert
        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals(user.getId(), result.getCustomerId());
        assertEquals(clinic.getId(), result.getClinicId());
        assertEquals(baseTime, result.getStartTime());
        assertEquals(baseTime.plusMinutes(30), result.getEndTime());
        assertEquals(50, result.getTotalPrice());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_SlotNotAvailable_Overlapping() {
        // Arrange
        Booking existing = new Booking();
        existing.setId(2L);
        existing.setClinicId(clinic.getId());
        existing.setStartTime(baseTime.minusMinutes(15)); // 09:45
        existing.setEndTime(baseTime.plusMinutes(15));   // 10:15 (overlaps with 10:00 - 10:30)
        existing.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findByClinicId(clinic.getId())).thenReturn(Collections.singletonList(existing));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> 
            bookingService.createBooking(bookingRequest, user, clinic, services)
        );
        assertEquals("Slot not available, choose different time.", exception.getMessage());
    }

    @Test
    void createBooking_BeforeClinicOpen() {
        // Arrange
        bookingRequest.setStartTime(baseTime.withHour(7).withMinute(30)); // 07:30 AM (clinic opens at 08:00 AM)

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> 
            bookingService.createBooking(bookingRequest, user, clinic, services)
        );
        assertEquals("Booking time must be within clinic's open hours.", exception.getMessage());
    }

    @Test
    void createBooking_AfterClinicClose() {
        // Arrange
        bookingRequest.setStartTime(baseTime.withHour(17).withMinute(45)); // 17:45 to 18:15 (clinic closes at 18:00)

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> 
            bookingService.createBooking(bookingRequest, user, clinic, services)
        );
        assertEquals("Booking time must be within clinic's open hours.", exception.getMessage());
    }

    @Test
    void getBookingsByCustomer() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCustomerId(1L);
        when(bookingRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(booking));

        // Act
        List<Booking> result = bookingService.getBookingsByCustomer(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getBookingsByClinic() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setClinicId(10L);
        when(bookingRepository.findByClinicId(10L)).thenReturn(Collections.singletonList(booking));

        // Act
        List<Booking> result = bookingService.getBookingsByClinic(10L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getBookingById_Found() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        Booking result = bookingService.getBookingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_NotFound() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Booking result = bookingService.getBookingById(1L);

        // Assert
        assertNull(result);
    }

    @Test
    void bookingSuccess() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);
        
        PaymentOrder order = new PaymentOrder();
        order.setBookingId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.bookingSuccess(order);

        // Assert
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingStatus_Success() throws Exception {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.updateBookingStatus(1L, BookingStatus.CANCELLED);

        // Assert
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void updateBookingStatus_NotFound() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> bookingService.updateBookingStatus(1L, BookingStatus.CANCELLED));
    }

    @Test
    void getClinicReport() {
        // Arrange
        Booking b1 = new Booking(1L, 10L, 1L, baseTime, baseTime.plusHours(1), null, BookingStatus.CONFIRMED, 100);
        Booking b2 = new Booking(2L, 10L, 2L, baseTime, baseTime.plusHours(1), null, BookingStatus.CANCELLED, 50);
        Booking b3 = new Booking(3L, 10L, 3L, baseTime, baseTime.plusHours(1), null, BookingStatus.PENDING, 80);

        when(bookingRepository.findByClinicId(10L)).thenReturn(Arrays.asList(b1, b2, b3));

        // Act
        ClinicReport report = bookingService.getClinicReport(10L);

        // Assert
        assertNotNull(report);
        assertEquals(230.0, report.getTotalEarnings()); // 100 + 50 + 80
        assertEquals(3, report.getTotalBookings());
        assertEquals(1, report.getCancelledBookings());
        assertEquals(50.0, report.getTotalRefund());
    }

    @Test
    void getBookingsByDate_WithNullDate() {
        // Arrange
        Booking b1 = new Booking();
        b1.setId(1L);
        when(bookingRepository.findByClinicId(10L)).thenReturn(Collections.singletonList(b1));

        // Act
        List<Booking> result = bookingService.getBookingsByDate(null, 10L);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getBookingsByDate_WithMatchingDate() {
        // Arrange
        LocalDate date = LocalDate.of(2026, 6, 24);
        Booking b1 = new Booking(1L, 10L, 1L, baseTime, baseTime.plusHours(1), null, BookingStatus.CONFIRMED, 100); // June 24
        Booking b2 = new Booking(2L, 10L, 2L, baseTime.plusDays(1), baseTime.plusDays(1).plusHours(1), null, BookingStatus.CONFIRMED, 100); // June 25

        when(bookingRepository.findByClinicId(10L)).thenReturn(Arrays.asList(b1, b2));

        // Act
        List<Booking> result = bookingService.getBookingsByDate(date, 10L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
