/*
 * Copyright 2023-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
