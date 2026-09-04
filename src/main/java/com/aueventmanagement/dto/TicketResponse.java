package com.aueventmanagement.dto;

import com.aueventmanagement.enums.TicketStatus;
import com.aueventmanagement.enums.TicketTypesCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketResponse {
    private UUID id;
    private UUID attendeeId;
    private UUID eventId;
    private TicketTypesCategory ticketType;
    private Integer quantity;
    private BigDecimal totalPrice;
    private TicketStatus status;
    private LocalDateTime createdTime;
    private String qrCodeBase64;
}
