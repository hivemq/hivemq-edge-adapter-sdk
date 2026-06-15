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
package com.hivemq.adapter.sdk.api2.template;

import com.hivemq.adapter.sdk.api.config.ProtocolSpecificAdapterConfig;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api2.messaging.MessageDispatcher;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2;
import com.hivemq.adapter.sdk.api2.node.NodeTagPair;
import com.hivemq.adapter.sdk.api2.services.ProtocolAdapterService;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A minimal {@link ProtocolAdapterInput2} for the template tests: an empty typed configuration, no nodes, and
 * the given dispatcher.
 */
record TestProtocolAdapterInput(
        @NotNull String adapterId,
        @NotNull ProtocolSpecificAdapterConfig adapterConfig,
        @NotNull List<NodeTagPair> nodes,
        @NotNull ProtocolAdapterService services) implements ProtocolAdapterInput2 {

    static @NotNull TestProtocolAdapterInput create(
            final @NotNull String adapterId, final @NotNull MessageDispatcher dispatcher) {
        return new TestProtocolAdapterInput(
                adapterId,
                new EmptyConfig(),
                List.of(),
                new TestProtocolAdapterService(new TestDataPointFactory(), dispatcher));
    }

    /**
     * A trivial, empty reused-v1 configuration — the template tests carry no adapter-specific settings.
     */
    record EmptyConfig() implements ProtocolSpecificAdapterConfig {
    }

    record TestProtocolAdapterService(@NotNull DataPointFactory dataPointFactory, @NotNull MessageDispatcher dispatcher)
            implements ProtocolAdapterService {
    }
}
