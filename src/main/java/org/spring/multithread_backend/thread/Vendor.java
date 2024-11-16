package org.spring.multithread_backend.thread;

import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Vendor implements Runnable {
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
                    System.out.println("Vendor released " + batchSize + " tickets.");
                }

                ticketsReleased += batchSize;

                // Wait for 1 second between releases
                Thread.sleep(1000);
                System.out.println("released");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}