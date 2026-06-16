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
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2;
import com.hivemq.adapter.sdk.api2.model.ProtocolAdapterOutput2;
import com.hivemq.adapter.sdk.api2.node.Node;
import org.jetbrains.annotations.NotNull;

/**
 * The template's executable specification: the minimal working adapter an author writes against
 * {@link AbstractProtocolAdapter2} alone — only the abstract {@code do*} methods, synthetic values built with
 * the reused v1 {@code DataPointFactory}. Test-scope only; never shipped, never registered.
 */
final class TestTemplateAdapter extends AbstractProtocolAdapter2 {

    private long nextSyntheticValue = 1L;

    TestTemplateAdapter(
            final @NotNull ProtocolAdapterInput2 input, final @NotNull ProtocolAdapterOutput2 output) {
        super(input, output);
    }

    @Override
    protected void doStart() {
        output.started();
    }

    @Override
    protected void doStop() {
        output.stopped();
    }

    @Override
    protected void doConnect() {
        output.connected();
    }

    @Override
    protected void doDisconnect() {
        output.disconnected();
    }

    @Override
    protected void doPoll(final @NotNull Node node) {
        output.dataPoint(node, dataPointFactory.create(node.nodeId(), nextSyntheticValue++));
    }

    @Override
    protected void doAddSubscription(final @NotNull Node node) {
        // the synthetic device pushes the first value right after subscribing
        output.dataPoint(node, dataPointFactory.create(node.nodeId(), nextSyntheticValue++));
    }

    @Override
    protected void doWrite(final @NotNull Node node, final @NotNull DataPoint value) {
        output.writeResult(node, true, null);
    }
}
