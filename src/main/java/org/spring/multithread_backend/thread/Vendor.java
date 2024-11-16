package org.spring.multithread_backend.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);

    private final TicketPool ticketPool;
    private final List<Ticket> ticketsToAdd;
    private final int ticketReleaseRate; // Tickets released per second

    public Vendor(TicketPool ticketPool, List<Ticket> ticketsToAdd, int ticketReleaseRate) {
        if (ticketPool == null) {
            throw new IllegalArgumentException("TicketPool cannot be null");
        }
        this.ticketPool = ticketPool;
        this.ticketsToAdd = ticketsToAdd;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    @Override
    public void run() {
        int ticketsReleased = 0;

        while (ticketsReleased < ticketsToAdd.size()) {
            try {
                int batchSize = Math.min(ticketReleaseRate, ticketsToAdd.size() - ticketsReleased);

                // Release tickets in batches
                synchronized (ticketPool) {
                    List<Ticket> batch = ticketsToAdd.subList(ticketsReleased, ticketsReleased + batchSize);
                    ticketPool.addTickets(batch);
                }

                logger.info("Vendor released {} tickets.", batchSize);
                ticketsReleased += batchSize;

                // Wait for 1 second between releases
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                logger.error("Vendor thread was interrupted.", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}