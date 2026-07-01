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
package com.hivemq.adapter.sdk.api.v2.template;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api.v2.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.v2.messaging.DefaultMailbox;
import com.hivemq.adapter.sdk.api.v2.messaging.Mailbox;
import com.hivemq.adapter.sdk.api.v2.messaging.MessageDispatcherHandle;
import com.hivemq.adapter.sdk.api.v2.messaging.MessageHandler;
import com.hivemq.adapter.sdk.api.v2.messaging.command.ProtocolAdapterBatchProcessCommand;
import com.hivemq.adapter.sdk.api.v2.messaging.command.ProtocolAdapterCommand;
import com.hivemq.adapter.sdk.api.v2.messaging.command.ProtocolAdapterConnectionCommand;
import com.hivemq.adapter.sdk.api.v2.model.BrowseContinuation;
import com.hivemq.adapter.sdk.api.v2.model.BrowseFilter;
import com.hivemq.adapter.sdk.api.v2.model.ErrorScope;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterInput;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput;
import com.hivemq.adapter.sdk.api.v2.model.VerifyOutcome;
import com.hivemq.adapter.sdk.api.v2.model.WriteEntry;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The protocol adapter template — the adapter as a single-threaded message handler. The {@code final} command
 * methods {@code tell} an immutable {@link ProtocolAdapterCommand} onto the adapter's own
 * {@link DefaultMailbox}; {@link #receive(ProtocolAdapterCommand)} switches the sealed command set into
 * {@code do*} methods on the adapter's single dispatch thread. An author implements only the abstract
 * {@code do*} methods and never thinks about threads: every {@code do*} runs on the one dispatch thread, never
 * concurrently.
 * <p>
 * <b>Blocking.</b> A {@code do*} implementation may block the dispatch thread — a blocking {@code connect()}
 * against a protocol library is normal. Queued commands simply wait behind it, and the framework's watchdogs
 * bound the damage; a queued {@link ProtocolAdapterConnectionCommand.Stop} or
 * {@link ProtocolAdapterConnectionCommand.Disconnect}, being a {@code CONTROL}-band message, is delivered ahead
 * of queued batch work — but nothing can preempt an in-flight {@code do*}. Browse is paginated
 * ({@link #doBrowse(int, BrowseFilter, int)} / {@link #doBrowseNext(int, BrowseContinuation)}): each call
 * returns a single page via {@link ProtocolAdapterOutput#browsePage(int, List, BrowseContinuation)}, so a large
 * address space yields the dispatch thread between pages and never starves polls or {@code CONTROL} commands. The
 * RESOLVE step ({@link #doReadNodeAttributes(int, List)}) reads the discovered nodes' attributes in the same
 * {@code DATA} band, one batched round-trip at a time.
 * <p>
 * An author who needs a different threading model does not use this template: implement
 * {@link ProtocolAdapter} directly, or supply a different
 * {@link com.hivemq.adapter.sdk.api.v2.messaging.MessageDispatcher} via
 * {@link com.hivemq.adapter.sdk.api.v2.services.ProtocolAdapterService#dispatcher()}.
 * <p>
 * <b>Teardown.</b> The adapter owns one long-lived dispatch thread, attached at construction. It is
 * {@link AutoCloseable} so the framework can release that thread when the adapter instance is discarded — on
 * removal, a full recreate, or subsystem shutdown. {@link #close()} is the framework's teardown seam, distinct from
 * {@link #stop()}: a stopped adapter may be started again, so the dispatch thread is detached only by {@code close()},
 * never by a plain {@code stop()}. An author releases protocol-library resources in {@link #doStop()} and never calls
 * {@code close()}.
 */
public abstract class AbstractProtocolAdapter
        implements ProtocolAdapter, MessageHandler<ProtocolAdapterCommand>, AutoCloseable {

    /**
     * The adapter's output to the framework — its state-and-event tell-façade. Thread-safe: callable from any
     * thread, including library threads.
     */
    protected final @NotNull ProtocolAdapterOutput output;

    /**
     * The reused v1 factory adapter code builds its values with.
     */
    protected final @NotNull DataPointFactory dataPointFactory;

    private final @NotNull String adapterId;
    private final @NotNull Mailbox<ProtocolAdapterCommand> mailbox;
    private final @NotNull MessageDispatcherHandle dispatcherHandle;

    /**
     * Creates the adapter's mailbox and attaches it, with the adapter as the message handler, to the
     * framework-supplied dispatcher, retaining the binding handle so the framework can detach the dispatch thread on
     * teardown. Construction is synchronous and cheap: no I/O, no connection.
     *
     * @param input         everything this adapter instance is constructed from.
     * @param output the framework's state-and-event reporter.
     */
    protected AbstractProtocolAdapter(
            final @NotNull ProtocolAdapterInput input, final @NotNull ProtocolAdapterOutput output) {
        this.output = output;
        this.dataPointFactory = input.services().dataPointFactory();
        this.adapterId = input.adapterId();
        this.mailbox = new DefaultMailbox<>();
        this.dispatcherHandle = input.services().dispatcher().attach(mailbox, this);
    }

    @Override
    public final @NotNull String adapterId() {
        return adapterId;
    }

    /**
     * Detach this adapter's dispatch thread from the framework dispatcher. The framework calls this once, only when
     * the adapter instance is discarded (removal, full recreate, or subsystem shutdown) — never on a plain
     * {@link #stop()}, since a stopped adapter may be started again. Idempotent; an in-flight {@code do*} completes
     * first.
     */
    @Override
    public final void close() {
        dispatcherHandle.close();
    }

    // ── ProtocolAdapter: every command is one thread-safe tell ──────────────────────────────────

    @Override
    public final void start() {
        mailbox.tell(new ProtocolAdapterConnectionCommand.Start());
    }

    @Override
    public final void stop() {
        mailbox.tell(new ProtocolAdapterConnectionCommand.Stop());
    }

    @Override
    public final void connect() {
        mailbox.tell(new ProtocolAdapterConnectionCommand.Connect());
    }

    @Override
    public final void disconnect() {
        mailbox.tell(new ProtocolAdapterConnectionCommand.Disconnect());
    }

    @Override
    public final void verifyBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.VerifyBatch(nodes));
    }

    @Override
    public final void pollBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.PollBatch(nodes));
    }

    @Override
    public final void addSubscriptionBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.AddSubscriptionBatch(nodes));
    }

    @Override
    public final void removeSubscriptionBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.RemoveSubscriptionBatch(nodes));
    }

    @Override
    public final void writeBatch(final @NotNull List<WriteEntry> entries) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.WriteBatch(entries));
    }

    @Override
    public final void browse(final int requestId, final @NotNull BrowseFilter filter, final int maxReferences) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.Browse(requestId, filter, maxReferences));
    }

    @Override
    public final void browseNext(final int requestId, final @NotNull BrowseContinuation continuation) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.BrowseNext(requestId, continuation));
    }

    @Override
    public final void readNodeAttributes(final int requestId, final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.ReadNodeAttributes(requestId, nodes));
    }

    @Override
    public final void browseCancel(final int requestId) {
        mailbox.tell(new ProtocolAdapterBatchProcessCommand.BrowseCancel(requestId));
    }

    // ── MessageHandler: one message at a time on the dispatch thread ─────────────────────────────

    @Override
    public final void receive(final @NotNull ProtocolAdapterCommand command) {
        try {
            dispatch(command);
        } catch (final RuntimeException failure) {
            reportCommandFailure(command, failure);
        }
    }

    private void dispatch(final @NotNull ProtocolAdapterCommand command) {
        switch (command) {
            case ProtocolAdapterConnectionCommand.Start start -> doStart();
            case ProtocolAdapterConnectionCommand.Stop stop -> doStop();
            case ProtocolAdapterConnectionCommand.Connect connect -> doConnect();
            case ProtocolAdapterConnectionCommand.Disconnect disconnect -> doDisconnect();
            case ProtocolAdapterBatchProcessCommand.VerifyBatch verifyBatch -> doVerifyBatch(verifyBatch.nodes());
            case ProtocolAdapterBatchProcessCommand.PollBatch pollBatch -> doPollBatch(pollBatch.nodes());
            case ProtocolAdapterBatchProcessCommand.AddSubscriptionBatch addSubscriptionBatch ->
                    doAddSubscriptionBatch(addSubscriptionBatch.nodes());
            case ProtocolAdapterBatchProcessCommand.RemoveSubscriptionBatch removeSubscriptionBatch ->
                    doRemoveSubscriptionBatch(removeSubscriptionBatch.nodes());
            case ProtocolAdapterBatchProcessCommand.WriteBatch writeBatch -> doWriteBatch(writeBatch.entries());
            case ProtocolAdapterBatchProcessCommand.Browse browse ->
                    doBrowse(browse.requestId(), browse.filter(), browse.maxReferences());
            case ProtocolAdapterBatchProcessCommand.BrowseNext browseNext ->
                    doBrowseNext(browseNext.requestId(), browseNext.continuation());
            case ProtocolAdapterBatchProcessCommand.ReadNodeAttributes readNodeAttributes ->
                    doReadNodeAttributes(readNodeAttributes.requestId(), readNodeAttributes.nodes());
            case ProtocolAdapterBatchProcessCommand.BrowseCancel browseCancel ->
                    doBrowseCancel(browseCancel.requestId());
        }
    }

    /**
     * A {@code do*} method threw an unchecked exception: surface it to the framework instead of letting it escape the
     * dispatch thread (where it would terminate the actor). A failed {@code doStop()} still acknowledges
     * {@link ProtocolAdapterOutput#stopped()} — the framework has decided to stop, and a partial teardown failure does
     * not change that — while every other command reports an {@link ErrorScope#ADAPTER} error, which the framework
     * turns into the wrapper's {@code ERROR} state with a clear reason.
     *
     * @param command the command whose {@code do*} method failed.
     * @param failure the unchecked exception it threw.
     */
    private void reportCommandFailure(
            final @NotNull ProtocolAdapterCommand command, final @NotNull RuntimeException failure) {
        if (command instanceof ProtocolAdapterConnectionCommand.Stop) {
            output.stopped();
            return;
        }
        output.error(
                ErrorScope.ADAPTER,
                "adapter [" + adapterId + "] command " + command.getClass().getSimpleName() + " failed: " + failure);
    }

    // ── Default batch fallbacks: loop the single-node methods; a native override wins ────────────

    /**
     * Default: one {@link #doVerifyNode(Node)} per node. Override for a native batch verification.
     *
     * @param nodes the nodes to verify.
     */
    protected void doVerifyBatch(final @NotNull List<Node> nodes) {
        nodes.forEach(this::doVerifyNode);
    }

    /**
     * Default: one {@link #doPoll(Node)} per node. Override for a native batch read.
     *
     * @param nodes the nodes to poll.
     */
    protected void doPollBatch(final @NotNull List<Node> nodes) {
        nodes.forEach(this::doPoll);
    }

    /**
     * Default: one {@link #doAddSubscription(Node)} per node. Override for a native batch subscription.
     *
     * @param nodes the nodes to subscribe to.
     */
    protected void doAddSubscriptionBatch(final @NotNull List<Node> nodes) {
        nodes.forEach(this::doAddSubscription);
    }

    /**
     * Default: one {@link #doRemoveSubscription(Node)} per node. Override for a native batch removal.
     *
     * @param nodes the nodes to unsubscribe from.
     */
    protected void doRemoveSubscriptionBatch(final @NotNull List<Node> nodes) {
        nodes.forEach(this::doRemoveSubscription);
    }

    /**
     * Default: one {@link #doWrite(Node, DataPoint)} per entry. Override for a native batch write.
     *
     * @param entries the node/value pairs to write.
     */
    protected void doWriteBatch(final @NotNull List<WriteEntry> entries) {
        entries.forEach(entry -> doWrite(entry.node(), entry.value()));
    }

    // ── Optional no-op defaults ───────────────────────────────────────────────────────────────────

    /**
     * Default: answers a single empty last page — for protocols without an enumerable address space. Override
     * to enumerate the filter node's children, returning at most {@code maxReferences} entries and a non-null
     * {@link BrowseContinuation} when more remain.
     *
     * @param requestId     correlates this browse's pages.
     * @param filter        the filter selecting where to browse.
     * @param maxReferences max entries per page; {@code 0} lets the device decide, {@code >0} forces pagination.
     */
    protected void doBrowse(final int requestId, final @NotNull BrowseFilter filter, final int maxReferences) {
        output.browsePage(requestId, List.of(), null);
    }

    /**
     * Default: answers a single empty last page. Override alongside {@link #doBrowse(int, BrowseFilter, int)}
     * to resume from the given continuation.
     *
     * @param requestId    the browse this page belongs to.
     * @param continuation the opaque token from the previous page.
     */
    protected void doBrowseNext(final int requestId, final @NotNull BrowseContinuation continuation) {
        output.browsePage(requestId, List.of(), null);
    }

    /**
     * Default: answers an empty result — for protocols that cannot resolve node attributes. Override alongside
     * {@link #doBrowse(int, BrowseFilter, int)} to read each node's datatype, access, and description and report
     * them with {@link ProtocolAdapterOutput#readAttributesResult(int, List)}.
     *
     * @param requestId correlates this resolve with the browse that discovered the nodes.
     * @param nodes     the discovered nodes whose attributes to resolve.
     */
    protected void doReadNodeAttributes(final int requestId, final @NotNull List<Node> nodes) {
        output.readAttributesResult(requestId, List.of());
    }

    /**
     * Default: does nothing — correct for protocols that hold no per-browse server state. Override alongside
     * {@link #doBrowse(int, BrowseFilter, int)} to release a device-side resource an abandoned paginated walk
     * holds open (for OPC-UA, {@code ReleaseContinuationPoints}). No answer is expected.
     *
     * @param requestId the browse to abandon and release.
     */
    protected void doBrowseCancel(final int requestId) {}

    /**
     * Default: reports {@link VerifyOutcome.Success} — for protocols that cannot verify a node ahead of use.
     *
     * @param node the node to verify.
     */
    protected void doVerifyNode(final @NotNull Node node) {
        output.verifyResult(node, new VerifyOutcome.Success());
    }

    /**
     * Default: no-op — for protocols whose subscriptions vanish with the connection.
     *
     * @param node the node to unsubscribe from.
     */
    protected void doRemoveSubscription(final @NotNull Node node) {
    }

    // ── The methods an author MUST implement ──────────────────────────────────────────────────────

    /**
     * Allocate resources; no connection yet. Acknowledge with {@link ProtocolAdapterOutput#started()}.
     */
    protected abstract void doStart();

    /**
     * Release resources. Acknowledge with {@link ProtocolAdapterOutput#stopped()}.
     */
    protected abstract void doStop();

    /**
     * Connect to the device. Acknowledge with {@link ProtocolAdapterOutput#connected()}.
     */
    protected abstract void doConnect();

    /**
     * Disconnect from the device. Acknowledge with {@link ProtocolAdapterOutput#disconnected()}.
     */
    protected abstract void doDisconnect();

    /**
     * Read the node's current value and report it with
     * {@link ProtocolAdapterOutput#dataPoint(Node, DataPoint)} — build the value with
     * {@link #dataPointFactory}; the framework stamps the tag name and adapter identifier.
     *
     * @param node the node to poll.
     */
    protected abstract void doPoll(@NotNull Node node);

    /**
     * Subscribe to the node's value changes; report pushed values with
     * {@link ProtocolAdapterOutput#dataPoint(Node, DataPoint)}.
     *
     * @param node the node to subscribe to.
     */
    protected abstract void doAddSubscription(@NotNull Node node);

    /**
     * Write the value to the node and acknowledge with
     * {@link ProtocolAdapterOutput#writeResult(Node, boolean, String)}.
     *
     * @param node  the node to write to.
     * @param value the value to write.
     */
    protected abstract void doWrite(@NotNull Node node, @NotNull DataPoint value);
}
