package com.aueventmanagement.service.Impl;

import com.aueventmanagement.service.PaymentService;
import com.aueventmanagement.service.RazorpayWebhookService;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor

public class RazorpayWebhookServiceImpl
        implements RazorpayWebhookService {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;


    @Override
    public void handleWebhook(String payload, String signature) {

        try {

            // Verify Razorpay webhook signature
            boolean isValid = Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    webhookSecret
            );

            if (!isValid) {

                log.warn("Invalid Razorpay webhook signature");

                throw new RuntimeException(
                        "Invalid webhook signature"
                );
            }


            // Convert payload into JSON
            JSONObject webhook =
                    new JSONObject(payload);

            String event =
                    webhook.getString("event");


            log.info(
                    "Razorpay webhook received: {}",
                    event
            );


            // Handle Razorpay events
            switch (event) {

                case "payment.captured":
                    paymentService.handlePaymentCaptured(webhook);
                    break;

                case "payment.failed":
                    paymentService.handlePaymentFailed(webhook);
                    break;

                case "order.paid":
                    paymentService.handleOrderPaid(webhook);
                    break;

                default:
                    log.info(
                            "Unhandled Razorpay event: {}",
                            event
                    );
            }

        } catch (Exception e) {

            log.error(
                    "Error processing Razorpay webhook",
                    e
            );

            throw new RuntimeException(
                    "Webhook processing failed",
                    e
            );
        }
    }
}