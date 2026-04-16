package org.sang.service.impl;

import org.sang.constant.BookingStatus;
import org.sang.model.Booking;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingChartService {

	/**
	 * Generate daily earnings chart data.
	 */
	public List<Map<String, Object>> generateEarningsChartData(List<Booking> bookings) {
		// Group bookings by day and calculate total earnings for each day
		Map<String, Integer> earningsByDay = bookings.stream()
				.collect(Collectors.groupingBy(
						booking -> booking.getStartTime().toLocalDate().toString(),
						Collectors.summingInt(Booking::getTotalPrice)
				));

		// Convert the grouped data into chart-friendly format
		return convertToChartData(earningsByDay, "daily", "earnings");
	}

	/**
	 * Generate daily booking count chart data.
	 */
	public List<Map<String, Object>> generateBookingCountChartData(List<Booking> bookings) {
		// Filter only confirmed bookings and group by day to count bookings
		Map<String, Long> countsByDay = bookings.stream()
				.filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
				.collect(Collectors.groupingBy(
						booking -> booking.getStartTime().toLocalDate().toString(),
						Collectors.counting()
				));

		// Convert the grouped data into chart-friendly format
		return convertToChartData(countsByDay, "daily", "count");
	}

	/**
	 * Convert grouped data to chart-friendly format.
	 */
	private <T> List<Map<String, Object>> convertToChartData(Map<String, T> groupedData, String period,
			String dataKey) {
		List<Map<String, Object>> chartData = new ArrayList<>();

		// Convert each entry in the map to a data point
		groupedData.forEach((date, value) -> {
			Map<String, Object> dataPoint = new HashMap<>();
			dataPoint.put(period, date);
			dataPoint.put(dataKey, value);
			chartData.add(dataPoint);
		});

		// Sort the data by date
		chartData.sort(Comparator.comparing(dp -> dp.get(period).toString()));
		return chartData;
	}
}
