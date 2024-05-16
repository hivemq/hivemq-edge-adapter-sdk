package com.hivemq.adapter.sdk.api.factories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.adapter.sdk.api.config.UserProperty;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;

import java.util.List;

public interface PollingContextFactory {

    /**
     * @param destination    the mqtt topic on which the data should be published
     * @param qos            the mqtt qos for the data
     * @param userProperties mqtt user properties for the publish that will contain the data
     * @return an {@link PollingContext} containing information how the data will be published.
     */
    @NotNull PollingContext create(
            @JsonProperty("destination") @Nullable final String destination,
            @JsonProperty("qos") final int qos,
            @JsonProperty("userProperties") @Nullable List<UserProperty> userProperties);
}