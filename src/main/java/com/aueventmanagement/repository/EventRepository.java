package com.aueventmanagement.repository;

import com.aueventmanagement.entity.Event;
import com.aueventmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizer(User organizer);
}
