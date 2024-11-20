package com.hivemq.adapter.sdk.api.eventsv2;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EventSendingService {
    void publish(Event event);
    List<Event> readEvents(final @Nullable Long sinceTimestamp, final @Nullable Integer limit); 
}
