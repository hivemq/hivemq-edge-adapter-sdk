package com.hivemq.adapter.sdk.api.writing;

import org.jetbrains.annotations.NotNull;

public interface WritingProtocolAdapter<T extends WritePayload> {

    void write(@NotNull WriteInput<T> input, @NotNull WriteOutput writeOutput);

    @NotNull Class<T> getPayloadClass();
}
