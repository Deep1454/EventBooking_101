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
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only staff can reject events.");
            }
            return ResponseEntity.ok("Event is rejected.");
        }

        if (!"staff".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User with role " + userRole + " does not have permission to approve this event.");
        }

        if (approvalExists(approval.getEventId(), approval.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This user has already approved this event.");
        }

        ResponseEntity<String> eventValidationResponse = validateEventExists(approval.getEventId());
        if (eventValidationResponse.getStatusCode() != HttpStatus.OK) {
            return eventValidationResponse;
        }

        Approval savedApproval = approvalRepository.save(approval);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Approval created successfully for event ID: " + approval.getEventId());
    }

    public ResponseEntity<Approval> getApprovalById(String id) {
        return approvalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    public ResponseEntity<List<Approval>> getAllApprovals() {
        List<Approval> approvals = approvalRepository.findAll();
        return ResponseEntity.ok(approvals);
    }

    public ResponseEntity<String> deleteApproval(String id) {
        if (!approvalRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Approval with ID " + id + " does not exist.");
        }
        approvalRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Approval deleted successfully.");
    }

    private String getUserRole(String userId) {
        String roleCheckUrl = userServiceUrl + "/api/users/" + userId + "/role";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(roleCheckUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return parseUserRole(response.getBody());
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    private String parseUserRole(String roleResponse) {
        if (roleResponse.startsWith("User role: ")) {
            return roleResponse.substring("User role: ".length()).trim();
        }
        return roleResponse;
    }

    private ResponseEntity<String> validateEventExists(String eventId) {
        String eventCheckUrl = eventServiceUrl + "/api/events/" + eventId;
        try {
            ResponseEntity<String> eventResponse = restTemplate.getForEntity(eventCheckUrl, String.class);
            if (eventResponse.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Event exists.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Event with ID " + eventId + " does not exist or is not available for approval.");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Event with ID " + eventId + " does not exist.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while checking event availability: " + e.getMessage());
        }
    }

    private boolean approvalExists(String eventId, String userId) {
        List<Approval> existingApprovals = approvalRepository.findByEventIdAndUserId(eventId, userId);
        return !existingApprovals.isEmpty();
    }
}
