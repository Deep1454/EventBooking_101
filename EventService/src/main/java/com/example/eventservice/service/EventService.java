package com.example.eventservice.service;

import com.example.eventservice.model.Event;
import com.example.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public ResponseEntity<?> createEvent(Event event) {

        String roleCheckUrl = userServiceUrl + "/api/users/" + event.getOrganizerId() + "/usertype";
        String roleResponse;
        try {
            roleResponse = restTemplate.getForObject(roleCheckUrl, String.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking user role: " + e.getMessage());
        }


        if (roleResponse != null && roleResponse.startsWith("User type: ")) {
            String userType = roleResponse.substring(11);
            if ("student".equalsIgnoreCase(userType) && event.getExpectedAttendees() > 20) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Students cannot create events with more than 20 attendees.");
            }

            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User does not have permission to create this event.");
        }
    }

    public ResponseEntity<Event> getEventById(String id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .<Event>build());
    }

    public ResponseEntity<?> updateEvent(String id, Event eventDetails) {

        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));


        String roleCheckUrl = userServiceUrl + "/api/users/" + eventDetails.getOrganizerId() + "/usertype";
        String roleResponse;
        try {
            roleResponse = restTemplate.getForObject(roleCheckUrl, String.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking user role: " + e.getMessage());
        }


        if (roleResponse != null && roleResponse.startsWith("User type: ")) {
            String userType = roleResponse.substring(11);
            if ("student".equalsIgnoreCase(userType) && eventDetails.getExpectedAttendees() > 20) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Students cannot update events with more than 20 attendees.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User does not have permission to update this event.");
        }
        existingEvent.setEventName(eventDetails.getEventName());
        existingEvent.setOrganizerId(eventDetails.getOrganizerId());
        existingEvent.setEventType(eventDetails.getEventType());
        existingEvent.setExpectedAttendees(eventDetails.getExpectedAttendees());

        Event updatedEvent = eventRepository.save(existingEvent);
        return ResponseEntity.ok(updatedEvent);
    }

    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    public ResponseEntity<?> deleteEvent(String id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Event not found with id: " + id);
        }
    }
}
