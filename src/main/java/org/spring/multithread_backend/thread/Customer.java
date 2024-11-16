package org.spring.multithread_backend.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Customer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);

    private final TicketPool ticketPool;
    private final String eventId;
    private final int quantity; // Total tickets to purchase
    private final int customerRetrievalRate; // Tickets retrieved per second
    private final int maxQty;

    public Customer(TicketPool ticketPool, String eventId, int quantity, int customerRetrievalRate, int maxQty) {
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
        logger.info("Customer thread started for event: {}, requesting {} tickets (max allowed: {}).", eventId, effectiveQuantity, maxQty);

        while (ticketsRetrieved < effectiveQuantity) {
            try {
                int batchSize = Math.min(customerRetrievalRate, effectiveQuantity - ticketsRetrieved);

                // Retrieve tickets in batches
                synchronized (ticketPool) {
                    if (!ticketPool.areTicketsAvailable(eventId, batchSize)) {
                        logger.warn("Not enough tickets available for event: {}", eventId);
                        break;
                    }
                    List<Ticket> batch = ticketPool.removeTickets(eventId, batchSize);
                    batch.forEach(ticket -> logger.info("Customer purchased ticket: {}", ticket.getId()));
                }

                ticketsRetrieved += batchSize;

                // Wait for 1 second between retrievals
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Customer thread was interrupted.", e);
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (ticketsRetrieved < effectiveQuantity) {
            logger.warn("Customer could not retrieve all requested tickets for event: {}. Retrieved: {} of {}", eventId, ticketsRetrieved, effectiveQuantity);
        } else {
            logger.info("Customer successfully retrieved all requested tickets for event: {}.", eventId);
        }

        logger.info("Customer thread for event: {} completed.", eventId);
    }
}
