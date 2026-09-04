package com.aueventmanagement.service;

import com.aueventmanagement.dto.*;
import com.aueventmanagement.entity.Payment;
import com.razorpay.RazorpayException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    CreatePaymentResponse createOrder(CreatePaymentRequest request) throws RazorpayException;
    TicketResponse verifyPayment(VerifyPaymentRequest request);
    List<PaymentResponse> getMyPayments();

    PaymentResponse getPaymentById(UUID paymentId);
    void refundPayment(Payment payment) throws RazorpayException;


    void handlePaymentCaptured(JSONObject webhook);
    void handlePaymentFailed(JSONObject webhook);
    void handleOrderPaid(JSONObject webhook);


}
