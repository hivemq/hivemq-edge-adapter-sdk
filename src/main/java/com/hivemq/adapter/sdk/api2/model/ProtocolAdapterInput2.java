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
package com.hivemq.adapter.sdk.api2.model;

import com.hivemq.adapter.sdk.api.config.ProtocolSpecificAdapterConfig;
import com.hivemq.adapter.sdk.api2.node.NodeTagPair;
import com.hivemq.adapter.sdk.api2.services.ProtocolAdapterService;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Everything one adapter instance is constructed from.
 */
public interface ProtocolAdapterInput2 {

    /**
     * @return the identifier of the adapter instance, unique within this Edge instance.
     */
    @NotNull String adapterId();

    /**
     * @return the typed instance configuration — a reused v1 {@link ProtocolSpecificAdapterConfig}. Its schema is
     *         the factory's {@code adapterConfigSchema()}, an
     *         {@link com.hivemq.adapter.sdk.api2.schema.AdapterConfigSchema}.
     */
    @NotNull ProtocolSpecificAdapterConfig adapterConfig();

    /**
     * @return the Node/Tag pairs this instance serves.
     */
    @NotNull List<NodeTagPair> nodes();

    /**
     * @return the services the framework provides to the instance.
     */
    @NotNull ProtocolAdapterService services();
}
