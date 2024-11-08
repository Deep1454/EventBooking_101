package com.example.approvalservice.controller;

import com.example.approvalservice.model.Approval;
import com.example.approvalservice.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PostMapping
    public ResponseEntity<String> createApproval(@RequestBody Approval approval) {
        return approvalService.createApproval(approval);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApprovalById(@PathVariable String id) {
        return approvalService.getApprovalById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Approval> getAllApprovals() {
        return approvalService.getAllApprovals();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApproval(@PathVariable String id) {
        approvalService.deleteApproval(id);
        return ResponseEntity.noContent().build();
    }
}
