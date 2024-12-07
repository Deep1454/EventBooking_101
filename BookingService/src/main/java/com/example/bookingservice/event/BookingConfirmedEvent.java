package com.example.bookingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingConfirmedEvent {
    private String userId;
    private String roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
