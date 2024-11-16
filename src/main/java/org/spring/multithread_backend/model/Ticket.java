package org.spring.multithread_backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tickets")
@Getter
@Setter
public class Ticket {

    @Id
    private String id;
    private String eventId;
    private boolean sold;
}
