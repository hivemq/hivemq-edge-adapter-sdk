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
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterOutput2;
import com.hivemq.adapter.sdk.api2.model.BrowseFilter;
import com.hivemq.adapter.sdk.api2.model.VerifyOutcome;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2;
import com.hivemq.adapter.sdk.api2.node.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The optional defaults: {@code doBrowse} answers an empty browse result, {@code doVerifyNode} reports
 * {@link VerifyOutcome.Success}, and {@code doRemoveSubscription} is a no-op — an adapter implementing only
 * the abstract methods still satisfies the full command contract.
 */
class DefaultNoOpTest {

    private static final class MinimalAdapter extends AbstractProtocolAdapter2 {

        MinimalAdapter(
                final @NotNull ProtocolAdapterInput2 input, final @NotNull ProtocolAdapterOutput2 reporter) {
            super(input, reporter);
        }

        @Override
        protected void doStart() {
        }

        @Override
        protected void doStop() {
        }

        @Override
        protected void doConnect() {
        }

        @Override
        protected void doDisconnect() {
        }

        @Override
        protected void doPoll(final @NotNull Node node) {
        }

        @Override
        protected void doAddSubscription(final @NotNull Node node) {
        }

        @Override
        protected void doWrite(final @NotNull Node node, final @NotNull DataPoint value) {
        }
    }

    @Test
    void browse_answersAnEmptyBrowseResult() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput2 callbacks = new RecordingProtocolAdapterOutput2();
        final MinimalAdapter adapter =
                new MinimalAdapter(TestProtocolAdapterInput.create("adapter-1", dispatcher), callbacks);

        adapter.browse(new BrowseFilter(new TestNode("root")));

        dispatcher.drainAll();
        assertThat(callbacks.invocations()).containsExactly("browseResult");
        assertThat(callbacks.browseResults()).containsExactly(List.of());
    }

    @Test
    void verifyBatch_reportsSuccessPerNode() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput2 callbacks = new RecordingProtocolAdapterOutput2();
        final MinimalAdapter adapter =
                new MinimalAdapter(TestProtocolAdapterInput.create("adapter-1", dispatcher), callbacks);

        adapter.verifyBatch(List.of(new TestNode("node-a"), new TestNode("node-b")));

        dispatcher.drainAll();
        assertThat(callbacks.invocations()).containsExactly("verifyResult:node-a", "verifyResult:node-b");
        assertThat(callbacks.verifyOutcomes()).hasSize(2).allSatisfy(
                outcome -> assertThat(outcome).isInstanceOf(VerifyOutcome.Success.class));
    }

    @Test
    void removeSubscriptionBatch_isANoOperation() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput2 callbacks = new RecordingProtocolAdapterOutput2();
        final MinimalAdapter adapter =
                new MinimalAdapter(TestProtocolAdapterInput.create("adapter-1", dispatcher), callbacks);

        adapter.removeSubscriptionBatch(List.of(new TestNode("node-a")));

        dispatcher.drainAll();
        assertThat(callbacks.invocations()).isEmpty();
    }
}
