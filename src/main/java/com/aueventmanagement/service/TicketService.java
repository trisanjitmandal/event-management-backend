package com.aueventmanagement.service;

import com.aueventmanagement.dto.CancelTicketResponse;
import com.aueventmanagement.dto.PurchaseTicketRequest;
import com.aueventmanagement.dto.TicketResponse;
import com.razorpay.RazorpayException;

import java.util.List;
import java.util.UUID;

public interface TicketService  {

    TicketResponse getTicketById(UUID ticketId);
    List<TicketResponse> getMyTickets();

    CancelTicketResponse cancelTicket(UUID ticketId) throws RazorpayException;
}
