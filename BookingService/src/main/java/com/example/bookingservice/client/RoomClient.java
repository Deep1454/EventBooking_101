package com.example.bookingservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface RoomClient {

    Logger log = LoggerFactory.getLogger(RoomClient.class);

    @GetExchange("/api/rooms/roomId/{roomId}/availability")
    @CircuitBreaker(name = "rooms", fallbackMethod = "fallbackMethod")
    @Retry(name = "rooms")
    boolean checkRoomAvailability(@PathVariable("roomId") String roomId);


    // Fallback Method
    default boolean fallbackMethod(String roomId, Throwable throwable) {
        log.error("Fallback invoked for roomId {}. Reason: {}", roomId, throwable.getMessage());
        throw new RuntimeException("Room Service is unavailable. Fallback executed.");
    }
}
