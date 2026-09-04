package com.aueventmanagement.dto;

import com.aueventmanagement.enums.PaymentStatus;
import com.aueventmanagement.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CancelTicketResponse {

    private String message;
    private TicketStatus ticketStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime cancelledAt;
}