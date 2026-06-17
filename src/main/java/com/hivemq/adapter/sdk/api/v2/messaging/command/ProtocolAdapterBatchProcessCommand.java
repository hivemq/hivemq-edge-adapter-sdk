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
package com.hivemq.adapter.sdk.api.v2.messaging.command;

import com.hivemq.adapter.sdk.api.v2.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.v2.messaging.MailboxMessagePriority;
import com.hivemq.adapter.sdk.api.v2.model.BrowseFilter;
import com.hivemq.adapter.sdk.api.v2.model.WriteEntry;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The batch and browse commands — delivered in the {@link MailboxMessagePriority#DATA} band, so bulk work
 * yields to lifecycle. Sealed over its six immutable record commands (list components are defensively copied).
 */
public sealed interface ProtocolAdapterBatchProcessCommand extends ProtocolAdapterCommand {

    @Override
    default @NotNull MailboxMessagePriority priority() {
        return MailboxMessagePriority.DATA;
    }

    /**
     * Carries {@link ProtocolAdapter#verifyBatch(List)}.
     *
     * @param nodes the nodes to verify.
     */
    record VerifyBatch(@NotNull List<Node> nodes) implements ProtocolAdapterBatchProcessCommand {
        public VerifyBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter#pollBatch(List)}.
     *
     * @param nodes the nodes to poll.
     */
    record PollBatch(@NotNull List<Node> nodes) implements ProtocolAdapterBatchProcessCommand {
        public PollBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter#addSubscriptionBatch(List)}.
     *
     * @param nodes the nodes to subscribe to.
     */
    record AddSubscriptionBatch(@NotNull List<Node> nodes) implements ProtocolAdapterBatchProcessCommand {
        public AddSubscriptionBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter#removeSubscriptionBatch(List)}.
     *
     * @param nodes the nodes to unsubscribe from.
     */
    record RemoveSubscriptionBatch(@NotNull List<Node> nodes) implements ProtocolAdapterBatchProcessCommand {
        public RemoveSubscriptionBatch {
            nodes = List.copyOf(nodes);
        }
    }

    /**
     * Carries {@link ProtocolAdapter#writeBatch(List)}.
     *
     * @param entries the node/value pairs to write.
     */
    record WriteBatch(@NotNull List<WriteEntry> entries) implements ProtocolAdapterBatchProcessCommand {
        public WriteBatch {
            entries = List.copyOf(entries);
        }
    }

    /**
     * Carries {@link ProtocolAdapter#browse(BrowseFilter)}.
     *
     * @param filter the filter selecting where to browse.
     */
    record Browse(@NotNull BrowseFilter filter) implements ProtocolAdapterBatchProcessCommand {
    }
}
