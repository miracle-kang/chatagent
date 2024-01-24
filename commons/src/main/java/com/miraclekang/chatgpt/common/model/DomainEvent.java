package com.miraclekang.chatgpt.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class DomainEvent implements Serializable {

    private int eventVersion;
    private final LocalDateTime occurredOn;

    public DomainEvent() {
        eventVersion = 1;
        occurredOn = LocalDateTime.now();
    }

    public int increaseVersion() {
        return ++eventVersion;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
