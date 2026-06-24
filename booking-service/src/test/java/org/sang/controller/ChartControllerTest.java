package org.sang.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sang.client.feign.ClinicFeignClient;
import org.sang.model.Booking;
import org.sang.payload.dto.ClinicDTO;
import org.sang.service.BookingService;
import org.sang.service.impl.BookingChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChartController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security Filters
class ChartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingChartService bookingChartService;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private ClinicFeignClient clinicService;

    private ClinicDTO clinic;
    private List<Booking> bookings;

    @BeforeEach
    void setUp() {
        clinic = new ClinicDTO();
        clinic.setId(10L);
        clinic.setName("Wellness Clinic");

        bookings = Collections.singletonList(new Booking());
    }

    @Test
    void getEarningsChartData_Success() throws Exception {
        // Arrange
        when(clinicService.getClinicByOwner()).thenReturn(ResponseEntity.ok(clinic));
        when(bookingService.getBookingsByClinic(10L)).thenReturn(bookings);

        Map<String, Object> dataPoint = new HashMap<>();
        dataPoint.put("daily", "2026-06-24");
        dataPoint.put("earnings", 100);
        List<Map<String, Object>> chartData = Collections.singletonList(dataPoint);

        when(bookingChartService.generateEarningsChartData(bookings)).thenReturn(chartData);

        // Act & Assert
        mockMvc.perform(get("/api/bookings/chart/earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].daily").value("2026-06-24"))
                .andExpect(jsonPath("$[0].earnings").value(100));
    }

    @Test
    void getBookingsChartData_Success() throws Exception {
        // Arrange
        when(clinicService.getClinicByOwner()).thenReturn(ResponseEntity.ok(clinic));
        when(bookingService.getBookingsByClinic(10L)).thenReturn(bookings);

        Map<String, Object> dataPoint = new HashMap<>();
        dataPoint.put("daily", "2026-06-24");
        dataPoint.put("count", 3);
        List<Map<String, Object>> chartData = Collections.singletonList(dataPoint);

        when(bookingChartService.generateBookingCountChartData(bookings)).thenReturn(chartData);

        // Act & Assert
        mockMvc.perform(get("/api/bookings/chart/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].daily").value("2026-06-24"))
                .andExpect(jsonPath("$[0].count").value(3));
    }
}
