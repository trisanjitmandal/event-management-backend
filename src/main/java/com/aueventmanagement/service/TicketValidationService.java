package com.aueventmanagement.service;

import com.aueventmanagement.dto.TicketValidationRequest;
import com.aueventmanagement.dto.TicketValidationResponse;


public interface TicketValidationService {

    TicketValidationResponse validateTicket(TicketValidationRequest request);
}
