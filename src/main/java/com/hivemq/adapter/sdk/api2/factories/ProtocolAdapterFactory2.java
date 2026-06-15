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
package com.hivemq.adapter.sdk.api2.factories;

import com.hivemq.adapter.sdk.api.schema.Schema;
import com.hivemq.adapter.sdk.api2.ProtocolAdapter2;
import com.hivemq.adapter.sdk.api2.ProtocolAdapterInformation2;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterOutput2;
import com.hivemq.adapter.sdk.api2.schema.AdapterConfigSchema;
import org.jetbrains.annotations.NotNull;

/**
 * The factory of one protocol adapter type. There is deliberately no capability accessor here —
 * {@link ProtocolAdapterInformation2} is the single home of capabilities.
 */
public interface ProtocolAdapterFactory2 {

    /**
     * @return the identity, display metadata, and capabilities of this adapter type — the single source.
     */
    @NotNull ProtocolAdapterInformation2 information();

    /**
     * Construct one adapter instance. Synchronous and cheap: no I/O, no connection — connecting is a separate,
     * framework-commanded step.
     *
     * @param input  the instance configuration, nodes, and services.
     * @param output the tell-façade the new adapter reports its state and events through.
     * @return the new, not-yet-started adapter instance.
     */
    @NotNull ProtocolAdapter2 createAdapter(
            @NotNull ProtocolAdapterInput2 input,
            @NotNull ProtocolAdapterOutput2 output);

    /**
     * @return the {@link AdapterConfigSchema} describing this adapter type's instance configuration — a new v2
     *         schema type, distinct from the reused v1 {@link Schema} (which describes data points and node
     *         values, not adapter configuration).
     */
    @NotNull AdapterConfigSchema adapterConfigSchema();

    /**
     * @return the reused v1 {@link Schema} describing this adapter type's node definitions.
     */
    @NotNull Schema nodeDefinitionSchema();
}
