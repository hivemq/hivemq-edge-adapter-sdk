package com.hivemq.adapter.sdk.api.eventsv2;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public abstract class Event {
    public enum SEVERITY {
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }

    @JsonProperty(value = "type",required = true)
    @Schema(name = "type", description = "Type of the event")
    private final @NotNull String type;

    @JsonProperty(value = "title", required = true)
    @Schema(name = "title", description = "Human readable title")
    private final @NotNull String title;

    @JsonProperty(value = "edgeId", required = true)
    @Schema(name = "edgeId", description = "Globally unique id of the edge broker")
    private final @NotNull String edgeId;

    @JsonProperty(value = "severity", required = true)
    @Schema(name = "severity", description = "Severity of the event")
    private final @NotNull SEVERITY severity;

    @JsonProperty(value = "timestamp", required = true)
    @Schema(name = "timestamp", description = "Time at which the event occured")
    private final @NotNull Date timestamp;

    public Event(
            @JsonProperty(value = "type",required = true)
            final @NotNull String type,
            @JsonProperty(value = "title", required = true)
            final @NotNull String title,
            @JsonProperty(value = "edgeId", required = true)
            final @NotNull String edgeId,
            @JsonProperty(value = "severity", required = true)
            final @NotNull SEVERITY severity,
            @JsonProperty(value = "timestamp", required = true)
            final @NotNull Date timestamp) {
        this.type = type;
        this.title = title;
        this.edgeId = edgeId;
        this.severity = severity;
        this.timestamp = timestamp;
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
}
