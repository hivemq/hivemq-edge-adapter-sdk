package com.hivemq.adapter.sdk.api.eventsv2.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.eventsv2.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public class AdapterEvent extends Event {

    @JsonProperty(value = "adapterId", required = true)
    @Schema(name = "adapterId", description = "ID of the adapter starting")
    private final @NotNull String adapterId;

    @JsonProperty(value = "protocolId", required = true)
    @Schema(name = "protocolId", description = "ID of protocol of the adapter")
    private final @NotNull String protocolId;

    public AdapterEvent(
            @JsonProperty(value = "type", required = true)
            final @NotNull String type,
            @JsonProperty(value = "title", required = true)
            final @NotNull String title,
            @JsonProperty(value = "edgeId", required = true)
            final @NotNull String edgeId,
            @JsonProperty(value = "source", required = true)
            final @NotNull Source source,
            @JsonProperty(value = "severity", required = true)
            final @NotNull Severity severity,
            @JsonProperty(value = "created", required = true)
            final @NotNull Long created,
            @JsonProperty(value = "adapterId", required = true)
            final @NotNull String adapterId,
            @JsonProperty(value = "protocolId", required = true)
            final @NotNull String protocolId,
            @JsonProperty(value = "transactionId", required = true)
            final @NotNull String transactionId) {
        super(type, title, edgeId, source, severity, created, transactionId);
        this.adapterId = adapterId;
        this.protocolId = protocolId;
    }

    public @NotNull String getAdapterId() {
        return adapterId;
    }

    public @NotNull String getProtocolId() {
        return protocolId;
    }

    @Override
    public String getTopic() {
        return "eventlog/"+getSource()+"/"+getAdapterId();
    }

    public Builder builder(
            final @NotNull String adapterId,
            final @NotNull String protocolId,
            final @NotNull String edgeId,
            final @NotNull String transactionId) {
        return new Builder(adapterId, protocolId, edgeId, transactionId);
    }

    public static class Builder {
        final @NotNull String adapterId;
        final @NotNull String protocolId;
        final @NotNull String edgeId;
        final @NotNull String transactionId;
        final @NotNull Source source;
        final @NotNull Date created;

        private String type;
        private String title;
        private Severity severity;

        public Builder(@NotNull final String adapterId, @NotNull final String protocolId, @NotNull final String edgeId, @NotNull final String transactionId) {
            this.adapterId = adapterId;
            this.protocolId = protocolId;
            this.edgeId = edgeId;
            this.transactionId = transactionId;
            this.source = Source.ADAPTER;
            this.created = new Date();
        }

        public Builder error() {
            this.type = "AdapterErrorEvent";
            this.title = "Adapter encountered an error";
            this.severity = Severity.ERROR;
            return this;
        }

        public Builder starting() {
            this.type = "AdapterStartingEvent";
            this.title = "Adapter is starting";
            this.severity = Severity.INFO;
            return this;
        }

        public Builder subscribed() {
            this.type = "AdapterSubscribedEvent";
            this.title = "Adapter is subscribed";
            this.severity = Severity.INFO;
            return this;
        }

        public Builder firstTag() {
            this.type = "AdapterFirstTagEvent";
            this.title = "Adapter received first tag";
            this.severity = Severity.INFO;
            return this;
        }

        public Builder stopping() {
            this.type = "AdapterStoppingEvent";
            this.title = "Adapter is stopping";
            this.severity = Severity.INFO;
            return this;
        }

        public Builder stopped() {
            this.type = "AdapterStoppedEvent";
            this.title = "Adapter is stopped";
            this.severity = Severity.INFO;
            return this;
        }

        public AdapterEvent build() {
            Objects.requireNonNull(type);
            Objects.requireNonNull(title);
            Objects.requireNonNull(severity);
            return new AdapterEvent(type, title, edgeId, source, severity, created, adapterId, protocolId, transactionId);
        }

    }
    
}
