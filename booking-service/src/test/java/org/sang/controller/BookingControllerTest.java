package org.sang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.client.feign.PaymentFeignClient;
import org.sang.client.feign.ServiceOfferingFeignClient;
import org.sang.client.feign.UserFeignClient;
import org.sang.constant.BookingStatus;
import org.sang.constant.PaymentMethod;
import org.sang.model.Booking;
import org.sang.model.ClinicReport;
import org.sang.payload.dto.ClinicDTO;
import org.sang.payload.dto.ServiceOfferingDTO;
import org.sang.payload.dto.UserDTO;
import org.sang.payload.request.BookingRequest;
import org.sang.payload.response.PaymentLinkResponse;
import org.sang.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security Filters for controller logic testing
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean(name = "userService")
    private UserFeignClient userService;

    @MockitoBean(name = "userFeignClient")
    private UserFeignClient userFeignClient;

    @MockitoBean
    private ClinicFeignClient clinicService;

    @MockitoBean
    private ServiceOfferingFeignClient serviceOfferingService;

    @MockitoBean
    private PaymentFeignClient paymentService;

    private UserDTO user;
    private ClinicDTO clinic;
    private ServiceOfferingDTO serviceOffering;
    private Booking booking;
    private BookingRequest bookingRequest;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2026, 6, 24, 10, 0);

        user = new UserDTO();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john.doe@example.com");

        clinic = new ClinicDTO();
        clinic.setId(10L);
        clinic.setName("Wellness Clinic");

        serviceOffering = new ServiceOfferingDTO();
        serviceOffering.setId(100L);
        serviceOffering.setName("General Consultation");
        serviceOffering.setPrice(50);
        serviceOffering.setDuration(30);

        booking = new Booking();
        booking.setId(1L);
        booking.setClinicId(10L);
        booking.setCustomerId(1L);
        booking.setStartTime(baseTime);
        booking.setEndTime(baseTime.plusMinutes(30));
        booking.setServiceIds(Collections.singleton(100L));
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(50);

        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(baseTime);
        bookingRequest.setServiceIds(Collections.singleton(100L));
    }

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
        when(userService.getUserProfile()).thenReturn(user);
        when(clinicService.getClinicById(10L)).thenReturn(ResponseEntity.ok(clinic));
        when(serviceOfferingService.getServicesByIds(Collections.singleton(100L)))
                .thenReturn(ResponseEntity.ok(Collections.singleton(serviceOffering)));
        when(bookingService.createBooking(any(BookingRequest.class), any(UserDTO.class), any(ClinicDTO.class), any()))
                .thenReturn(booking);
        
        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse("http://payment.url/123", "link_id_123");
        when(paymentService.createPaymentLink(any(Booking.class), eq(PaymentMethod.STRIPE)))
                .thenReturn(ResponseEntity.ok(paymentLinkResponse));

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .param("clinicId", "10")
                        .param("paymentMethod", "STRIPE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payment_link_url").value("http://payment.url/123"))
                .andExpect(jsonPath("$.payment_link_id").value("link_id_123"));
    }

    @Test
    void getBookingsByCustomer_Success() throws Exception {
        // Arrange
        when(userService.getUserProfile()).thenReturn(user);
        when(bookingService.getBookingsByCustomer(user.getId())).thenReturn(Collections.singletonList(booking));
        when(serviceOfferingService.getServicesByIds(Collections.singleton(100L)))
                .thenReturn(ResponseEntity.ok(Collections.singleton(serviceOffering)));
        when(clinicService.getClinicById(10L)).thenReturn(ResponseEntity.ok(clinic));
        when(userFeignClient.getUserById(1L)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/bookings/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].clinicId").value(10))
                .andExpect(jsonPath("$[0].totalPrice").value(50));
    }

    @Test
    void getClinicReport_Success() throws Exception {
        // Arrange
        when(userService.getUserProfile()).thenReturn(user);
        when(clinicService.getClinicByOwner()).thenReturn(ResponseEntity.ok(clinic));
        ClinicReport report = new ClinicReport();
        report.setTotalEarnings(200.0);
        report.setTotalBookings(5);
        report.setCancelledBookings(1);
        report.setTotalRefund(50.0);
        when(bookingService.getClinicReport(10L)).thenReturn(report);

        // Act & Assert
        mockMvc.perform(get("/api/bookings/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEarnings").value(200.0))
                .andExpect(jsonPath("$.totalBookings").value(5))
                .andExpect(jsonPath("$.cancelledBookings").value(1))
                .andExpect(jsonPath("$.totalRefund").value(50.0));
    }

    @Test
    void getBookingsByClinic_Success() throws Exception {
        // Arrange
        when(userService.getUserProfile()).thenReturn(user);
        when(clinicService.getClinicByOwner()).thenReturn(ResponseEntity.ok(clinic));
        when(bookingService.getBookingsByClinic(10L)).thenReturn(Collections.singletonList(booking));
        when(serviceOfferingService.getServicesByIds(Collections.singleton(100L)))
                .thenReturn(ResponseEntity.ok(Collections.singleton(serviceOffering)));
        when(userFeignClient.getUserById(1L)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/bookings/clinic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingById_Success() throws Exception {
        // Arrange
        when(bookingService.getBookingById(1L)).thenReturn(booking);
        when(serviceOfferingService.getServicesByIds(Collections.singleton(100L)))
                .thenReturn(ResponseEntity.ok(Collections.singleton(serviceOffering)));

        // Act & Assert
        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(50));
    }

    @Test
    void updateBookingStatus_Success() throws Exception {
        // Arrange
        Booking updatedBooking = new Booking();
        updatedBooking.setId(1L);
        updatedBooking.setClinicId(10L);
        updatedBooking.setCustomerId(1L);
        updatedBooking.setStartTime(baseTime);
        updatedBooking.setEndTime(baseTime.plusMinutes(30));
        updatedBooking.setServiceIds(Collections.singleton(100L));
        updatedBooking.setStatus(BookingStatus.CONFIRMED);
        updatedBooking.setTotalPrice(50);

        when(bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED)).thenReturn(updatedBooking);
        when(serviceOfferingService.getServicesByIds(Collections.singleton(100L)))
                .thenReturn(ResponseEntity.ok(Collections.singleton(serviceOffering)));
        when(clinicService.getClinicById(10L)).thenReturn(ResponseEntity.ok(clinic));

        // Act & Assert
        mockMvc.perform(put("/api/bookings/1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getBookedSlots_Success() throws Exception {
        // Arrange
        when(bookingService.getBookingsByDate(any(LocalDate.class), eq(10L)))
                .thenReturn(Collections.singletonList(booking));

        // Act & Assert
        mockMvc.perform(get("/api/bookings/slots/clinic/10/date/2026-06-24"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].startTime").exists())
                .andExpect(jsonPath("$[0].endTime").exists());
    }
}
