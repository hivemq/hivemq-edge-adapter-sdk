package com.hivemq.adapter.sdk.api.writing;

import com.hivemq.adapter.sdk.api.config.WriteContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WritingProtocolAdapter<T extends WritePayload, C extends WriteContext> {

    void write(@NotNull WriteInput<T, C> input, @NotNull WriteOutput writeOutput);

    @NotNull
    Class<T> getPayloadClass();

    @NotNull
    List<? extends WriteContext> getWriteContexts();
}
