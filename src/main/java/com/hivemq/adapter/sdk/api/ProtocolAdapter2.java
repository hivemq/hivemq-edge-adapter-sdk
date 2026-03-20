/*
 * Copyright 2019-present HiveMQ GmbH
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

import com.hivemq.adapter.sdk.api.exceptions.ProtocolAdapterException;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStartInput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStartOutput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStopInput;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterStopOutput;
import com.hivemq.adapter.sdk.api.services.ModuleServices;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simplified protocol adapter interface for the FSM-based redesign.
 * <p>
 * Implementations provide access to the wrapped legacy {@link ProtocolAdapter} and module services.
 * The default methods implement the synchronous lifecycle expected by the FSM wrapper.
 */
public interface ProtocolAdapter2 {

    /**
     * Returns the underlying legacy protocol adapter instance.
     *
     * @return wrapped legacy adapter
     */
    @NotNull
    ProtocolAdapter getLegacyAdapter();

    /**
     * Returns the module services used when delegating lifecycle calls to the legacy adapter.
     *
     * @return module services
     */
    @NotNull
    ModuleServices getModuleServices();

    /**
     * Get the adapter's unique identifier.
     */
    default @NotNull String getId() {
        return getLegacyAdapter().getId();
    }

    /**
     * Get adapter information (protocol type, capabilities, etc.).
     */
    default @NotNull ProtocolAdapterInformation getProtocolAdapterInformation() {
        return getLegacyAdapter().getProtocolAdapterInformation();
    }

    /**
     * Check if this adapter supports southbound (MQTT to device) communication.
     *
     * @return true if southbound is supported
     */
    default boolean supportsSouthbound() {
        return getLegacyAdapter().getProtocolAdapterInformation().getCapabilities().contains(ProtocolAdapterCapability.WRITE);
    }

    /**
     * Validate configuration before connecting.
     * Called during the Precheck phase.
     *
     * @throws ProtocolAdapterException if configuration is invalid
     */
    default void precheck() throws ProtocolAdapterException {
        // Legacy adapters do not expose a precheck API.
    }

    /**
     * Establish connection to the device/service.
     *
     * @param direction the connection direction (northbound vs southbound)
     * @throws ProtocolAdapterException on connection failure
     */
    default void connect(final @NotNull ProtocolAdapterConnectionDirection direction) throws ProtocolAdapterException {
        if (direction.isSouthbound()) {
            // Legacy adapters handle southbound internally during start.
            return;
        }
        final ProtocolAdapterStartInput input = () -> getModuleServices();
        final CompletableFuture<Void> startFuture = new CompletableFuture<>();
        final AtomicReference<String> errorMessage = new AtomicReference<>();
        getLegacyAdapter().start(input, new ProtocolAdapterStartOutput() {
            @Override
            public void startedSuccessfully() {
                startFuture.complete(null);
            }

            @Override
            public void failStart(final @NotNull Throwable throwable, final @Nullable String message) {
                errorMessage.set(message);
                startFuture.completeExceptionally(throwable);
            }
        });
        try {
            startFuture.get();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProtocolAdapterException("Adapter start interrupted for '" + getId() + "'", e);
        } catch (final ExecutionException e) {
            final Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new ProtocolAdapterException(
                    "Adapter start failed for '" + getId() + "': " + errorMessage.get(), cause);
        }
    }

    /**
     * Disconnect from the device/service.
     *
     * @param direction the connection direction (northbound vs southbound)
     */
    default void disconnect(final @NotNull ProtocolAdapterConnectionDirection direction) {
        if (direction.isSouthbound()) {
            // Legacy adapters handle southbound internally during stop.
            return;
        }
        final ProtocolAdapterStopInput input = new ProtocolAdapterStopInput() {};
        final CompletableFuture<Void> stopFuture = new CompletableFuture<>();
        getLegacyAdapter().stop(input, new ProtocolAdapterStopOutput() {
            @Override
            public void stoppedSuccessfully() {
                stopFuture.complete(null);
            }

            @Override
            public void failStop(final @NotNull Throwable throwable, final @Nullable String errorMessage) {
                stopFuture.completeExceptionally(throwable);
            }
        });
        try {
            stopFuture.get();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final ExecutionException e) {
            // Disconnect should not throw; caller handles error reporting.
        }
    }

    /**
     * Destroy the adapter instance.
     */
    default void destroy() {
        getLegacyAdapter().destroy();
    }
}
