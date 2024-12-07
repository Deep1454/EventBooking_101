package com.example.bookingservice.service;

import com.example.bookingservice.client.RoomClient;
import com.example.bookingservice.event.BookingConfirmedEvent;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Slf4j
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomClient roomClient;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${user-service.url}")
    private String userServiceUrl;

   @Autowired
   private  KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;

    public ResponseEntity<?> createBooking(Booking booking) {

        if (!isUserValid(booking.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid user. Booking cannot be created.");
        }

        boolean isAvailable;
        try {
            isAvailable = roomClient.checkRoomAvailability(booking.getRoomId());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Fallback executed")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Room Service is currently unavailable. Please try again later.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }

        if (!isAvailable) {
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

        BookingConfirmedEvent bookingConfirmedEvent = new BookingConfirmedEvent(
                savedBooking.getUserId(),
                savedBooking.getRoomId(),
                savedBooking.getStartTime(),
                savedBooking.getEndTime()
        );

        try {
            log.info("Sending BookingConfirmedEvent: {} to Kafka topic booking-confirmed", bookingConfirmedEvent);
            kafkaTemplate.send("booking-confirmed", bookingConfirmedEvent).get();
            log.info("Successfully sent BookingConfirmedEvent to Kafka topic booking-confirmed");
        } catch (Exception e) {
            log.error("Failed to send BookingConfirmedEvent to Kafka: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process booking. Please try again later.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
    }

    private boolean isUserValid(String userId) {
        String url = userServiceUrl + "/api/users/" + userId;
        ResponseEntity<Void> response;

        try {
            response = restTemplate.getForEntity(url, Void.class);
        } catch (Exception e) {
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
