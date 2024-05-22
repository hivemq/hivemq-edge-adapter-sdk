package com.hivemq.adapter.sdk.api.events.model;

import com.hivemq.adapter.sdk.api.events.EventService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for events for the {@link EventService}.
 */
public interface Event {

    /**
     * @return the severity of the event
     */
    @NotNull SEVERITY getSeverity();

    /**
     * @return the message of this event
     */
    @NotNull String getMessage();

    /**
     * @return the {@link Payload} associated with this event
     */
    @Nullable Payload getPayload();

    /**
     * @return unix timestamp when this event was created
     */
    @NotNull Long getTimestamp();

    /**
     * @return the type identifier of the associated object
     */
    @Nullable TypeIdentifier getAssociatedObject();

    /**
     * @return the type identifier of the source of this event
     */
    @Nullable TypeIdentifier getSource();

    /**
     * Represents a uniquely identifiable object in the system.
     * @return The system-wide identifier of the object
     */
    @NotNull TypeIdentifier getIdentifier();

    enum SEVERITY {
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }
}
