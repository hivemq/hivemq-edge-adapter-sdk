package com.hivemq.adapter.sdk.api.config.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FieldMapping {

    @JsonProperty("fieldMappingItems")
    private final @NotNull List<FieldMappingItem> fieldMappingItems;

    @JsonCreator
    public FieldMapping(@JsonProperty("fieldMappingItems") @NotNull List<FieldMappingItem> fieldMappingItems) {
        this.fieldMappingItems = fieldMappingItems;
    }

    public @NotNull List<FieldMappingItem> getFieldMappingItems() {
        return fieldMappingItems;
    }
}
