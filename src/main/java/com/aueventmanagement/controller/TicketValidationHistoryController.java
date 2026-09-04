package com.aueventmanagement.controller;

import com.aueventmanagement.dto.ValidationHistoryResponse;
import com.aueventmanagement.service.TicketValidationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/validation")
@RestController
@RequiredArgsConstructor

public class TicketValidationHistoryController {

    private final TicketValidationHistoryService historyService;

    @GetMapping("/history")
    public ResponseEntity<List<ValidationHistoryResponse>>
                           getValidationHistory(){
        return ResponseEntity.ok(historyService.getMyValidationHistory());
    }
}
