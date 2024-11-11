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
package com.hivemq.adapter.sdk.api.model;

import com.hivemq.adapter.sdk.api.config.ProtocolAdapterConfig;
import com.hivemq.adapter.sdk.api.factories.AdapterFactories;
import com.hivemq.adapter.sdk.api.services.ModuleServices;
import com.hivemq.adapter.sdk.api.services.ProtocolAdapterMetricsService;
import com.hivemq.adapter.sdk.api.state.ProtocolAdapterState;
import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Object containing information and services for the construction of adapter instances
 *
 * @param <E> the ProtocolAdapterConfig implementation for this Protocol Adapter
 */
public interface ProtocolAdapterInput<E extends ProtocolAdapterConfig> {
    /**
     * @return the concrete config implementation for this protocol adapter
     */
    @NotNull E getConfig();

    /**
     * @return the concrete list of tags for this protocol adapter
     */
    @NotNull
    List<Tag> getTags();

    /**
     * @return the current HiveMQ Edge version
     */
    @NotNull String getVersion();

    /**
     * @return the {@link ProtocolAdapterState} for this adapter. Via this object changes in the connection and runtime status of the adapter can be made.
     */
    @NotNull ProtocolAdapterState getProtocolAdapterState();

    /**
     * @return Object that contains a variety of services useful for the start of a protocol adapter.
     */
    @NotNull ModuleServices moduleServices();

    /**
     * @return Object that contains a variety of factories for creating concrete implementations of the interfaces within the SDK.
     */
    @NotNull AdapterFactories adapterFactories();

    /**
     * @return the {@link ProtocolAdapterMetricsService} to increment and decrement metrics for the adapter.
     */
    @NotNull ProtocolAdapterMetricsService getProtocolAdapterMetricsHelper();
}
