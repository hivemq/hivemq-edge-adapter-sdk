package com.hivemq.adapter.sdk.api.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface to overwrite the default creation of payloads from samples.
 */
@FunctionalInterface
public interface JsonPayloadCreator {

    /**
     * @param sample the sample containing the data points and the polling context
     * @param objectMapper object mapper instance that can be used to create the payloads
     *
     * @return a list containing the payload for the mqtt publishes that will be created.
     *         The implementation can decide to put all data points into a single payload or split them.
     *         This is indicated via {@link PollingContext#getMessageHandlingOptions()}.
     *         The resulting bytes MUST be utf-8 encoded jsons.
     */
    @NotNull
    List<byte[]> convertToJson(@NotNull ProtocolAdapterDataSample sample, @NotNull ObjectMapper objectMapper);
}
