package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.ValidationHistoryResponse;
import com.aueventmanagement.entity.TicketValidationHistory;
import com.aueventmanagement.entity.User;
import com.aueventmanagement.repository.TicketValidationHistoryRepository;
import com.aueventmanagement.repository.UserRepository;
import com.aueventmanagement.service.TicketValidationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class TicketValidationHistoryServiceImpl implements TicketValidationHistoryService {

    private final TicketValidationHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Override
    public List<ValidationHistoryResponse> getMyValidationHistory() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User staff = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        List<TicketValidationHistory> history =
                historyRepository.findByStaffOrderByValidationDateTimeDesc(staff);

        return history.stream()
                .map(item -> ValidationHistoryResponse.builder()
                        .id(item.getId())
                        .eventName(item.getTicket().getEvent().getName())
                        .ticketType(item.getTicket().getTicketType().getName())
                        .quantity(item.getTicket().getQuantity())
                        .validationDateTime(item.getValidationDateTime())
                        .build())
                .toList();
    }
}
