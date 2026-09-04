package com.aueventmanagement.dto;

import com.aueventmanagement.enums.TicketTypesCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data

public class CreatePaymentRequest {

    @NotNull
    private UUID eventId;

    @NotNull
    private TicketTypesCategory ticketType;

    @NotNull
    @Min(1)
    private Integer quantity;
}
