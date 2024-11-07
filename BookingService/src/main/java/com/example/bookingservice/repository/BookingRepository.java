package com.example.bookingservice.repository;

import com.example.bookingservice.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByRoomIdAndStartTimeBeforeAndEndTimeAfter(String roomId, LocalDateTime endTime, LocalDateTime startTime);

}