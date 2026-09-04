package com.aueventmanagement.service;

import com.aueventmanagement.dto.ValidationHistoryResponse;

import java.util.List;

public interface TicketValidationHistoryService {

    List<ValidationHistoryResponse> getMyValidationHistory();
}
