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
import com.hivemq.adapter.sdk.api.exceptions.ProtocolAdapterException;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStartInput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStartOutput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStopInput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStopOutput;
import com.hivemq.adapter.sdk.api.schema.TagSchemaCreationInput;
import com.hivemq.adapter.sdk.api.schema.TagSchemaCreationOutput;
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
     * Start the adapter for the given direction. Called by the wrapper for each direction
     * (northbound, and southbound if supported). The default delegates to the 2-arg
     * {@link #start(ProtocolAdapterStartInput, ProtocolAdapterStartOutput)} overload, which is
     * sufficient for northbound-only adapters. Adapters that need direction-specific behavior
     * (e.g. OPC UA) should override this method.
     *
     * @param direction the connection direction (northbound or southbound)
     * @param input     the state associated with runtime. Allows the adapter to bind to required services.
     * @param output    the output to signal back to HiveMQ Edge the status of the start attempt.
     */
    default void start(
            final @NotNull ProtocolAdapterConnectionDirection direction,
            final @NotNull ProtocolAdapterStartInput input,
            final @NotNull ProtocolAdapterStartOutput output) {
        start(input, output);
    }

    /**
     * Start the adapter, establishing a connection to any internal or external device using the
     * configuration supplied during instantiation. Most adapters (northbound-only) override this
     * method. The default signals success.
     *
     * @param input  the state associated with runtime. Allows the adapter to bind to required services.
     * @param output the output to signal back to HiveMQ Edge the status of the start attempt.
     */
    @Deprecated
    default void start(
            final @NotNull ProtocolAdapterStartInput input,
            final @NotNull ProtocolAdapterStartOutput output) {
        final String errorMessage ="Start not implemented for this adapter";
        output.failStart(new RuntimeException(errorMessage), errorMessage);
    }

    /**
     * Stop the adapter for the given direction. Called by the wrapper for each direction
     * (northbound, and southbound if supported). The default delegates to the 2-arg
     * {@link #stop(ProtocolAdapterStopInput, ProtocolAdapterStopOutput)} overload, which is
     * sufficient for northbound-only adapters. Adapters that need direction-specific behavior
     * (e.g. OPC UA) should override this method.
     *
     * @param direction the connection direction (northbound or southbound)
     * @param input     the input for the stop
     * @param output    the output to signal back to HiveMQ Edge the status of the stop attempt.
     */
    default void stop(
            final @NotNull ProtocolAdapterConnectionDirection direction,
            final @NotNull ProtocolAdapterStopInput input,
            final @NotNull ProtocolAdapterStopOutput output) {
        stop(input, output);
    }

    /**
     * Stop the adapter. Stopping must release any network interface connections or local resources
     * associated with the connection to the device. Most adapters (northbound-only) override this
     * method. The default signals success.
     *
     * @param input  the input for the stop
     * @param output the output to signal back to HiveMQ Edge the status of the stop attempt.
     */
    @Deprecated
    default void stop(
            final @NotNull ProtocolAdapterStopInput input,
            final @NotNull ProtocolAdapterStopOutput output) {
        final String errorMessage ="Stop not implemented for this adapter";
        output.failStop(new RuntimeException(errorMessage), errorMessage);
    }

    /**
     * Validate configuration before connecting. Called during the Precheck phase.
     *
     * @throws ProtocolAdapterException if configuration is invalid
     */
    default void precheck() throws ProtocolAdapterException {
    }

    /**
     * This method needs to be implemented in case the adapter provides the possibility to discover values at the PLC.
     * @param input an input object containing information during the discovery process
     * @param output an output object to set the discovered nodes on and signal edge that the discovery process has finished.
     */
    default void discoverValues(
            final @NotNull ProtocolAdapterDiscoveryInput input,
            final @NotNull ProtocolAdapterDiscoveryOutput output) {
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


    /**
     * This method is intended to retrieve the json schema that represents the data of a tag on a PLC.
     * The json schema is used to create mappings between incoming and outgoing data.
     *
     * @param input the input object holding information necessary for the operation.
     * @param output the output object on which the result of the operation can be set.
     */
    default void createTagSchema(final @NotNull TagSchemaCreationInput input, final @NotNull TagSchemaCreationOutput output){
        output.notSupported();
    }
}
