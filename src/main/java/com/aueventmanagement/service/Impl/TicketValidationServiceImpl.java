package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.TicketValidationRequest;
import com.aueventmanagement.dto.TicketValidationResponse;
import com.aueventmanagement.entity.*;
import com.aueventmanagement.enums.TicketStatus;
import com.aueventmanagement.repository.TicketRepository;
import com.aueventmanagement.repository.TicketValidationHistoryRepository;
import com.aueventmanagement.repository.TicketValidationRepository;
import com.aueventmanagement.repository.UserRepository;
import com.aueventmanagement.service.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class TicketValidationServiceImpl implements TicketValidationService {

    private final TicketRepository ticketRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final UserRepository userRepository;
    private final TicketValidationHistoryRepository historyRepository;

    @Override
    public TicketValidationResponse validateTicket(TicketValidationRequest request) {


        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found"));


        Ticket ticket = ticketRepository.findByQrCodeData(request.getQrCodeData())
                .orElseThrow(() -> new RuntimeException("Invalid Ticket"));

        // Check already USED or not
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new RuntimeException("Ticket Already USED");
        }

        // Mark as USED
        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);

        TicketValidationHistory history = new TicketValidationHistory();

        history.setStaff(staff);
        history.setTicket(ticket);
        history.setValidationDateTime(LocalDateTime.now());

        historyRepository.save(history);

        // Build Response
        TicketValidation validation = TicketValidation.builder()
                .ticket(ticket)
                .staff(staff)
                .validationDateTime(LocalDateTime.now())
                .build();

        ticketValidationRepository.save(validation);

        TicketValidationResponse response = new TicketValidationResponse();

        response.setStatus(ticket.getStatus());
        response.setAttendeeName(ticket.getAttendee().getName());
        response.setEventName(ticket.getEvent().getName());
        response.setTicketType(ticket.getTicketType().getName());
        response.setQuantity(ticket.getQuantity());
        response.setValidationDateTime(validation.getValidationDateTime());

        return response;
    }
    }
