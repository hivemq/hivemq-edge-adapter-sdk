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
package com.hivemq.adapter.sdk.api.v2.model;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api.v2.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The adapter's output to the framework — the state-and-event side of the adapter, a tell-façade. It is the
 * symmetric counterpart of {@link ProtocolAdapterInput}: input is what the framework hands the adapter at
 * construction, output is how the adapter reports back. Each call is one thread-safe <i>tell</i>: the framework
 * implements every method by building an immutable event message and enqueueing it on the controlling handler's
 * mailbox. An adapter may therefore call any method from any thread (protocol library callbacks, receive
 * threads, …) with no locking.
 * <p>
 * Values are reported keyed by {@link Node} reference — the adapter is not required to set
 * {@link DataPoint#getTagName()} or {@link DataPoint#getAdapterId()}; the framework stamps them from the
 * owning node's tag before handing the value to northbound consumers.
 */
public interface ProtocolAdapterOutput {

    /**
     * Acknowledges {@link ProtocolAdapter#start()}.
     */
    void started();

    /**
     * Acknowledges {@link ProtocolAdapter#stop()}.
     */
    void stopped();

    /**
     * Acknowledges {@link ProtocolAdapter#connect()}.
     */
    void connected();

    /**
     * Acknowledges {@link ProtocolAdapter#disconnect()} — or reports a spontaneous connection loss.
     */
    void disconnected();

    /**
     * Reports a failure of the adapter ({@link ErrorScope#ADAPTER}) or of the connection
     * ({@link ErrorScope#CONNECTION}).
     *
     * @param scope  which recovery the failure admits.
     * @param reason a human-readable description of the failure.
     */
    void error(@NotNull ErrorScope scope, @NotNull String reason);

    /**
     * Reports the outcome of verifying one node of a {@link ProtocolAdapter#verifyBatch(List)}.
     *
     * @param node    the verified node.
     * @param outcome the verification outcome.
     */
    void verifyResult(@NotNull Node node, @NotNull VerifyOutcome outcome);

    /**
     * Reports one value — a poll response or a subscription push; the {@link Node} is the correlation key.
     *
     * @param node  the node the value belongs to.
     * @param value the reused v1 value; tag name and adapter identifier are stamped by the framework.
     */
    void dataPoint(@NotNull Node node, @NotNull DataPoint value);

    /**
     * Reports a per-node failure (failed poll, failed or lost subscription).
     *
     * @param node        the node the failure belongs to.
     * @param reason      a human-readable description of the failure.
     * @param spontaneous {@code true} if the failure arrived outside a command-response exchange — this one
     *                    bit selects the framework's recovery path (retry the operation vs. a full
     *                    power-cycle of the node's lifecycle).
     */
    void nodeError(@NotNull Node node, @NotNull String reason, boolean spontaneous);

    /**
     * Acknowledges one entry of a {@link ProtocolAdapter#writeBatch(List)}.
     *
     * @param node    the node the write targeted.
     * @param success whether the write succeeded.
     * @param reason  a human-readable description of the failure, or {@code null} on success.
     */
    void writeResult(@NotNull Node node, boolean success, @Nullable String reason);

    /**
     * Answers {@link ProtocolAdapter#browse(com.hivemq.adapter.sdk.api.v2.model.BrowseFilter)}.
     *
     * @param entries the discovered nodes.
     */
    void browseResult(@NotNull List<BrowseResultEntry> entries);
}
