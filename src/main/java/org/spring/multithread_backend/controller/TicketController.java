package org.spring.multithread_backend.controller;

import org.spring.multithread_backend.configuration.Configuration;
import org.spring.multithread_backend.configuration.ConfigurationManager;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketPool ticketPool;

    private final Configuration configuration = ConfigurationManager.loadConfiguration();

    @PostMapping("/vendor/add")
    public ResponseEntity<?> addTickets(
            @RequestParam String eventId,
            @RequestParam Float price,
            @RequestParam Integer qty
    ) {
        int totalTickets = configuration.getTotalTickets(); // Maximum allowed tickets
        int ticketReleaseRate = configuration.getTicketReleaseRate();

        if (ticketReleaseRate <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket release rate must be greater than 0.");
        }

        // Adjust quantity if it exceeds the maximum allowed tickets
        if (qty > totalTickets) {
            qty = totalTickets;
        }

        List<Ticket> tickets = new LinkedList<>();
        for (int i = 0; i < qty; i++) {
            Ticket ticket = new Ticket();
            ticket.setId(UUID.randomUUID().toString());
            ticket.setEventId(eventId);
            ticket.setPrice(price);
            ticket.setSold(false);
            tickets.add(ticket);
        }

        new Thread(new Vendor(ticketPool, tickets, ticketReleaseRate)).start();
        return ResponseEntity.ok("Vendor is adding " + qty + " tickets for event: " + eventId + " at a release rate of " + ticketReleaseRate + " tickets/second.");
    }

    @PostMapping("/customer/purchase")
    public ResponseEntity<?> purchaseTickets(
            @RequestParam String eventId,
            @RequestParam int quantity) {

        int customerRetrievalRate = configuration.getCustomerRetrievalRate();
        int maxTicketCapacity = configuration.getMaxTicketCapacity();

        if (customerRetrievalRate <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer retrieval rate must be greater than 0.");
        }

        new Thread(new Customer(ticketPool, eventId, quantity, customerRetrievalRate,maxTicketCapacity)).start();
        return ResponseEntity.ok("Customer is attempting to purchase " + quantity + " tickets for event: " + eventId + " at a retrieval rate of " + customerRetrievalRate + " tickets/second.");
    }


    @GetMapping("/status/{eventId}")
    public ResponseEntity<?> getTicketStatus(@PathVariable String eventId) {
        int availableTickets = ticketPool.getPoolSize(eventId);
        return ResponseEntity.ok("Available tickets for event " + eventId + ": " + availableTickets);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTickets() {
        Map<String, Map<String, Object>> availableTicketsByEvent = ticketPool.getAvailableTicketsByEvent();

        // Return the structured data
        return ResponseEntity.ok(availableTicketsByEvent);
    }
}
