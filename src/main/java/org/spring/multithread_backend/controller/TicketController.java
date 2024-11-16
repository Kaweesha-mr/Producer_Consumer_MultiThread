package org.spring.multithread_backend.controller;

import org.spring.multithread_backend.model.Ticket;
import org.spring.multithread_backend.thread.Customer;
import org.spring.multithread_backend.thread.TicketPool;
import org.spring.multithread_backend.thread.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketPool ticketPool;

    @PostMapping("/vendor/add")
    public ResponseEntity<?> addTickets(@RequestParam String eventId, @RequestParam int totalTickets) {
        List<Ticket> tickets = new LinkedList<>();
        for (int i = 0; i < totalTickets; i++) {
            Ticket ticket = new Ticket();
            ticket.setId(UUID.randomUUID().toString());
            ticket.setEventId(eventId);
            ticket.setSold(false);
            tickets.add(ticket);
        }

        new Thread(new Vendor(ticketPool, tickets)).start();
        return ResponseEntity.ok("Vendor is adding " + totalTickets + " tickets for event: " + eventId);
    }

    @PostMapping("/customer/purchase")
    public ResponseEntity<?> purchaseTickets(@RequestParam String eventId, @RequestParam int quantity) {
        new Thread(new Customer(ticketPool, eventId, quantity)).start();
        return ResponseEntity.ok("Customer is attempting to purchase " + quantity + " tickets for event: " + eventId);
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<?> getTicketStatus(@PathVariable String eventId) {
        int availableTickets = ticketPool.getPoolSize(eventId);
        return ResponseEntity.ok("Available tickets for event " + eventId + ": " + availableTickets);
    }

}
