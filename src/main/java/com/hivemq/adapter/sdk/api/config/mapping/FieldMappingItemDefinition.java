package com.hivemq.adapter.sdk.api.config.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class FieldMappingItemDefinition {


    @JsonProperty("propertyPath")
    private final @NotNull String propertyPath;

    public FieldMappingItemDefinition(@JsonProperty("propertyPath") final @NotNull String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public @NotNull String getPropertyPath() {
        return propertyPath;
    }
}
