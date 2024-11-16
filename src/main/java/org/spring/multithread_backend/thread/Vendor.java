package org.spring.multithread_backend.thread;

import org.spring.multithread_backend.model.Ticket;

import java.util.List;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final List<Ticket> ticketsToAdd;

    public Vendor(TicketPool ticketPool, List<Ticket> ticketsToAdd) {
        if (ticketPool == null) {
            throw new IllegalArgumentException("TicketPool cannot be null");
        }
        this.ticketPool = ticketPool;
        this.ticketsToAdd = ticketsToAdd;
    }

    @Override
    public void run() {
        synchronized (ticketPool) {
            ticketPool.addTickets(ticketsToAdd);
            System.out.println("Vendor added " + ticketsToAdd.size() + " tickets.");
        }
    }
}