package com.aueventmanagement.entity;

import com.aueventmanagement.enums.TicketStatus;
import com.aueventmanagement.enums.TicketTypesCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketValidationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User staff;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    private TicketTypesCategory ticketType;

    private Integer quantity;

    private LocalDateTime validationDateTime;

}

