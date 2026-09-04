package com.aueventmanagement.controller;

import com.aueventmanagement.dto.CancelTicketResponse;
import com.aueventmanagement.dto.EventResponse;
import com.aueventmanagement.dto.PurchaseTicketRequest;
import com.aueventmanagement.dto.TicketResponse;
import com.aueventmanagement.service.EventService;
import com.aueventmanagement.service.TicketService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor

public class TicketController {

    private final TicketService ticketService;



    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable UUID ticketId){
        return ResponseEntity.ok(ticketService.getTicketById(ticketId));
    }



    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(){
        return ResponseEntity.ok(ticketService.getMyTickets());
    }

    @PatchMapping("/cancel/{ticketId}")
    public ResponseEntity<CancelTicketResponse> cancelTicket(
            @PathVariable UUID ticketId) throws RazorpayException {

        return ResponseEntity.ok(ticketService.cancelTicket(ticketId));
    }
}

//    @PostMapping("/purchase")
//    public ResponseEntity<TicketResponse> purchaseTicket(
//            @RequestBody PurchaseTicketRequest request){
//        return ResponseEntity.ok(ticketService.purchaseTicket(request));
//    }
