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

import com.hivemq.adapter.sdk.api2.actor.Actor;
import com.hivemq.adapter.sdk.api2.actor.Message;
import com.hivemq.adapter.sdk.api2.actor.MessagePriority;
import com.hivemq.adapter.sdk.api2.command.BrowseFilter;
import com.hivemq.adapter.sdk.api2.command.WriteEntry;
import com.hivemq.adapter.sdk.api2.node.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Compile-only contract smoke: a no-op {@link ProtocolAdapter2} and a no-op {@link Actor} compile against the
 * SDK alone — the executable proof that the author-facing contracts are self-contained.
 */
class ContractCompilationSmokeTest {

    private static final class NoOperationProtocolAdapter implements ProtocolAdapter2 {
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
        public void browse(final @NotNull BrowseFilter filter) {
        }
    }

    private record TestMessage() implements Message {
    }

    private static final class NoOperationActor implements Actor<TestMessage> {
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
    void noOperationActor_compilesAndReceivesTheDefaultBandMessage() {
        final NoOperationActor actor = new NoOperationActor();
        final TestMessage message = new TestMessage();
        assertThat(message.priority()).isEqualTo(MessagePriority.EVENT);
        assertThatCode(() -> actor.receive(message)).doesNotThrowAnyException();
    }
}
