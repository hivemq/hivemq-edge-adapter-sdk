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
package com.hivemq.adapter.sdk.api;

import com.hivemq.adapter.sdk.api.discovery.ProtocolAdapterDiscoveryInput;
import com.hivemq.adapter.sdk.api.discovery.ProtocolAdapterDiscoveryOutput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStartInput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStartOutput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStopInput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStopOutput;
import org.jetbrains.annotations.NotNull;


/**
 * A protocol adapter is the resource responsible for connecting to and providing data from disparate remote or local
 * device. The implementation must manage its resources internally, and adhere to the semantic lifecycle applied to the
 * adapter instances.
 * <p>
 *
 * @since 2023.1
 */
public interface ProtocolAdapter {

    /**
     * The adapter identifier represents a unique id/name of the instance within the runtime. This value should be considered
     * immutable.
     *
     * @return A string ID conform to the regex ([a-zA-Z_0-9-_])* which uniquely identifies the instance of
     * the adapter.
     */
    @NotNull String getId();

    /**
     * Start the adapter, establishing a connection to any internal or external device using the configuration supplied
     * during instantiation.
     *
     * @param input  - the state associated with runtime. Allows the adapter to bind to required services in a decoupled
     *               manner
     * @param output - the output to signal back to HiveMQ Edge the status of the start attempt.
     */
    void start(
            @NotNull ProtocolAdapterStartInput input, @NotNull ProtocolAdapterStartOutput output);

    /**
     * Stop the adapter. Stopping the adapter must release any network interface connections or local resources
     * associated with the connection to the device. State relating to the connection however will be retained allowing
     * the start method to restart the adapter.
     *
     * @param input  the input for the stop (currently empty)
     * @param output the output to signal back to HiveMQ Edge the status of the stop attempt.
     */
     void stop(@NotNull ProtocolAdapterStopInput input, @NotNull ProtocolAdapterStopOutput output);

    /**
     * This method needs to be implemented in case the adapter provides the possibility to discover values at the PLC.
     * @param input an input object containing information during the discovery process
     * @param output an output object to set the discovered nodes on and signal edge that the discovery process has finished.
     */
    default void discoverValues(
            @NotNull ProtocolAdapterDiscoveryInput input,
            @NotNull ProtocolAdapterDiscoveryOutput output) {
        output.fail("Adapter type does not support discovery");
    }

    /**
     * @return the {@link ProtocolAdapterInformation} containing information about this protocol adapter instance.
     */
    @NotNull ProtocolAdapterInformation getProtocolAdapterInformation();

    /**
     * Called by the framework when the instance will be discarded
     */
    default void destroy() {

    }
}
