package com.aueventmanagement.controller;

import com.aueventmanagement.dto.TicketValidationRequest;
import com.aueventmanagement.dto.TicketValidationResponse;
import com.aueventmanagement.service.TicketValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;

    @PostMapping("/scan")
    public ResponseEntity<TicketValidationResponse> validateTicket(
            @Valid @RequestBody TicketValidationRequest request){
        return ResponseEntity.ok(
                ticketValidationService.validateTicket(request));
    }
}
