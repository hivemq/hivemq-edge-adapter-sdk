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
import com.hivemq.adapter.sdk.api.v2.model.WriteEntry;
import com.hivemq.adapter.sdk.api.v2.messaging.MailboxMessage;
import com.hivemq.adapter.sdk.api.v2.messaging.MailboxMessagePriority;
import com.hivemq.adapter.sdk.api.v2.messaging.MessageHandler;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Compile-only contract smoke: a no-op {@link ProtocolAdapter} and a no-op {@link MessageHandler} compile
 * against the SDK alone — the executable proof that the author-facing contracts are self-contained.
 */
class ContractCompilationSmokeTest {

    private static final class NoOperationProtocolAdapter implements ProtocolAdapter {
        @Override
        public @NotNull String adapterId() {
            return "no-operation";
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void connect() {
        }

        @Override
        public void disconnect() {
        }

        @Override
        public void verifyBatch(final @NotNull List<Node> nodes) {
        }

        @Override
        public void pollBatch(final @NotNull List<Node> nodes) {
        }

        @Override
        public void addSubscriptionBatch(final @NotNull List<Node> nodes) {
        }

        @Override
        public void removeSubscriptionBatch(final @NotNull List<Node> nodes) {
        }

        @Override
        public void writeBatch(final @NotNull List<WriteEntry> entries) {
        }

        @Override
        public void browse(final int requestId, final @NotNull BrowseFilter filter, final int maxReferences) {
        }

        @Override
        public void browseNext(final int requestId, final @NotNull BrowseContinuation continuation) {
        }

        @Override
        public void readNodeAttributes(final int requestId, final @NotNull List<Node> nodes) {
        }
    }

    private record TestMessage() implements MailboxMessage {
    }

    private static final class NoOperationMessageHandler implements MessageHandler<TestMessage> {
        @Override
        public void receive(final @NotNull TestMessage message) {
        }
    }

    @Test
    void noOperationProtocolAdapter_compilesAndAnswersItsIdentifier() {
        final NoOperationProtocolAdapter protocolAdapter = new NoOperationProtocolAdapter();
        assertThat(protocolAdapter.adapterId()).isEqualTo("no-operation");
        assertThatCode(() -> {
            protocolAdapter.start();
            protocolAdapter.connect();
            protocolAdapter.pollBatch(List.of());
            protocolAdapter.disconnect();
            protocolAdapter.stop();
        }).doesNotThrowAnyException();
    }

    @Test
    void noOperationMessageHandler_compilesAndReceivesTheDefaultBandMessage() {
        final NoOperationMessageHandler handler = new NoOperationMessageHandler();
        final TestMessage message = new TestMessage();
        assertThat(message.priority()).isEqualTo(MailboxMessagePriority.EVENT);
        assertThatCode(() -> handler.receive(message)).doesNotThrowAnyException();
    }
}
