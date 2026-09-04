package com.aueventmanagement.controller;

import com.aueventmanagement.service.RazorpayWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor

public class RazorpayWebhookController {

    private final RazorpayWebhookService razorpayWebhookService;

    @PostMapping("/razorpay")
    private ResponseEntity<Void> handleWebhooks(
            @RequestBody String payload,
            @RequestHeader ("X-Razorpay-Signature") String signature
    ){
      razorpayWebhookService.handleWebhook(payload,signature);

      return ResponseEntity.ok().build();
    }
}
