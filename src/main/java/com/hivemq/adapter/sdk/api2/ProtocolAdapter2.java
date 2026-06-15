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
package com.hivemq.adapter.sdk.api2;

import com.hivemq.adapter.sdk.api2.model.BrowseFilter;
import com.hivemq.adapter.sdk.api2.model.WriteEntry;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterOutput2;
import com.hivemq.adapter.sdk.api2.node.Node;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The protocol adapter command interface — <b>pure mechanism</b>. The adapter only executes commands and
 * reports events through {@link ProtocolAdapterOutput2}; it has ZERO retry, backoff, reconnect, or
 * scheduling logic — the controlling framework owns all policy.
 * <p>
 * Every command is asynchronous and acknowledged by a callback event. Batch variants are always what the
 * framework calls; how an implementation decomposes a batch is its own concern.
 * <p>
 * <b>Long-running commands.</b> An implementation may block its own dispatch thread (a blocking
 * {@code connect()} against a protocol library is normal); queued commands simply wait, and the framework's
 * watchdogs bound the damage. A long {@code browse} walk, however, starves polls for its whole duration on a
 * single-threaded implementation — adapters with large address spaces should implement browse asynchronously
 * inside the adapter (issue the walk on library threads, report
 * {@link ProtocolAdapterOutput2#browseResult(List)} via the thread-safe callbacks).
 */
public interface ProtocolAdapter2 {

    /**
     * @return the identifier of this adapter instance, unique within this Edge instance.
     */
    @NotNull String adapterId();

    /**
     * Start the adapter (allocate resources; no connection yet). Acknowledged by
     * {@link ProtocolAdapterOutput2#started()} or
     * {@link ProtocolAdapterOutput2#error(com.hivemq.adapter.sdk.api2.model.ErrorScope, String)} with
     * scope {@code ADAPTER}.
     */
    void start();

    /**
     * Stop the adapter (release resources). Acknowledged by {@link ProtocolAdapterOutput2#stopped()} or
     * {@link ProtocolAdapterOutput2#error(com.hivemq.adapter.sdk.api2.model.ErrorScope, String)} with
     * scope {@code ADAPTER}.
     */
    void stop();

    /**
     * Connect to the device. Acknowledged by {@link ProtocolAdapterOutput2#connected()},
     * {@link ProtocolAdapterOutput2#error(com.hivemq.adapter.sdk.api2.model.ErrorScope, String)} with
     * scope {@code CONNECTION}, or {@link ProtocolAdapterOutput2#disconnected()}.
     */
    void connect();

    /**
     * Disconnect from the device. Acknowledged by {@link ProtocolAdapterOutput2#disconnected()}.
     */
    void disconnect();

    /**
     * Verify the given nodes against the connected device. Each node is acknowledged by one
     * {@link ProtocolAdapterOutput2#verifyResult(Node, com.hivemq.adapter.sdk.api2.model.VerifyOutcome)}.
     *
     * @param nodes the nodes to verify.
     */
    void verifyBatch(@NotNull List<Node> nodes);

    /**
     * Poll the current values of the given nodes. Each node is answered by one
     * {@link ProtocolAdapterOutput2#dataPoint(Node, com.hivemq.adapter.sdk.api.data.DataPoint)} or one
     * {@link ProtocolAdapterOutput2#nodeError(Node, String, boolean)}.
     *
     * @param nodes the nodes to poll.
     */
    void pollBatch(@NotNull List<Node> nodes);

    /**
     * Subscribe to value changes of the given nodes. Pushed values arrive as
     * {@link ProtocolAdapterOutput2#dataPoint(Node, com.hivemq.adapter.sdk.api.data.DataPoint)}; a failed or
     * lost subscription is reported as {@link ProtocolAdapterOutput2#nodeError(Node, String, boolean)}.
     *
     * @param nodes the nodes to subscribe to.
     */
    void addSubscriptionBatch(@NotNull List<Node> nodes);

    /**
     * Remove the subscriptions of the given nodes.
     *
     * @param nodes the nodes to unsubscribe from.
     */
    void removeSubscriptionBatch(@NotNull List<Node> nodes);

    /**
     * Write the given values southbound. Each entry is acknowledged by one
     * {@link ProtocolAdapterOutput2#writeResult(Node, boolean, String)}.
     *
     * @param entries the node/value pairs to write.
     */
    void writeBatch(@NotNull List<WriteEntry> entries);

    /**
     * Enumerate the device's address space below the filter node. Answered by one
     * {@link ProtocolAdapterOutput2#browseResult(List)}.
     *
     * @param filter the filter selecting where to browse.
     */
    void browse(@NotNull BrowseFilter filter);
}
