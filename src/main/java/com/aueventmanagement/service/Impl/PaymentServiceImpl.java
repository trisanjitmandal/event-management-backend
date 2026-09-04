package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.*;
import com.aueventmanagement.entity.*;
import com.aueventmanagement.entity.Payment;
import com.aueventmanagement.enums.EventStatus;
import com.aueventmanagement.enums.PaymentStatus;
import com.aueventmanagement.enums.TicketStatus;
import com.aueventmanagement.repository.*;
import com.aueventmanagement.service.PaymentService;
import com.aueventmanagement.service.QRCodeService;
import com.razorpay.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final RazorpayClient razorpayClient;

    private final QRCodeService qrCodeService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public CreatePaymentResponse createOrder(CreatePaymentRequest request) throws RazorpayException {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User attendee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Attendee not found"));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new RuntimeException("Event is not published");
        }

        LocalDateTime now = LocalDateTime.now();


        if (now.isAfter(event.getSalesEndDate())) {
            throw new RuntimeException("Ticket sales have ended");
        }

        TicketType ticketType = ticketTypeRepository
                .findByEventAndName(event, request.getTicketType())
                .orElseThrow(() -> new RuntimeException("Ticket type not found"));

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (ticketType.getAvailableQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough tickets available");
        }

        BigDecimal amount = ticketType.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        long amountInPaise = amount
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put(
                "receipt",
                "ticket_" + UUID.randomUUID()
        );

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        String razorpayOrderId = razorpayOrder.get("id");

        // Create payment
        Payment payment = new Payment();
        payment.setAttendee(attendee);
        payment.setEvent(event);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTicketType(ticketType);
        payment.setQuantity(request.getQuantity());
        payment.setRazorpayOrderId(razorpayOrderId);

        Payment savedPayment = paymentRepository.save(payment);


        CreatePaymentResponse response = new CreatePaymentResponse();

        response.setOrderId(savedPayment.getRazorpayOrderId());
        response.setAmount(
                amount.multiply(BigDecimal.valueOf(100)).intValue());
        response.setCurrency("INR");
        response.setKey(razorpayKeyId);

        return response;

    }

    @Override
    public TicketResponse verifyPayment(VerifyPaymentRequest request) {

        // Find payment using Razorpay Order ID
        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Already verified
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already verified");
        }

        // verify signature
        try{
            String payload = request.getRazorpayOrderId()
                    + "|" + request.getRazorpayPaymentId();

            boolean isValid = Utils.verifySignature(
                    payload,
                    request.getRazorpaySignature(),
                    razorpayKeySecret
            );

            if(!isValid){
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                throw new RuntimeException("Invalid payment signature");
            }

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment verification failed");
        }

        // Update payment details
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);

        TicketType ticketType = payment.getTicketType();

        if (ticketType.getAvailableQuantity() < payment.getQuantity()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Tickets are no longer available");
        }

        // Create Ticket
        return createTicket(payment);
    }

    @Override
    public List<PaymentResponse> getMyPayments() {


        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User attendee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Attendee not found"));

        List<Payment> payments = paymentRepository.findByAttendee(attendee);

        return payments.stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentById(UUID paymentId) {


        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User attendee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Attendee not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Security check
        if (!payment.getAttendee().getId().equals(attendee.getId())) {
            throw new RuntimeException("You are not authorized to view this payment");
        }

        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public void refundPayment(Payment payment) throws RazorpayException{

        if(payment.getStatus() !=  PaymentStatus.SUCCESS){
            throw new RuntimeException("Only successful payment can be refunded");
        }

        if(payment.getRazorpayPaymentId() == null){
            throw new RuntimeException("Razorpay paymentId not found");
        }

        long refundAmountInPaise = payment.getAmount()
                        .multiply(BigDecimal.valueOf(100)).longValue();

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount",refundAmountInPaise);
        refundRequest.put("speed","normal");

        Refund refund = razorpayClient.payments.refund(
                payment.getRazorpayPaymentId(),refundRequest
        );
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }

    private TicketResponse createTicket(Payment payment) {

        TicketType ticketType = payment.getTicketType();

        if (ticketType.getAvailableQuantity() < payment.getQuantity()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Tickets are no longer available");
        }

        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() - payment.getQuantity()
        );

        ticketTypeRepository.save(ticketType);

        Ticket ticket = new Ticket();

        ticket.setAttendee(payment.getAttendee());
        ticket.setEvent(payment.getEvent());
        ticket.setTicketType(ticketType);
        ticket.setQuantity(payment.getQuantity());
        ticket.setTotalPrice(payment.getAmount());
        ticket.setStatus(TicketStatus.ACTIVE);

        String qrData = "TICKET-" + UUID.randomUUID();
        ticket.setQrCodeData(qrData);
        ticket.setCreatedTime(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        byte[] qrImage = qrCodeService.generateQRCode(qrData);
        String base64QR = Base64.getEncoder().encodeToString(qrImage);

        // Link ticket with payment
        payment.setTicket(savedTicket);
        paymentRepository.save(payment);

        return mapToResponse(savedTicket, base64QR);
    }

    private TicketResponse mapToResponse(Ticket ticket, String qrCodeBase64) {

        TicketResponse response = new TicketResponse();

        response.setId(ticket.getId());
        response.setAttendeeId(ticket.getAttendee().getId());
        response.setEventId(ticket.getEvent().getId());
        response.setTicketType(ticket.getTicketType().getName());
        response.setQuantity(ticket.getQuantity());
        response.setTotalPrice(ticket.getTotalPrice());
        response.setStatus(ticket.getStatus());
        response.setCreatedTime(ticket.getCreatedTime());
        response.setQrCodeBase64(qrCodeBase64);

        return response;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setRazorpayOrderId(payment.getRazorpayOrderId());
        response.setRazorpayPaymentId(payment.getRazorpayPaymentId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setAttendeeId(payment.getAttendee().getId());
        response.setEventId(payment.getEvent().getId());

        if (payment.getTicket() != null) {
            response.setTicketId(payment.getTicket().getId());
        }

        response.setCreatedAt(payment.getCreatedAt());

        return response;
    }

    @Override
    public void handlePaymentCaptured(JSONObject webhook) {

        JSONObject paymentEntity = webhook
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayPaymentId =
                paymentEntity.getString("id");

        String razorpayOrderId =
                paymentEntity.getString("order_id");

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"
                        )
                );

        if (payment == null) {
            log.warn(
                    "Payment not found for Razorpay order: {}",
                    razorpayOrderId
            );
            return;
        }

        // Already processed
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info(
                    "Payment already marked SUCCESS: {}",
                    razorpayOrderId
            );
            return;
        }

        payment.setRazorpayPaymentId(
                razorpayPaymentId
        );

        payment.setStatus(
                PaymentStatus.SUCCESS
        );
        paymentRepository.save(payment);

        log.info(
                "Payment marked SUCCESS from webhook: {}",
                razorpayOrderId
        );
    }

    @Override
    public void handlePaymentFailed(JSONObject webhook) {

        JSONObject paymentEntity = webhook
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayPaymentId =
                paymentEntity.getString("id");

        String razorpayOrderId =
                paymentEntity.optString("order_id", null);

        if (razorpayOrderId == null) {
            log.warn(
                    "Payment failed webhook without order ID. Payment ID: {}",
                    razorpayPaymentId
            );
            return;
        }

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found")
                );

        // Already processed
        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.info(
                    "Payment already marked FAILED: {}",
                    razorpayOrderId
            );
            return;
        }

        payment.setRazorpayPaymentId(
                razorpayPaymentId
        );

        payment.setStatus(
                PaymentStatus.FAILED
        );

        paymentRepository.save(payment);

        log.info(
                "Payment marked FAILED from webhook: {}",
                razorpayOrderId
        );
    }


    @Override
    public void handleOrderPaid(JSONObject webhook) {

        JSONObject paymentEntity = webhook
                .getJSONObject("payload")
                .getJSONObject("order")
                .getJSONObject("entity");

        String razorpayOrderId =
                paymentEntity.getString("id");

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElse(null);

        if (payment == null) {
            log.warn(
                    "Payment not found for Razorpay order: {}",
                    razorpayOrderId
            );
            return;
        }

        log.info(
                "Order paid webhook received for payment: {}",
                payment.getId()
        );
    }
}
