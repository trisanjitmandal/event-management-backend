package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.ApiResponse;
import com.aueventmanagement.dto.UserSearchResponse;
import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.Role;
import com.aueventmanagement.repository.UserRepository;
import com.aueventmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public List<UserSearchResponse> searchUsers(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return userRepository
                .findTop10ByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword.trim(),
                        keyword.trim()
                )
                .stream()
                .filter(user ->
                        user.getRole() == Role.ATTENDEE ||
                                user.getRole() == Role.ORGANIZER
                )
                .map(user -> UserSearchResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .toList();
    }

    @Override
    public ApiResponse promoteToOrganizer(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.STAFF) {
            throw new RuntimeException("Staff cannot become Organizer");
        }

        if (user.getRole() == Role.ORGANIZER) {
            throw new RuntimeException("User is already an Organizer");
        }

        user.setRole(Role.ORGANIZER);

        userRepository.save(user);
        return new ApiResponse("User promoted to Organizer successfully");


    }

    @Override
    public ApiResponse demoteToAttendee(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.STAFF) {
            throw new RuntimeException("Staff cannot be demoted");
        }

        if (user.getRole() == Role.ATTENDEE) {
            throw new RuntimeException("User is already an Attendee");
        }

        user.setRole(Role.ATTENDEE);

        userRepository.save(user);

        return new ApiResponse("Organizer demoted to Attendee successfully");
    }


}
