package com.aueventmanagement.service;


public interface RazorpayWebhookService {

    void handleWebhook(String payload, String signature);

}
