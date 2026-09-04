package com.aueventmanagement.dto;

import com.aueventmanagement.enums.TicketTypesCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TicketTypeRequest {

    private UUID id;      // null when creating, present when updating
    private TicketTypesCategory typeName;


    private BigDecimal price;

    private Integer availableQuantity;;
}
