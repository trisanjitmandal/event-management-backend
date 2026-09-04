package com.aueventmanagement.repository;

import com.aueventmanagement.entity.Payment;
import com.aueventmanagement.entity.Ticket;
import com.aueventmanagement.entity.User;
import com.aueventmanagement.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByAttendee(User attendee);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    Optional <Payment> findByTicket(Ticket ticket);
}
