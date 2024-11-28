package com.hivemq.adapter.sdk.api.mappings.fields;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FieldMapping {

    @NotNull List<Instruction> getInstructions();

    @NotNull Metadata getMetaData();
}
