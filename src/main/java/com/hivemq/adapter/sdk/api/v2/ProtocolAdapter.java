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
package com.hivemq.adapter.sdk.api.v2;

import com.hivemq.adapter.sdk.api.v2.model.BrowseContinuation;
import com.hivemq.adapter.sdk.api.v2.model.BrowseFilter;
import com.hivemq.adapter.sdk.api.v2.model.BrowseResultEntry;
import com.hivemq.adapter.sdk.api.v2.model.WriteEntry;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The protocol adapter command interface — <b>pure mechanism</b>. The adapter only executes commands and
 * reports events through {@link ProtocolAdapterOutput}; it has ZERO retry, backoff, reconnect, or
 * scheduling logic — the controlling framework owns all policy.
 * <p>
 * Every command is asynchronous and acknowledged by a callback event. Batch variants are always what the
 * framework calls; how an implementation decomposes a batch is its own concern.
 * <p>
 * <b>Long-running commands.</b> An implementation may block its own dispatch thread (a blocking
 * {@code connect()} against a protocol library is normal); queued commands simply wait, and the framework's
 * watchdogs bound the damage. Browse does <b>not</b> walk a whole address space in one command: it is
 * <b>paginated</b> ({@link #browse(int, BrowseFilter, int)} / {@link #browseNext(int, BrowseContinuation)}),
 * so each step is a single round-trip and lifecycle ({@code CONTROL}) commands and polls interleave between
 * pages — a large address space never starves them.
 */
public interface ProtocolAdapter {

    /**
     * @return the identifier of this adapter instance, unique within this Edge instance.
     */
    @NotNull String adapterId();

    /**
     * Start the adapter (allocate resources; no connection yet). Acknowledged by
     * {@link ProtocolAdapterOutput#started()} or
     * {@link ProtocolAdapterOutput#error(com.hivemq.adapter.sdk.api.v2.model.ErrorScope, String)} with
     * scope {@code ADAPTER}.
     */
    void start();

    /**
     * Stop the adapter (release resources). <b>Always</b> acknowledged by
     * {@link ProtocolAdapterOutput#stopped()} — and only by {@code stopped()}: the framework has already
     * decided to stop, so a partial teardown failure does not change the outcome. Log it and acknowledge
     * {@code stopped()} anyway. {@code stop()} never reports
     * {@link ProtocolAdapterOutput#error(com.hivemq.adapter.sdk.api.v2.model.ErrorScope, String)}.
     */
    void stop();

    /**
     * Connect to the device. Acknowledged by {@link ProtocolAdapterOutput#connected()},
     * {@link ProtocolAdapterOutput#error(com.hivemq.adapter.sdk.api.v2.model.ErrorScope, String)} with
     * scope {@code CONNECTION}, or {@link ProtocolAdapterOutput#disconnected()}.
     */
    void connect();

    /**
     * Disconnect from the device. Acknowledged by {@link ProtocolAdapterOutput#disconnected()}.
     */
    void disconnect();

    /**
     * Verify the given nodes against the connected device. Each node is acknowledged by one
     * {@link ProtocolAdapterOutput#verifyResult(Node, com.hivemq.adapter.sdk.api.v2.model.VerifyOutcome)}.
     *
     * @param nodes the nodes to verify.
     */
    void verifyBatch(@NotNull List<Node> nodes);

    /**
     * Poll the current values of the given nodes. Each node is answered by one
     * {@link ProtocolAdapterOutput#dataPoint(Node, com.hivemq.adapter.sdk.api.data.DataPoint)} or one
     * {@link ProtocolAdapterOutput#nodeError(Node, String, boolean)}.
     *
     * @param nodes the nodes to poll.
     */
    void pollBatch(@NotNull List<Node> nodes);

    /**
     * Subscribe to value changes of the given nodes. Pushed values arrive as
     * {@link ProtocolAdapterOutput#dataPoint(Node, com.hivemq.adapter.sdk.api.data.DataPoint)}; a failed or
     * lost subscription is reported as {@link ProtocolAdapterOutput#nodeError(Node, String, boolean)}.
     * <p>
     * The semantics are <b>incremental add</b>: the adapter maintains a shadow set of currently subscribed
     * nodes and each call <i>adds</i> to it. The framework may call this multiple times; an implementation
     * must <b>not</b> reset or replace existing subscriptions — only the given nodes are affected.
     *
     * @param nodes the nodes to subscribe to.
     */
    void addSubscriptionBatch(@NotNull List<Node> nodes);

    /**
     * Remove the subscriptions of the given nodes — <b>fire and forget</b>: no acknowledgment is expected.
     * The adapter drops the given nodes from its shadow set and tears down their protocol-level
     * subscriptions; a node that is not currently subscribed is ignored silently.
     *
     * @param nodes the nodes to unsubscribe from.
     */
    void removeSubscriptionBatch(@NotNull List<Node> nodes);

    /**
     * Write the given values southbound. Each entry is acknowledged by one
     * {@link ProtocolAdapterOutput#writeResult(Node, boolean, String)}.
     *
     * @param entries the node/value pairs to write.
     */
    void writeBatch(@NotNull List<WriteEntry> entries);

    /**
     * Begin enumerating the device's address space below the filter node — <b>one page at a time</b>. Answered
     * by one {@link ProtocolAdapterOutput#browsePage(int, List, BrowseContinuation)} or
     * {@link ProtocolAdapterOutput#browseError(int, String)}; if that page carries a non-null
     * {@link BrowseContinuation}, the framework fetches the next page with
     * {@link #browseNext(int, BrowseContinuation)}. Pagination keeps each step a single round-trip, so a large
     * address space never starves lifecycle commands or polls.
     *
     * @param requestId     correlates this browse's pages and errors.
     * @param filter        the filter selecting where to browse.
     * @param maxReferences max entries per page; {@code 0} lets the device decide, {@code >0} forces pagination.
     */
    void browse(int requestId, @NotNull BrowseFilter filter, int maxReferences);

    /**
     * Fetch the next page of an in-progress browse. Answered by one
     * {@link ProtocolAdapterOutput#browsePage(int, List, BrowseContinuation)} (continuation {@code null} = last
     * page) or {@link ProtocolAdapterOutput#browseError(int, String)}.
     *
     * @param requestId    the browse these pages belong to.
     * @param continuation the opaque token from the previous page.
     */
    void browseNext(int requestId, @NotNull BrowseContinuation continuation);

    /**
     * Resolve the device attributes (datatype, access, description) of the given nodes — the RESOLVE half of a
     * browse. {@link #browse(int, BrowseFilter, int)} DISCOVERs which nodes exist; this reads their attributes so
     * the framework can build typed tag definitions. Like browse it is <b>pure mechanism</b>: one server
     * round-trip per call (the framework batches the discovered variables), answered by a single
     * {@link ProtocolAdapterOutput#readAttributesResult(int, List)} carrying one
     * {@link com.hivemq.adapter.sdk.api.v2.model.ResolvedAttributes} per node, or by
     * {@link ProtocolAdapterOutput#browseError(int, String)} on failure. Correlated to its browse by
     * {@code requestId}.
     *
     * @param requestId correlates this resolve with the browse that discovered the nodes.
     * @param nodes     the discovered nodes whose attributes to resolve.
     */
    void readNodeAttributes(int requestId, @NotNull List<Node> nodes);
}
