package com.aueventmanagement.service.Impl;

import com.aueventmanagement.dto.CancelTicketResponse;
import com.aueventmanagement.dto.TicketResponse;
import com.aueventmanagement.entity.*;
import com.aueventmanagement.enums.TicketStatus;
import com.aueventmanagement.repository.*;
import com.aueventmanagement.service.PaymentService;
import com.aueventmanagement.service.QRCodeService;
import com.aueventmanagement.service.TicketService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final UserRepository userRepository;
    private final QRCodeService qrCodeService;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;



    @Override
    public TicketResponse getTicketById(UUID ticketId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User attendee = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Attendee not found")
                );

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check ticket ownership
        if (!ticket.getAttendee().getId().equals(attendee.getId())) {
            throw new RuntimeException(
                    "You are not authorized to view this ticket.. invalid"
            );
        }

        TicketResponse response = mapToResponse(ticket);

        byte[] qrImage = qrCodeService.generateQRCode(ticket.getQrCodeData());

        String base64QR = Base64.getEncoder().encodeToString(qrImage);

        response.setQrCodeBase64(base64QR);

        return response;
    }

    @Override
    public List<TicketResponse> getMyTickets() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User attendee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Attendee not found"));
        List<Ticket> tickets = ticketRepository.findByAttendee(attendee);

        return tickets.stream().map(ticket -> {

            TicketResponse response = mapToResponse(ticket);

            byte[] qrImage = qrCodeService.generateQRCode(ticket.getQrCodeData());

            String base64QR = Base64.getEncoder().encodeToString(qrImage);

            response.setQrCodeBase64(base64QR);

            return response;

        }).toList();
    }

    @Override
    public CancelTicketResponse cancelTicket(UUID ticketId) throws RazorpayException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User attendee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!ticket.getAttendee().getId().equals(attendee.getId())) {
            throw new RuntimeException("You are not authorized to cancel this ticket");
        }

        if (ticket.getStatus() == TicketStatus.CANCEL) {
            throw new RuntimeException("Ticket is already cancelled");
        }

        // Find Payment
        Payment payment = paymentRepository.findByTicket(ticket)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Refund Payment
        paymentService.refundPayment(payment);

        ticket.setStatus(TicketStatus.CANCEL);

        TicketType ticketType = ticket.getTicketType();
        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity() + ticket.getQuantity());

        ticketTypeRepository.save(ticketType);
        ticketRepository.save(ticket);

        CancelTicketResponse response = new CancelTicketResponse();

        response.setMessage("Ticket cancelled successfully. Refund completed.");
        response.setTicketStatus(ticket.getStatus());
        response.setPaymentStatus(payment.getStatus());
        response.setCancelledAt(LocalDateTime.now());

        return response;
    }

    private TicketResponse mapToResponse(Ticket ticket) {

        TicketResponse response = new TicketResponse();

        response.setId(ticket.getId());
        response.setAttendeeId(ticket.getAttendee().getId());
        response.setEventId(ticket.getEvent().getId());
        response.setTicketType(ticket.getTicketType().getName());
        response.setQuantity(ticket.getQuantity());
        response.setTotalPrice(ticket.getTotalPrice());
        response.setStatus(ticket.getStatus());
        response.setCreatedTime(ticket.getCreatedTime());

        return response;
    }
}
