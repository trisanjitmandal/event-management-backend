package com.aueventmanagement.repository;

import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);

    List<User> findByRole(Role role);

    List<User> findByOrganizerAndRole(User organizer, Role role);

    List<User> findTop10ByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email
    );
}
