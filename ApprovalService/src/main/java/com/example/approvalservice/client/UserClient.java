package com.example.approvalservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface UserClient {

    Logger log = LoggerFactory.getLogger(UserClient.class);

    @GetExchange("/api/users/{id}/role")
    @CircuitBreaker(name = "approvals", fallbackMethod = "fallbackMethod")
    @Retry(name = "approvals")
    String checkUserRole(@PathVariable("id") String userId);


    default String fallbackMethod(String userId, Throwable throwable) {
        log.error("Fallback invoked for userId {}. Reason: {}", userId, throwable.getMessage());
        throw new RuntimeException("Room Service is unavailable. Fallback executed.");
    }
}
