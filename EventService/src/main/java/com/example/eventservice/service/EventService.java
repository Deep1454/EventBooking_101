package com.example.eventservice.service;

import com.example.eventservice.model.Event;
import com.example.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public Event createEvent(Event event) {

        String roleCheckUrl = userServiceUrl + "/api/users/" + event.getOrganizerId() + "/usertype";
        String roleResponse = restTemplate.getForObject(roleCheckUrl, String.class);


        if (roleResponse.startsWith("User type: ")) {
            String userType = roleResponse.substring(11);


            if ("student".equalsIgnoreCase(userType)) {
                if (event.getExpectedAttendees() > 20) {
                    throw new RuntimeException("Students cannot create events with more than 20 attendees.");
                }
            }

            return eventRepository.save(event);
        } else {
            throw new RuntimeException("User does not have permission to create this event.");
        }
    }


    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(String id, Event eventDetails) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));


        String roleCheckUrl = userServiceUrl + "/api/users/" + eventDetails.getOrganizerId() + "/usertype";
        String roleResponse = restTemplate.getForObject(roleCheckUrl, String.class);

        if (roleResponse.startsWith("User type: ")) {
            String userType = roleResponse.substring(11);

            if ("student".equalsIgnoreCase(userType)) {
                if (eventDetails.getExpectedAttendees() > 20) {
                    throw new RuntimeException("Students cannot update events with more than 20 attendees.");
                }
            }
        } else {
            throw new RuntimeException("User does not have permission to update this event.");
        }


        existingEvent.setEventName(eventDetails.getEventName());
        existingEvent.setOrganizerId(eventDetails.getOrganizerId());
        existingEvent.setEventType(eventDetails.getEventType());
        existingEvent.setExpectedAttendees(eventDetails.getExpectedAttendees());

        return eventRepository.save(existingEvent);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }
}
