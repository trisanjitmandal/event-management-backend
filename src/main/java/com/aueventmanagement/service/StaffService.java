package com.aueventmanagement.service;

import com.aueventmanagement.dto.CreateStaffRequest;
import com.aueventmanagement.dto.StaffResponse;

import java.util.List;
import java.util.UUID;

public interface StaffService {

    StaffResponse createStaff(CreateStaffRequest createStaffRequest);

    List<StaffResponse> getAllStaff();

    void deleteStaff(UUID id);
}
