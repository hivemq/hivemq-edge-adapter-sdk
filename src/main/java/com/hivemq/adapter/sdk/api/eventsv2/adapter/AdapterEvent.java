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

    public AdapterEvent(@NotNull final String type, @NotNull final String title, @NotNull final String edgeId, @NotNull final Source source, @NotNull final Severity severity, @NotNull final Date created, @NotNull final String adapterId, @NotNull final String protocolId) {
        super(type, title, edgeId, source, severity, created);
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
