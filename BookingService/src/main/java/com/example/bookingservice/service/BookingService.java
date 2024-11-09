package com.example.bookingservice.service;

import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    public ResponseEntity<?> createBooking(Booking booking) {

        if (!isUserValid(booking.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid user. Booking cannot be created.");
        }


        String availabilityUrl = roomServiceUrl + "/api/rooms/roomId/" + booking.getRoomId() + "/availability";
        Boolean isAvailable;
        try {
            isAvailable = restTemplate.getForObject(availabilityUrl, Boolean.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking room availability: " + e.getMessage());
        }

        if (isAvailable == null || !isAvailable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Room is not available for booking.");
        }


        List<Booking> overlappingBookings = bookingRepository.findByRoomIdAndStartTimeBeforeAndEndTimeAfter(
                booking.getRoomId(), booking.getEndTime(), booking.getStartTime());

        if (!overlappingBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Room is already booked for the selected time range.");
        }


        Booking savedBooking = bookingRepository.save(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    private boolean isUserValid(String userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        ResponseEntity<Void> response;

        try {
            response = restTemplate.getForEntity(url, Void.class);
        } catch (RestClientException e) {
            return false;
        }

        return response.getStatusCode().is2xxSuccessful();
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }
}
