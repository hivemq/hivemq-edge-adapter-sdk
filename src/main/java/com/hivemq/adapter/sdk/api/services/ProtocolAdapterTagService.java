package com.hivemq.adapter.sdk.api.services;

import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

public interface ProtocolAdapterTagService {

    <T> @NotNull Tag<T> resolveTag(final @NotNull String tagName, final @NotNull Class<T> addressClass);

    @NotNull
    AddStatus addTag(@NotNull Tag<?> tag);


    enum AddStatus {
        SUCCESS,
        ALREADY_PRESENT
    }
}

