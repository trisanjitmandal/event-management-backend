package com.aueventmanagement.service;

import com.aueventmanagement.dto.AuthResponse;
import com.aueventmanagement.dto.LoginRequest;
import com.aueventmanagement.dto.RegisterRequest;


public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
