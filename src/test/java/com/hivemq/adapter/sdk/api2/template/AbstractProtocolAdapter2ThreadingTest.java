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
import com.hivemq.adapter.sdk.api2.command.BrowseFilter;
import com.hivemq.adapter.sdk.api2.command.WriteEntry;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2;
import com.hivemq.adapter.sdk.api2.node.Node;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The template's actor contract: a command method only enqueues; {@code do*} runs on the dispatch (drain)
 * thread; equal-band commands execute in tell order; a lifecycle command told after queued batch work is
 * delivered ahead of it ({@code CONTROL} &gt; {@code DATA}).
 */
class AbstractProtocolAdapter2ThreadingTest {

    private static final class ThreadRecordingAdapter extends AbstractProtocolAdapter2 {

        private final @NotNull List<String> executed = new ArrayList<>();
        private final @NotNull List<Thread> executingThreads = new ArrayList<>();

        ThreadRecordingAdapter(
                final @NotNull ProtocolAdapterInput2 input, final @NotNull ProtocolAdapterOutput2 reporter) {
            super(input, reporter);
        }

        private void record(final @NotNull String label) {
            executed.add(label);
            executingThreads.add(Thread.currentThread());
        }

        @Override
        protected void doStart() {
            record("doStart");
        }

        @Override
        protected void doStop() {
            record("doStop");
        }

        @Override
        protected void doConnect() {
            record("doConnect");
        }

        @Override
        protected void doDisconnect() {
            record("doDisconnect");
        }

        @Override
        protected void doPoll(final @NotNull Node node) {
            record("doPoll:" + node.nodeId());
        }

        @Override
        protected void doAddSubscription(final @NotNull Node node) {
            record("doAddSubscription:" + node.nodeId());
        }

        @Override
        protected void doWrite(final @NotNull Node node, final @NotNull DataPoint value) {
            record("doWrite:" + node.nodeId());
        }

        @Override
        protected void doBrowse(final @NotNull BrowseFilter filter) {
            record("doBrowse:" + filter.filterNode().nodeId());
        }
    }

    @Test
    void commandMethod_onlyEnqueues_drainAllRunsTheDoMethodOnTheDrainThread() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final ThreadRecordingAdapter adapter = new ThreadRecordingAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput2());

        adapter.start();
        assertThat(adapter.executed).isEmpty();

        dispatcher.drainAll();
        assertThat(adapter.executed).containsExactly("doStart");
        assertThat(adapter.executingThreads).containsExactly(Thread.currentThread());
    }

    @Test
    void equalBandCommands_executeInTellOrder() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final ThreadRecordingAdapter adapter = new ThreadRecordingAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput2());

        adapter.start();
        adapter.connect();
        adapter.pollBatch(List.of(new TestNode("node-a")));
        adapter.pollBatch(List.of(new TestNode("node-b")));
        adapter.browse(new BrowseFilter(new TestNode("root")));

        dispatcher.drainAll();
        assertThat(adapter.executed)
                .containsExactly("doStart", "doConnect", "doPoll:node-a", "doPoll:node-b", "doBrowse:root");
    }

    @Test
    void stopToldAfterQueuedBatchCommands_isDeliveredAheadOfThem() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final ThreadRecordingAdapter adapter = new ThreadRecordingAdapter(
                TestProtocolAdapterInput.create("adapter-1", dispatcher), new RecordingProtocolAdapterOutput2());

        adapter.pollBatch(List.of(new TestNode("node-a")));
        adapter.writeBatch(List.of(
                new WriteEntry(new TestNode("setpoint"), new TestDataPointFactory().create("setpoint", 21.5d))));
        adapter.stop();

        dispatcher.drainAll();
        assertThat(adapter.executed).containsExactly("doStop", "doPoll:node-a", "doWrite:setpoint");
    }
}
