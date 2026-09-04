package com.aueventmanagement.dto;

import com.aueventmanagement.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder

public class StaffResponse {

    private UUID id;

    private String name;

    private  String email;

    private Role role;

}
