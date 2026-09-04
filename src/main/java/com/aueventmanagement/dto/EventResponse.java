package com.aueventmanagement.dto;

import com.aueventmanagement.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class EventResponse {

    private UUID id;
    private String name;
    private String description;
    private String venue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;


    private LocalDateTime salesStartDate;
    private LocalDateTime salesEndDate;

    private EventStatus status;
    private String organizerName;

    private UUID organizerId;

    private List<TicketTypeResponse> ticketTypes;
}
