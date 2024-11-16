package org.spring.multithread_backend.thread;

import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final String eventId;
    private final int quantity; // Total tickets to purchase
    private final int customerRetrievalRate; // Tickets retrieved per second
    private final int maxQty;

    public Customer(TicketPool ticketPool, String eventId, int quantity, int customerRetrievalRate,int maxQty) {
        if (ticketPool == null) {
            throw new IllegalArgumentException("TicketPool cannot be null");
        }
        this.ticketPool = ticketPool;
        this.eventId = eventId;
        this.quantity = quantity;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxQty = maxQty;
    }

    @Override
    public void run() {
        int ticketsRetrieved = 0;

        // Ensure that the quantity does not exceed the customer's maximum allowed quantity
        int effectiveQuantity = Math.min(quantity, maxQty);

        while (ticketsRetrieved < effectiveQuantity) {
            try {
                int batchSize = Math.min(customerRetrievalRate, effectiveQuantity - ticketsRetrieved);

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

        if (ticketsRetrieved < effectiveQuantity) {
            System.out.println("Customer could not retrieve all requested tickets for event: " + eventId);
        } else {
            System.out.println("Customer successfully retrieved all requested tickets for event: " + eventId);
        }
    }

}