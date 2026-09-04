package com.aueventmanagement.repository;

import com.aueventmanagement.entity.Event;
import com.aueventmanagement.entity.TicketType;
import com.aueventmanagement.enums.TicketTypesCategory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {
    Optional <TicketType> findByEventAndName(Event event, TicketTypesCategory name);
    List<TicketType> findByEvent(Event event);
}
