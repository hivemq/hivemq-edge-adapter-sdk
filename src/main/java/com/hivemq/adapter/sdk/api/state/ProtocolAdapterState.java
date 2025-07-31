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
package com.hivemq.adapter.sdk.api.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the current status of a protocol adapter.
 * There are different methods to update and get both the runtime status and the connection status of a protocol adapter.
 */
public interface ProtocolAdapterState {

    /**
     * Enum for connection status
     */
    enum RuntimeStatus {
        /**
         * The adapter was started successful and is running currently.
         */
        STARTED,
        /**
         * Adapter is trying to start (used also after initial failure)
         */
        STARTING,
        /**
         * The adapter was stopped.
         */
        STOPPED
    }

    /**
     * Enum for the connection status for this adapter.
     */
    enum ConnectionStatus {
        /**
         * The adapter is currently connected to the PLC.
         */
        CONNECTED,
        /**
         * The adapter is currently disconnected from the PLC.
         */
        DISCONNECTED,
        /**
         * The adapter does not establish a standing connection to a PLC.
         */
        STATELESS,
        /**
         * The connection status for this adapter is currently not known.
         */
        UNKNOWN,
        /**
         * There is currently an error when trying to connect to the PLC.
         */
        ERROR
    }


    /**
     * @return the last error message this adapter created or null if none was created yet.
     */
    @Nullable String getLastErrorMessage();

    /**
     * Updates the current connection status
     *
     * @param connectionStatus the updated status
     * @return true: the update set a new state, false: the current and the updated status is the same
     */
    boolean setConnectionStatus(@NotNull ConnectionStatus connectionStatus);

    /**
     * @return the current connection status
     */
    @NotNull ConnectionStatus getConnectionStatus();

    /**
     * Signalizes that there was an error during connection establishment to the PLC
     *
     * @param throwable    a throwable indicating the cause.
     * @param errorMessage a error message to deliver more insights into the problem.
     */
    void setErrorConnectionStatus(
            @Nullable Throwable throwable,
            @Nullable String errorMessage);

    /**
     * Reports any error message for this adapter.
     *
     * @param throwable    a throwable indicating the cause.
     * @param errorMessage a error message to deliver more insights into the problem.
     * @param sendEvent    true: an {@link com.hivemq.adapter.sdk.api.events.model.Event} for this error is created and sent via the {@link com.hivemq.adapter.sdk.api.events.EventService}, False: no event is created or sent.
     */
    void reportErrorMessage(
            @Nullable Throwable throwable,
            @Nullable String errorMessage,
            boolean sendEvent);

    /**
     * @param runtimeStatus the new runtime status
     */
    void setRuntimeStatus(@NotNull RuntimeStatus runtimeStatus);

    /**
     * @return the current runtime status.
     */
    @NotNull RuntimeStatus getRuntimeStatus();
}
