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
 * Bridge that wraps an existing {@link ProtocolAdapter} (old SDK interface) and exposes it
 * as a {@link ProtocolAdapter2} (new FSM interface).
 * <p>
 * This allows the FSM-based wrapper and manager to work with existing adapter implementations
 * without requiring them to be rewritten. Adapter modules should extend this class and override
 * {@link #supportsSouthbound()} to encode adapter-type-specific behavior.
 * <p>
 * Mapping:
 * <ul>
 *   <li>{@link #connect}(Northbound) calls {@link ProtocolAdapter#start} and blocks until the output signals completion</li>
 *   <li>{@link #connect}(Southbound) is a no-op (old adapters handle southbound internally during start)</li>
 *   <li>{@link #disconnect}(Northbound) calls {@link ProtocolAdapter#stop} and blocks until the output signals completion</li>
 *   <li>{@link #disconnect}(Southbound) is a no-op</li>
 *   <li>{@link #precheck} is a no-op (old adapters don't have this concept)</li>
 *   <li>{@link #supportsSouthbound} checks for {@link ProtocolAdapterCapability#WRITE}</li>
 * </ul>
 */
public class ProtocolAdapter2Bridge implements ProtocolAdapter2 {

    private final @NotNull ProtocolAdapter delegate;
    private final @NotNull ModuleServices moduleServices;

    public ProtocolAdapter2Bridge(
            final @NotNull ProtocolAdapter delegate, final @NotNull ModuleServices moduleServices) {
        this.delegate = delegate;
        this.moduleServices = moduleServices;
    }

    /**
     * Returns the underlying {@link ProtocolAdapter} delegate.
     * This is used by the wrapper to provide backward-compatible access to the old adapter interface.
     *
     * @return the wrapped ProtocolAdapter
     */
    public @NotNull ProtocolAdapter getDelegate() {
        return delegate;
    }

    @Override
    public @NotNull String getId() {
        return delegate.getId();
    }

    @Override
    public @NotNull ProtocolAdapterInformation getProtocolAdapterInformation() {
        return delegate.getProtocolAdapterInformation();
    }

    @Override
    public boolean supportsSouthbound() {
        return delegate.getProtocolAdapterInformation().getCapabilities().contains(ProtocolAdapterCapability.WRITE);
    }

    @Override
    public void precheck() throws ProtocolAdapterException {
        // Old adapters don't have a precheck concept — no-op
    }

    @Override
    public void connect(final @NotNull ProtocolAdapterConnectionDirection direction) throws ProtocolAdapterException {
        if (direction.isSouthbound()) {
            // Old adapters handle southbound internally during start — no separate action needed
            return;
        }
        final ProtocolAdapterStartInput input = () -> moduleServices;
        final CompletableFuture<Void> startFuture = new CompletableFuture<>();
        final AtomicReference<String> errorMessage = new AtomicReference<>();
        delegate.start(input, new ProtocolAdapterStartOutput() {
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

    @Override
    public void disconnect(final @NotNull ProtocolAdapterConnectionDirection direction) {
        if (direction.isSouthbound()) {
            // Old adapters handle southbound internally during stop — no separate action needed
            return;
        }
        final ProtocolAdapterStopInput input = new ProtocolAdapterStopInput() {};
        final CompletableFuture<Void> stopFuture = new CompletableFuture<>();
        delegate.stop(input, new ProtocolAdapterStopOutput() {
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
            // Disconnect should not throw — errors are silently absorbed.
            // The caller (ProtocolAdapterWrapper2) handles error reporting.
        }
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }
}
