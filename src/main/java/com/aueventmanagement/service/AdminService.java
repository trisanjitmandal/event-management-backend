package com.aueventmanagement.service;

import com.aueventmanagement.dto.ApiResponse;
import com.aueventmanagement.dto.UserSearchResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    List<UserSearchResponse> searchUsers(String keyword);

    ApiResponse promoteToOrganizer(UUID userId);

    ApiResponse demoteToAttendee(UUID userId);

}
