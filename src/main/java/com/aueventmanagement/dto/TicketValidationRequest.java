package com.aueventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class TicketValidationRequest {

    @NotBlank
    private String qrCodeData;
}
