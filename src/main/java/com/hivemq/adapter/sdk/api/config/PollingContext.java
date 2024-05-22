package com.hivemq.adapter.sdk.api.config;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;

import java.util.List;

public interface PollingContext {

    /**
     * @return the topic on which the data will be available
     */
    @Nullable String getDestinationMqttTopic();

    /**
     * @return the Quality-of-Service for the MQTT publish containing the data
     */
    int getQos();

    /**
     * @return how the data will be published (one publish for all tags, a separate publish for each tag)
     */
    @NotNull MessageHandlingOptions getMessageHandlingOptions();

    /**
     * @return whether the MQTT publish payload should contain the timestamp
     */
    @NotNull Boolean getIncludeTimestamp();

    /**
     * @return whether the MQTT publish payload should contain the tag names
     */
    @NotNull Boolean getIncludeTagNames();

    /**
     * @return a list of MQTT user properties that are added to the MQTT publish
     */
    @NotNull List<UserProperty> getUserProperties();

}
