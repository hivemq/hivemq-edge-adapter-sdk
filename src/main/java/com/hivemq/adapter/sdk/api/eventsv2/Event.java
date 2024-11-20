package com.hivemq.adapter.sdk.api.eventsv2;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public abstract class Event {
    public enum SEVERITY {
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }

    public enum SOURCE {
        EDGE,
        ADAPTER,
        BRIDGE
    }

    private final @NotNull String type;

    private final @NotNull SOURCE source;

    private final @NotNull String title;

    private final @NotNull String edgeId;

    private final @NotNull SEVERITY severity;

    private final @NotNull Date timestamp;

    public Event(
            final @NotNull String type,
            final @NotNull String title,
            final @NotNull String edgeId,
            final @NotNull SOURCE source,
            final @NotNull SEVERITY severity,
            final @NotNull Date timestamp) {
        this.type = type;
        this.title = title;
        this.edgeId = edgeId;
        this.severity = severity;
        this.timestamp = timestamp;
        this.source = source;
    }

    public @NotNull String getType() {
        return type;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public @NotNull String getEdgeId() {
        return edgeId;
    }

    public @NotNull SEVERITY getSeverity() {
        return severity;
    }

    public @NotNull Date getTimestamp() {
        return timestamp;
    }

    public @NotNull SOURCE getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" +
                type +
                '\'' +
                ", source=" +
                source +
                ", title='" +
                title +
                '\'' +
                ", edgeId='" +
                edgeId +
                '\'' +
                ", severity=" +
                severity +
                ", timestamp=" +
                timestamp +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Event event = (Event) o;
        return Objects.equals(type, event.type) &&
                source == event.source &&
                Objects.equals(title, event.title) &&
                Objects.equals(edgeId, event.edgeId) &&
                severity == event.severity &&
                Objects.equals(timestamp, event.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, source, title, edgeId, severity, timestamp);
    }
}
