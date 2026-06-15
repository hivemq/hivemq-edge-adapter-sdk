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
package com.hivemq.adapter.sdk.api2.template;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api2.ProtocolAdapter2;
import com.hivemq.adapter.sdk.api2.command.BrowseFilter;
import com.hivemq.adapter.sdk.api2.command.VerifyOutcome;
import com.hivemq.adapter.sdk.api2.command.WriteEntry;
import com.hivemq.adapter.sdk.api2.messaging.DefaultMailbox;
import com.hivemq.adapter.sdk.api2.messaging.Mailbox;
import com.hivemq.adapter.sdk.api2.messaging.MessageHandler;
import com.hivemq.adapter.sdk.api2.messaging.ProtocolAdapterCommand;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterOutput2;
import com.hivemq.adapter.sdk.api2.node.Node;
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
 * bound the damage; a queued {@link ProtocolAdapterCommand.Stop} or {@link ProtocolAdapterCommand.Disconnect},
 * being a {@code CONTROL}-band message, is delivered ahead of queued batch work — but nothing can preempt an
 * in-flight {@code do*}. A long {@link #doBrowse(BrowseFilter)} walk starves polls for its whole duration on
 * the template's single thread: adapters with large address spaces should implement browse asynchronously
 * inside the adapter (issue the walk on library threads, report
 * {@link ProtocolAdapterOutput2#browseResult(List)} via the thread-safe output).
 * <p>
 * An author who needs a different threading model does not use this template: implement
 * {@link ProtocolAdapter2} directly, or supply a different
 * {@link com.hivemq.adapter.sdk.api2.messaging.MessageDispatcher} via
 * {@link com.hivemq.adapter.sdk.api2.services.ProtocolAdapterService#dispatcher()}.
 */
public abstract class AbstractProtocolAdapter2 implements ProtocolAdapter2, MessageHandler<ProtocolAdapterCommand> {

    /**
     * The adapter's output to the framework — its state-and-event tell-façade. Thread-safe: callable from any
     * thread, including library threads.
     */
    protected final @NotNull ProtocolAdapterOutput2 output;

    /**
     * The reused v1 factory adapter code builds its values with.
     */
    protected final @NotNull DataPointFactory dataPointFactory;

    private final @NotNull String adapterId;
    private final @NotNull Mailbox<ProtocolAdapterCommand> mailbox;

    /**
     * Creates the adapter's mailbox and attaches it, with the adapter as the message handler, to the
     * framework-supplied dispatcher. Construction is synchronous and cheap: no I/O, no connection.
     *
     * @param input         everything this adapter instance is constructed from.
     * @param output the framework's state-and-event reporter.
     */
    protected AbstractProtocolAdapter2(
            final @NotNull ProtocolAdapterInput2 input, final @NotNull ProtocolAdapterOutput2 output) {
        this.output = output;
        this.dataPointFactory = input.services().dataPointFactory();
        this.adapterId = input.adapterId();
        this.mailbox = new DefaultMailbox<>();
        input.services().dispatcher().attach(mailbox, this);
    }

    @Override
    public final @NotNull String adapterId() {
        return adapterId;
    }

    // ── ProtocolAdapter2: every command is one thread-safe tell ──────────────────────────────────

    @Override
    public final void start() {
        mailbox.tell(new ProtocolAdapterCommand.Start());
    }

    @Override
    public final void stop() {
        mailbox.tell(new ProtocolAdapterCommand.Stop());
    }

    @Override
    public final void connect() {
        mailbox.tell(new ProtocolAdapterCommand.Connect());
    }

    @Override
    public final void disconnect() {
        mailbox.tell(new ProtocolAdapterCommand.Disconnect());
    }

    @Override
    public final void verifyBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterCommand.VerifyBatch(nodes));
    }

    @Override
    public final void pollBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterCommand.PollBatch(nodes));
    }

    @Override
    public final void addSubscriptionBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterCommand.AddSubscriptionBatch(nodes));
    }

    @Override
    public final void removeSubscriptionBatch(final @NotNull List<Node> nodes) {
        mailbox.tell(new ProtocolAdapterCommand.RemoveSubscriptionBatch(nodes));
    }

    @Override
    public final void writeBatch(final @NotNull List<WriteEntry> entries) {
        mailbox.tell(new ProtocolAdapterCommand.WriteBatch(entries));
    }

    @Override
    public final void browse(final @NotNull BrowseFilter filter) {
        mailbox.tell(new ProtocolAdapterCommand.Browse(filter));
    }

    // ── MessageHandler: one message at a time on the dispatch thread ─────────────────────────────

    @Override
    public final void receive(final @NotNull ProtocolAdapterCommand command) {
        switch (command) {
            case ProtocolAdapterCommand.Start start -> doStart();
            case ProtocolAdapterCommand.Stop stop -> doStop();
            case ProtocolAdapterCommand.Connect connect -> doConnect();
            case ProtocolAdapterCommand.Disconnect disconnect -> doDisconnect();
            case ProtocolAdapterCommand.VerifyBatch verifyBatch -> doVerifyBatch(verifyBatch.nodes());
            case ProtocolAdapterCommand.PollBatch pollBatch -> doPollBatch(pollBatch.nodes());
            case ProtocolAdapterCommand.AddSubscriptionBatch addSubscriptionBatch ->
                    doAddSubscriptionBatch(addSubscriptionBatch.nodes());
            case ProtocolAdapterCommand.RemoveSubscriptionBatch removeSubscriptionBatch ->
                    doRemoveSubscriptionBatch(removeSubscriptionBatch.nodes());
            case ProtocolAdapterCommand.WriteBatch writeBatch -> doWriteBatch(writeBatch.entries());
            case ProtocolAdapterCommand.Browse browse -> doBrowse(browse.filter());
        }
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
     * Default: answers an empty browse result — for protocols without an enumerable address space. See the
     * class Javadoc before implementing a long synchronous walk here.
     *
     * @param filter the filter selecting where to browse.
     */
    protected void doBrowse(final @NotNull BrowseFilter filter) {
        output.browseResult(List.of());
    }

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
     * Allocate resources; no connection yet. Acknowledge with {@link ProtocolAdapterOutput2#started()}.
     */
    protected abstract void doStart();

    /**
     * Release resources. Acknowledge with {@link ProtocolAdapterOutput2#stopped()}.
     */
    protected abstract void doStop();

    /**
     * Connect to the device. Acknowledge with {@link ProtocolAdapterOutput2#connected()}.
     */
    protected abstract void doConnect();

    /**
     * Disconnect from the device. Acknowledge with {@link ProtocolAdapterOutput2#disconnected()}.
     */
    protected abstract void doDisconnect();

    /**
     * Read the node's current value and report it with
     * {@link ProtocolAdapterOutput2#dataPoint(Node, DataPoint)} — build the value with
     * {@link #dataPointFactory}; the framework stamps the tag name and adapter identifier.
     *
     * @param node the node to poll.
     */
    protected abstract void doPoll(@NotNull Node node);

    /**
     * Subscribe to the node's value changes; report pushed values with
     * {@link ProtocolAdapterOutput2#dataPoint(Node, DataPoint)}.
     *
     * @param node the node to subscribe to.
     */
    protected abstract void doAddSubscription(@NotNull Node node);

    /**
     * Write the value to the node and acknowledge with
     * {@link ProtocolAdapterOutput2#writeResult(Node, boolean, String)}.
     *
     * @param node  the node to write to.
     * @param value the value to write.
     */
    protected abstract void doWrite(@NotNull Node node, @NotNull DataPoint value);
}
