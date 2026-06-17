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
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput;
import com.hivemq.adapter.sdk.api.v2.model.WriteEntry;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterInput;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The default batch fallbacks loop the single-node {@code do*} methods in batch order; a native batch
 * override wins when provided.
 */
class DefaultBatchFallbackTest {

    private static class SingleNodeRecordingAdapter extends AbstractProtocolAdapter {

        final @NotNull List<String> executed = new ArrayList<>();

        SingleNodeRecordingAdapter(
                final @NotNull ProtocolAdapterInput input, final @NotNull ProtocolAdapterOutput reporter) {
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
            executed.add("doPoll:" + node.nodeId());
        }

        @Override
        protected void doAddSubscription(final @NotNull Node node) {
            executed.add("doAddSubscription:" + node.nodeId());
        }

        @Override
        protected void doWrite(final @NotNull Node node, final @NotNull DataPoint value) {
            executed.add("doWrite:" + node.nodeId() + ":" + value.getTagValue());
        }
    }

    private static final class NativeBatchAdapter extends SingleNodeRecordingAdapter {

        NativeBatchAdapter(
                final @NotNull ProtocolAdapterInput input, final @NotNull ProtocolAdapterOutput reporter) {
            super(input, reporter);
        }

        @Override
        protected void doPollBatch(final @NotNull List<Node> nodes) {
            executed.add("doPollBatch:native:" + nodes.size());
        }
    }

    @Test
    void pollBatch_fallsBackToOneDoPollPerNode_inBatchOrder() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final SingleNodeRecordingAdapter adapter = new SingleNodeRecordingAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput());

        adapter.pollBatch(List.of(new TestNode("node-a"), new TestNode("node-b")));

        dispatcher.drainAll();
        assertThat(adapter.executed).containsExactly("doPoll:node-a", "doPoll:node-b");
    }

    @Test
    void addSubscriptionBatch_fallsBackToOneDoAddSubscriptionPerNode() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final SingleNodeRecordingAdapter adapter = new SingleNodeRecordingAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput());

        adapter.addSubscriptionBatch(List.of(new TestNode("node-a"), new TestNode("node-b")));

        dispatcher.drainAll();
        assertThat(adapter.executed).containsExactly("doAddSubscription:node-a", "doAddSubscription:node-b");
    }

    @Test
    void writeBatch_fallsBackToOneDoWritePerEntry() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final SingleNodeRecordingAdapter adapter = new SingleNodeRecordingAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput());
        final TestDataPointFactory dataPointFactory = new TestDataPointFactory();

        adapter.writeBatch(List.of(
                new WriteEntry(new TestNode("setpoint-a"), dataPointFactory.create("setpoint-a", 1L)),
                new WriteEntry(new TestNode("setpoint-b"), dataPointFactory.create("setpoint-b", 2L))));

        dispatcher.drainAll();
        assertThat(adapter.executed).containsExactly("doWrite:setpoint-a:1", "doWrite:setpoint-b:2");
    }

    @Test
    void nativeBatchOverride_winsOverTheFallback() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final NativeBatchAdapter adapter = new NativeBatchAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput());

        adapter.pollBatch(List.of(new TestNode("node-a"), new TestNode("node-b")));

        dispatcher.drainAll();
        assertThat(adapter.executed).containsExactly("doPollBatch:native:2");
    }
}
