package com.aueventmanagement.controller;

import com.aueventmanagement.dto.ApiResponse;
import com.aueventmanagement.dto.UserSearchResponse;
import com.aueventmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(
            @RequestParam String keyword ) {

        return ResponseEntity.ok(
                adminService.searchUsers(keyword)
        );
    }

    @PatchMapping("/users/{userId}/promote-organizer")
    public ApiResponse promoteToOrganizer(
            @PathVariable UUID userId) {

        return adminService.promoteToOrganizer(userId);
    }

    @PatchMapping("/users/{userId}/demote-attendee")
    public ApiResponse demoteToAttendee(
            @PathVariable UUID userId) {

        return adminService.demoteToAttendee(userId);
    }
}
