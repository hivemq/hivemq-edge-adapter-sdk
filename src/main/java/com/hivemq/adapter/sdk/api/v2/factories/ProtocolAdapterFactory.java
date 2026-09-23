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
package com.hivemq.adapter.sdk.api.v2.factories;

import com.hivemq.adapter.sdk.api.schema.Schema;
import com.hivemq.adapter.sdk.api.v2.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.v2.ProtocolAdapterInformation;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterInput;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput;
import org.jetbrains.annotations.NotNull;

/**
 * The factory of one protocol adapter type. There is deliberately no capability accessor here —
 * {@link ProtocolAdapterInformation} is the single home of capabilities.
 */
public interface ProtocolAdapterFactory {

    /**
     * @return the identity, display metadata, and capabilities of this adapter type — the single source.
     */
    @NotNull ProtocolAdapterInformation information();

    /**
     * Construct one adapter instance. Synchronous and cheap: no I/O, no connection — connecting is a separate,
     * framework-commanded step.
     *
     * @param input  the instance configuration, nodes, and services.
     * @param output the tell-façade the new adapter reports its state and events through.
     * @return the new, not-yet-started adapter instance.
     */
    @NotNull ProtocolAdapter createAdapter(
            @NotNull ProtocolAdapterInput input,
            @NotNull ProtocolAdapterOutput output);

    /**
     * @return the reused v1 {@link Schema} describing this adapter type's instance configuration.
     */
    @NotNull Schema adapterConfigSchema();

    /**
     * @return the reused v1 {@link Schema} describing this adapter type's node definitions.
     */
    @NotNull Schema nodeDefinitionSchema();
}
