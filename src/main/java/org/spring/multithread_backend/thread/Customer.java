package org.spring.multithread_backend.thread;

import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Customer implements Runnable{

    private final TicketPool ticketPool;
    private final String eventId;
    private final int qty;

    public Customer(TicketPool ticketPool, String eventId, int qty) {
        if (ticketPool == null) {
            throw new IllegalArgumentException("TicketPool cannot be null");
        }
        this.ticketPool = ticketPool;
        this.eventId = eventId;
        this.qty = qty;

    }

    @Override
    public void run() {
        try {
            synchronized (ticketPool) {
                if (ticketPool.areTicketsAvailable(eventId, qty)) {
                    List<Ticket> tickets = ticketPool.removeTickets(eventId, qty);
                    tickets.forEach(ticket -> System.out.println("Customer purchased ticket: " + ticket.getId()));
                } else {
                    System.out.println("Not enough tickets available for event: " + eventId);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
