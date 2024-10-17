package com.hivemq.adapter.sdk.api.config.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class FieldMappingItem {

    @JsonProperty("source")
    private final @NotNull FieldMappingItemDefinition source;

    @JsonProperty("destination")
    private final @NotNull FieldMappingItemDefinition destination;

    public FieldMappingItem(@JsonProperty("source") final @NotNull FieldMappingItemDefinition source,
                            @JsonProperty("destination") final @NotNull FieldMappingItemDefinition destination) {
        this.source = source;
        this.destination = destination;
    }

    public @NotNull FieldMappingItemDefinition getDestination() {
        return destination;
    }

    public @NotNull FieldMappingItemDefinition getSource() {
        return source;
    }
}
