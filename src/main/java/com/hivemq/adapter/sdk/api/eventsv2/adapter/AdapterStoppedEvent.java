package com.hivemq.adapter.sdk.api.eventsv2.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.eventsv2.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class AdapterStoppedEvent extends Event {

    @JsonProperty(value = "adapterId", required = true)
    @Schema(name = "adapterId", description = "ID of the adapter starting")
    private final @NotNull String adapterId;

    @JsonProperty(value = "protocolId", required = true)
    @Schema(name = "protocolId", description = "ID of protocol of the adapter")
    private final @NotNull String protocolId;

    public AdapterStoppedEvent(
            final String edgeId,
            final Date timestamp,
            final String adapterId,
            final String protocolId) {
        super("AdapterStoppedEvent",
                "Adapter has been stopped",
                edgeId,
                Source.ADAPTER,
                Severity.INFO,
                timestamp);
        this.adapterId = adapterId;
        this.protocolId = protocolId;
    }

    public @NotNull String getAdapterId() {
        return adapterId;
    }

    public @NotNull String getProtocolId() {
        return protocolId;
    }
}
