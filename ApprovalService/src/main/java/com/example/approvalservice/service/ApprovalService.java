package com.example.approvalservice.service;

import com.example.approvalservice.model.Approval;
import com.example.approvalservice.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${event-service.url}")
    private String eventServiceUrl;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public ResponseEntity<String> createApproval(Approval approval) {

        String userRole = getUserRole(approval.getUserId());


        if (!approval.isApproved()) {

            if (!"staff".equalsIgnoreCase(userRole)) {
                return ResponseEntity.ok("Only staff can reject events.");
            }
            return ResponseEntity.ok("Event is rejected.");
        }

        validateUserRole(userRole);


        validateEventExists(approval.getEventId());


        if (approvalExists(approval.getEventId(), approval.getUserId())) {
            return ResponseEntity.ok("User has already approved this event.");
        }

        Approval savedApproval = approvalRepository.save(approval);
        return ResponseEntity.status(HttpStatus.CREATED).body("Approval created successfully for event ID: " + approval.getEventId());
    }

    public Optional<Approval> getApprovalById(String id) {
        return approvalRepository.findById(id);
    }

    public List<Approval> getAllApprovals() {
        return approvalRepository.findAll();
    }

    public void deleteApproval(String id) {
        if (!approvalRepository.existsById(id)) {
            throw new IllegalArgumentException("Approval with ID " + id + " does not exist.");
        }
        approvalRepository.deleteById(id);
    }

    private String getUserRole(String userId) {
        String roleCheckUrl = userServiceUrl + "/api/users/" + userId + "/role";
        ResponseEntity<String> response = restTemplate.getForEntity(roleCheckUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return parseUserRole(response.getBody());
        } else {
            throw new IllegalArgumentException("Failed to fetch user role for user ID: " + userId);
        }
    }

    private String parseUserRole(String roleResponse) {
        if (roleResponse.startsWith("User role: ")) {
            return roleResponse.substring("User role: ".length()).trim();
        }
        return roleResponse;
    }

    private void validateUserRole(String role) {
        if (!"admin".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("User with role " + role + " does not have permission to approve this event.");
        }
    }

    private void validateEventExists(String eventId) {
        String eventCheckUrl = eventServiceUrl + "/api/events/" + eventId;
        try {
            ResponseEntity<String> eventResponse = restTemplate.getForEntity(eventCheckUrl, String.class);
            if (eventResponse.getStatusCode() != HttpStatus.OK) {
                throw new IllegalArgumentException("Event with ID " + eventId + " does not exist or is not available for approval.");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("Event with ID " + eventId + " does not exist.");
            }
            throw new RuntimeException("An error occurred while checking event availability: " + e.getMessage());
        }
    }

    private boolean approvalExists(String eventId, String userId) {
        List<Approval> existingApprovals = approvalRepository.findByEventIdAndUserId(eventId, userId);
        return !existingApprovals.isEmpty();
    }
}
