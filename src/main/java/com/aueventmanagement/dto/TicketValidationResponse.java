package com.aueventmanagement.dto;

import com.aueventmanagement.enums.TicketStatus;
import com.aueventmanagement.enums.TicketTypesCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketValidationResponse {


    private TicketStatus status;
    private String attendeeName;

    private String eventName;
    private TicketTypesCategory ticketType;
    private Integer quantity;
    private LocalDateTime validationDateTime;
}
