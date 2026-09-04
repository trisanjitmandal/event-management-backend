package com.aueventmanagement.dto;

import com.aueventmanagement.enums.TicketTypesCategory;
import lombok.Data;

import java.util.UUID;

@Data
public class PurchaseTicketRequest {
    private UUID eventId;
    private TicketTypesCategory ticketType;
    private Integer quantity;
}
