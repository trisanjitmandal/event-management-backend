package com.aueventmanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {

    private String name;

    private String description;

    private String venue;


    private LocalDateTime startDate;


    private LocalDateTime endDate;



    private LocalDateTime salesStartDate;

    private LocalDateTime salesEndDate;

    private List<TicketTypeRequest> ticketTypes;
}
