package org.spring.multithread_backend.thread;

import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final String eventId;
    private final int quantity; // Total tickets to purchase
    private final int customerRetrievalRate; // Tickets retrieved per second

    public Customer(TicketPool ticketPool, String eventId, int quantity, int customerRetrievalRate) {
        if (ticketPool == null) {
            throw new IllegalArgumentException("TicketPool cannot be null");
        }
        this.ticketPool = ticketPool;
        this.eventId = eventId;
        this.quantity = quantity;
        this.customerRetrievalRate = customerRetrievalRate;
    }

    @Override
    public void run() {
        int ticketsRetrieved = 0;

        while (ticketsRetrieved < quantity) {
            try {
                int batchSize = Math.min(customerRetrievalRate, quantity - ticketsRetrieved);

                // Retrieve tickets in batches
                synchronized (ticketPool) {
                    if (!ticketPool.areTicketsAvailable(eventId, batchSize)) {
                        System.out.println("Not enough tickets available for event: " + eventId);
                        break;
                    }
                    List<Ticket> batch = ticketPool.removeTickets(eventId, batchSize);
                    batch.forEach(ticket -> System.out.println("Customer purchased ticket: " + ticket.getId()));
                }

                ticketsRetrieved += batchSize;

                // Wait for 1 second between retrievals
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (ticketsRetrieved < quantity) {
            System.out.println("Customer could not retrieve all requested tickets for event: " + eventId);
        }
    }
}