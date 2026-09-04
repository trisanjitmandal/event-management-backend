package com.aueventmanagement.repository;

import com.aueventmanagement.entity.Ticket;
import com.aueventmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByAttendee(User attendee);

    Optional<Ticket> findByQrCodeData(String qrCodeData);

}
