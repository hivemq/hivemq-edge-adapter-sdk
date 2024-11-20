package com.hivemq.adapter.sdk.api.eventsv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public abstract class Event {
    public enum Severity {
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }

    public enum Source {
        EDGE,
        ADAPTER,
        BRIDGE
    }

    @JsonProperty(value = "type",required = true)
    @Schema(name = "type", description = "Type of the event")
    private final @NotNull String type;

    @JsonProperty(value = "source", required = true)
    @Schema(name = "source", description = "Source of the event")
    private final @NotNull Event.Source source;

    @JsonProperty(value = "title", required = true)
    @Schema(name = "title", description = "Human readable title")
    private final @NotNull String title;

    @JsonProperty(value = "edgeId", required = true)
    @Schema(name = "edgeId", description = "Globally unique id of the edge broker")
    private final @NotNull String edgeId;

    @JsonProperty(value = "severity", required = true)
    @Schema(name = "severity", description = "Severity of the event")
    private final @NotNull Event.Severity severity;

    @JsonProperty(value = "created", required = true)
    @Schema(name = "created", description = "Time at which the event occured")
    private final @NotNull Long created;

    @JsonProperty(value = "transactionId", required = true)
    @Schema(name = "transactionId", description = "Backend transaction id")
    private final @NotNull String transactionId;

    @JsonCreator
    public Event(
            @JsonProperty(value = "type",required = true)
            final @NotNull String type,
            @JsonProperty(value = "title",required = true)
            final @NotNull String title,
            @JsonProperty(value = "edgeId",required = true)
            final @NotNull String edgeId,
            @JsonProperty(value = "source",required = true)
            final @NotNull Event.Source source,
            @JsonProperty(value = "severity",required = true)
            final @NotNull Event.Severity severity,
            @JsonProperty(value = "created",required = true)
            final @NotNull Long created,
            @JsonProperty(value = "transactionId", required = true)
            final @NotNull String transactionId) {
        this.type = type;
        this.title = title;
        this.edgeId = edgeId;
        this.severity = severity;
        this.created = created;
        this.source = source;
        this.transactionId = transactionId;
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

    public @NotNull Event.Severity getSeverity() {
        return severity;
    }

    public @NotNull Long getCreated() {
        return created;
    }

    public @NotNull Event.Source getSource() {
        return source;
    }

    public @NotNull String getTransactionId() {
        return transactionId;
    }

    public abstract String getTopic();

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", source=" + source +
                ", title='" + title + '\'' +
                ", edgeId='" + edgeId + '\'' +
                ", severity=" + severity +
                ", created=" + created +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Event event = (Event) o;
        return Objects.equals(type, event.type) && source == event.source && Objects.equals(title, event.title) && Objects.equals(edgeId, event.edgeId) && severity == event.severity && Objects.equals(created, event.created) && Objects.equals(transactionId, event.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, source, title, edgeId, severity, created, transactionId);
    }
}
