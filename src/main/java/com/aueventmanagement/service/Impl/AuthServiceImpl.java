package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.AuthResponse;
import com.aueventmanagement.dto.LoginRequest;
import com.aueventmanagement.dto.RegisterRequest;
import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.Role;
import com.aueventmanagement.repository.UserRepository;
import com.aueventmanagement.service.AuthService;
import com.aueventmanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
     private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("User Already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ATTENDEE);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().name());

        return new AuthResponse(token, "User registered Successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(" Invalid email or Password"));

        String token = jwtUtil.generateToken(user.getEmail(),user.getRole().name());

        return new AuthResponse(token, "Login Successfully");
    }
}
