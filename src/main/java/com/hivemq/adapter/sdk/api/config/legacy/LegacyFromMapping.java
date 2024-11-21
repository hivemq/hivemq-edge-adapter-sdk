package com.hivemq.adapter.sdk.api.config.legacy;

import com.hivemq.adapter.sdk.api.config.MessageHandlingOptions;
import org.jetbrains.annotations.NotNull;

public class LegacyFromMapping {

    private final @NotNull String topic;
    private final @NotNull String tagName;
    private final int maxQoS;
    private final @NotNull MessageHandlingOptions messageHandlingOptions;

    LegacyFromMapping(
            final @NotNull String topic,
            final @NotNull String tagName,
            final int maxQoS,
            final @NotNull MessageHandlingOptions messageHandlingOptions) {
        this.topic = topic;
        this.tagName = tagName;
        this.maxQoS = maxQoS;
        this.messageHandlingOptions = messageHandlingOptions;
    }

    public int getMaxQoS() {
        return maxQoS;
    }

    public @NotNull String getTagName() {
        return tagName;
    }

    public @NotNull String getTopic() {
        return topic;
    }
}
