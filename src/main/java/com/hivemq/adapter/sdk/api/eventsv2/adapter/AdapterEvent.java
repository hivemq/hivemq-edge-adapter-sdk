package com.hivemq.adapter.sdk.api.eventsv2.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.eventsv2.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class AdapterEvent extends Event {

    @JsonProperty(value = "adapterId", required = true)
    @Schema(name = "adapterId", description = "ID of the adapter starting")
    private final @NotNull String adapterId;

    @JsonProperty(value = "protocolId", required = true)
    @Schema(name = "protocolId", description = "ID of protocol of the adapter")
    private final @NotNull String protocolId;

    public AdapterEvent(
            @JsonProperty(value = "type", required = true)
            @NotNull final String type,
            @JsonProperty(value = "title", required = true)
            @NotNull final String title,
            @JsonProperty(value = "edgeId", required = true)
            @NotNull final String edgeId,
            @JsonProperty(value = "source", required = true)
            @NotNull final Source source,
            @JsonProperty(value = "severity", required = true)
            @NotNull final Severity severity,
            @JsonProperty(value = "created", required = true)
            @NotNull final Date created,
            @JsonProperty(value = "adapterId", required = true)
            @NotNull final String adapterId,
            @JsonProperty(value = "protocolId", required = true)
            @NotNull final String protocolId,
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


}
