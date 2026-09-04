package com.aueventmanagement.dto;

import com.aueventmanagement.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private UUID id;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private BigDecimal amount;

    private PaymentStatus status;

    private UUID attendeeId;

    private UUID eventId;

    private UUID ticketId;

    private LocalDateTime createdAt;
}
