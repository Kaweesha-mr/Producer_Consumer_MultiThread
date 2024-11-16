package org.spring.multithread_backend.thread;

import org.spring.multithread_backend.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketPool {

    private final List<Ticket> tickets = Collections.synchronizedList(new LinkedList<>());

    public synchronized void addTickets(List<Ticket> newTickets){
        tickets.addAll(newTickets);
        notifyAll();
    }

    // Check if enough tickets are available
    public synchronized boolean areTicketsAvailable(String eventId, int quantity) {
        return getPoolSize(eventId) >= quantity;
    }
    // Get the number of available tickets for an event
    public synchronized int getPoolSize(String eventId) {
        return (int) tickets.stream().filter(ticket -> ticket.getEventId().equals(eventId) && !ticket.isSold()).count();
    }

    // Remove tickets from the pool
    public synchronized List<Ticket> removeTickets(String eventId, int quantity) throws InterruptedException {
        List<Ticket> purchasedTickets = new LinkedList<>();

        while (purchasedTickets.size() < quantity) {
            Ticket ticket = tickets.stream()
                    .filter(t -> t.getEventId().equals(eventId) && !t.isSold())
                    .findFirst()
                    .orElse(null);

            if (ticket != null) {
                ticket.setSold(true);
                tickets.remove(ticket);
                purchasedTickets.add(ticket);
            } else {
                wait(); // Wait until more tickets are available
            }
        }

        return purchasedTickets;
    }

    // Get all available tickets (not sold)
    public synchronized List<Ticket> getAllAvailableTickets() {
        return tickets.stream()
                .filter(ticket -> !ticket.isSold()) // Filter tickets that are not sold
                .collect(Collectors.toList());
    }

}
