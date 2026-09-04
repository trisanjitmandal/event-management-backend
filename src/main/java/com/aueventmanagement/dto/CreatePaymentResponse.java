package com.aueventmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreatePaymentResponse {

    private String orderId;

    private Integer amount;

    private String currency;

    private String key;
}
