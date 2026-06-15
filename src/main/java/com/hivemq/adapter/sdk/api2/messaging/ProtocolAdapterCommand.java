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
package com.hivemq.adapter.sdk.api2.messaging;

import com.hivemq.adapter.sdk.api2.ProtocolAdapter2;
import com.hivemq.adapter.sdk.api2.command.BrowseFilter;
import com.hivemq.adapter.sdk.api2.command.WriteEntry;
import com.hivemq.adapter.sdk.api2.node.Node;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The template adapter's mailbox messages — one per {@link ProtocolAdapter2} command. Sealed; immutable
 * records (list components are defensively copied).
 * <p>
 * <b>Bands.</b> {@link Lifecycle} commands are {@link MailboxMessagePriority#CONTROL} — a stop or disconnect is
 * never starved behind a queued batch backlog; {@link BatchWork} commands are {@link MailboxMessagePriority#DATA}
 * — bulk work yields to lifecycle. FIFO within each band preserves the framework's emit order. (Nothing can
 * preempt an in-flight {@code do*} — the bands order only what is still queued.)
 */
public sealed interface ProtocolAdapterCommand extends MailboxMessage {

    /**
     * The lifecycle commands — delivered in the {@link MailboxMessagePriority#CONTROL} band.
     */
    sealed interface Lifecycle extends ProtocolAdapterCommand {
        @Override
        default @NotNull MailboxMessagePriority priority() {
            return MailboxMessagePriority.CONTROL;
        }
    }

    /**
     * The batch and browse commands — delivered in the {@link MailboxMessagePriority#DATA} band.
     */
    sealed interface BatchWork extends ProtocolAdapterCommand {
        @Override
        default @NotNull MailboxMessagePriority priority() {
            return MailboxMessagePriority.DATA;
        }
    }

    /**
     * Carries {@link ProtocolAdapter2#start()}.
     */
    record Start() implements Lifecycle {
    }

    /**
     * Carries {@link ProtocolAdapter2#stop()}.
     */
    record Stop() implements Lifecycle {
    }

    /**
     * Carries {@link ProtocolAdapter2#connect()}.
     */
    record Connect() implements Lifecycle {
    }

    /**
     * Carries {@link ProtocolAdapter2#disconnect()}.
     */
    record Disconnect() implements Lifecycle {
    }

    /**
     * Carries {@link ProtocolAdapter2#verifyBatch(List)}.
     *
     * @param nodes the nodes to verify.
     */
    record VerifyBatch(@NotNull List<Node> nodes) implements BatchWork {
        public VerifyBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter2#pollBatch(List)}.
     *
     * @param nodes the nodes to poll.
     */
    record PollBatch(@NotNull List<Node> nodes) implements BatchWork {
        public PollBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter2#addSubscriptionBatch(List)}.
     *
     * @param nodes the nodes to subscribe to.
     */
    record AddSubscriptionBatch(@NotNull List<Node> nodes) implements BatchWork {
        public AddSubscriptionBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter2#removeSubscriptionBatch(List)}.
     *
     * @param nodes the nodes to unsubscribe from.
     */
    record RemoveSubscriptionBatch(@NotNull List<Node> nodes) implements BatchWork {
        public RemoveSubscriptionBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter2#writeBatch(List)}.
     *
     * @param entries the node/value pairs to write.
     */
    record WriteBatch(@NotNull List<WriteEntry> entries) implements BatchWork {
        public WriteBatch {
            entries = List.copyOf(entries);
        }
    }

    /**
     * Carries {@link ProtocolAdapter2#browse(BrowseFilter)}.
     *
     * @param filter the filter selecting where to browse.
     */
    record Browse(@NotNull BrowseFilter filter) implements BatchWork {
    }
}
