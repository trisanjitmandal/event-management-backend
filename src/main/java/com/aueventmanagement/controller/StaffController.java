package com.aueventmanagement.controller;

import com.aueventmanagement.dto.CreateStaffRequest;
import com.aueventmanagement.dto.StaffResponse;
import com.aueventmanagement.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor

public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest request){
        return ResponseEntity.ok(staffService.createStaff(request));
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff(){
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStaff(
            @PathVariable UUID id){
        staffService.deleteStaff(id);
        return ResponseEntity.ok("Staff deleted Successfully");
    }
}
