package com.hivemq.adapter.sdk.api.config.legacy;

import com.hivemq.adapter.sdk.api.config.ProtocolAdapterConfig;
import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This represents the conversion result of a legacy config into the current state.
 *
 * @deprecated this class will be removed in early 2025, there won't be a replacement.
 */
@Deprecated()
public class ConfigTagsTuple {
    private final @NotNull ProtocolAdapterConfig config;
    private final @NotNull List<? extends Tag> tags;

    public ConfigTagsTuple(@NotNull final ProtocolAdapterConfig config, @NotNull final List<? extends Tag> tags) {
        this.config = config;
        this.tags = tags;
    }

    public @NotNull ProtocolAdapterConfig getConfig() {
        return config;
    }

    public @NotNull List<? extends Tag> getTags() {
        return tags;
    }
}
