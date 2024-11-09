package com.example.eventservice.controller;

import com.example.eventservice.model.Event;
import com.example.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        ResponseEntity<?> response = eventService.createEvent(event);


        if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof Event) {
            Event savedEvent = (Event) response.getBody();
            URI location = URI.create("/api/events/" + savedEvent.getId());
            return ResponseEntity.created(location).body(savedEvent);
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        return eventService.getEventById(id);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody Event eventDetails) {
        return eventService.updateEvent(id, eventDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        return eventService.deleteEvent(id);
    }
}
