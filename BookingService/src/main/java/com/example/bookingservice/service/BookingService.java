package com.example.bookingservice.service;

import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${room-service.url}")
    private String roomServiceUrl;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public Booking createBooking(Booking booking) {
        // Validate user before proceeding
        if (!isUserValid(booking.getUserId())) {
            throw new RuntimeException("Invalid user. Booking cannot be created.");
        }

        // Check room availability by calling RoomService
        String availabilityUrl = "http://localhost:2011/api/rooms/roomId/" + booking.getRoomId() + "/availability";
        Boolean isAvailable;

        try {
            isAvailable = restTemplate.getForObject(availabilityUrl, Boolean.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error checking room availability.", e);
        }

        if (isAvailable == null || !isAvailable) {
            throw new RuntimeException("Room is not available for booking.");
        }

        // Check for double-booking
        List<Booking> overlappingBookings = bookingRepository.findByRoomIdAndStartTimeBeforeAndEndTimeAfter(
                booking.getRoomId(), booking.getEndTime(), booking.getStartTime());

        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException("Room is already booked for the selected time range.");
        }

        // Save the new booking if room is available and no overlapping bookings exist
        return bookingRepository.save(booking);
    }

    private boolean isUserValid(String userId) {
        String url ="http://localhost:2010/api/users/" + userId; // Call UserService to validate
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        return response.getStatusCode().is2xxSuccessful(); // User exists if status is 2xx
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }
}
