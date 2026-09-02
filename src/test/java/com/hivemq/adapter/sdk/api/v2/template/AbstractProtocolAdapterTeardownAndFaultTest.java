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

import static org.assertj.core.api.Assertions.assertThat;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterInput;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * The template's two robustness guarantees. <b>Teardown:</b> the adapter attaches its own dispatch thread at
 * construction and owns the binding, so {@link AbstractProtocolAdapter#close()} detaches it — the framework's seam to
 * release the thread when the adapter instance is discarded, distinct from a plain {@code stop()}. <b>Fault
 * tolerance:</b> a {@code do*} method that throws an unchecked exception never escapes the dispatch loop — a failed
 * {@code doStop()} still acknowledges {@code stopped()}, every other failure is surfaced as an {@code ADAPTER} error,
 * and the adapter keeps processing later commands.
 */
class AbstractProtocolAdapterTeardownAndFaultTest {

    @Test
    void close_detachesTheAdapterFromTheDispatcher() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final FaultyAdapter adapter =
                new FaultyAdapter(TestProtocolAdapterInput.create("a", dispatcher), new RecordingProtocolAdapterOutput());

        adapter.close();
        adapter.start();
        dispatcher.drainAll();

        // The binding is gone, so the queued command is never delivered: the dispatch thread has been released.
        assertThat(adapter.executed).isEmpty();
    }

    @Test
    void throwingDoConnect_reportsAdapterError_andTheAdapterStillProcessesLaterCommands() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput output = new RecordingProtocolAdapterOutput();
        final FaultyAdapter adapter =
                new FaultyAdapter(TestProtocolAdapterInput.create("a", dispatcher), output);
        adapter.failConnect = true;

        adapter.connect();
        adapter.start();
        dispatcher.drainAll();

        // The thrown connect is surfaced as an ADAPTER error, not allowed to escape the dispatch loop, and the next
        // command (start) is still processed — the adapter did not die.
        assertThat(output.invocations()).anyMatch(invocation -> invocation.startsWith("error:ADAPTER"));
        assertThat(adapter.executed).containsExactly("doConnect", "doStart");
    }

    @Test
    void throwingDoStop_stillAcknowledgesStopped() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput output = new RecordingProtocolAdapterOutput();
        final FaultyAdapter adapter =
                new FaultyAdapter(TestProtocolAdapterInput.create("a", dispatcher), output);
        adapter.failStop = true;

        adapter.stop();
        dispatcher.drainAll();

        // stop() must always acknowledge stopped(), even when the teardown threw.
        assertThat(output.invocations()).contains("stopped");
    }

    private static final class FaultyAdapter extends AbstractProtocolAdapter {

        private final @NotNull List<String> executed = new ArrayList<>();
        private boolean failConnect;
        private boolean failStop;

        FaultyAdapter(final @NotNull ProtocolAdapterInput input, final @NotNull ProtocolAdapterOutput output) {
            super(input, output);
        }

        @Override
        protected void doStart() {
            executed.add("doStart");
            output.started();
        }

        @Override
        protected void doStop() {
            executed.add("doStop");
            if (failStop) {
                throw new IllegalStateException("teardown blew up");
            }
            output.stopped();
        }

        @Override
        protected void doConnect() {
            executed.add("doConnect");
            if (failConnect) {
                throw new IllegalStateException("connect blew up");
            }
            output.connected();
        }

        @Override
        protected void doDisconnect() {
            executed.add("doDisconnect");
            output.disconnected();
        }

        @Override
        protected void doPoll(final @NotNull Node node) {
            executed.add("doPoll");
        }

        @Override
        protected void doAddSubscription(final @NotNull Node node) {
        }

        @Override
        protected void doWrite(final @NotNull Node node, final @NotNull DataPoint value) {
        }
    }
}
