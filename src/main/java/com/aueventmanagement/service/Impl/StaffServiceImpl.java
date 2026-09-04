package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.CreateStaffRequest;
import com.aueventmanagement.dto.StaffResponse;
import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.Role;
import com.aueventmanagement.repository.UserRepository;
import com.aueventmanagement.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        if(organizer.getRole() != Role.ORGANIZER){
            throw new RuntimeException("Only organizer can create staff");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User staff = new User();

        staff.setName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setRole(Role.STAFF);
        staff.setOrganizer(organizer);

        User savedStaff = userRepository.save(staff);

        return StaffResponse.builder()
                .id(savedStaff.getId())
                .name(savedStaff.getName())
                .email(savedStaff.getEmail())
                .role(savedStaff.getRole())
                .build();
    }

    @Override
    public List<StaffResponse> getAllStaff() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));


        return userRepository.findByOrganizerAndRole(organizer,Role.STAFF)
                .stream()
                .map(user -> StaffResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .toList();
    }

  @Override

  public void deleteStaff(UUID id) {

      String email = SecurityContextHolder.getContext()
              .getAuthentication()
              .getName();

      User organizer = userRepository.findByEmail(email)
              .orElseThrow(() -> new RuntimeException("Organizer not found"));

      User staff = userRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Staff not found"));

      if (staff.getOrganizer() == null ||
              !staff.getOrganizer().getId().equals(organizer.getId())) {

          throw new RuntimeException("You cannot delete this staff");
      }

      userRepository.delete(staff);
  }
    }

