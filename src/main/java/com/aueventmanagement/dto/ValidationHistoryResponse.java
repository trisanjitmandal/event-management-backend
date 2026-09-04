package com.aueventmanagement.dto;

import com.aueventmanagement.enums.TicketTypesCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder

public class ValidationHistoryResponse {

        private UUID id;

        private String eventName;

        private TicketTypesCategory ticketType;

        private Integer quantity;

        private LocalDateTime validationDateTime;

}
