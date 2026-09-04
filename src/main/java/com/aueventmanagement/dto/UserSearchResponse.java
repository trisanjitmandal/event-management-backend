package com.aueventmanagement.dto;

import com.aueventmanagement.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserSearchResponse {

    private UUID id;

    private String name;

    private String email;

    private Role role;

}
