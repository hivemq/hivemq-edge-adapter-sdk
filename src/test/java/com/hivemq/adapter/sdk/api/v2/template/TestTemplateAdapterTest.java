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
import com.hivemq.adapter.sdk.api.v2.model.WriteEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives {@link TestTemplateAdapter} through start → connect → poll / subscribe / write and asserts the
 * expected callbacks with reused v1 {@link DataPoint}s — the proof that an author can build a working
 * protocol adapter from the template alone.
 */
class TestTemplateAdapterTest {

    @Test
    void adapterId_isTheInputIdentifier() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final TestTemplateAdapter adapter = new TestTemplateAdapter(
                TestProtocolAdapterInput.create("test-template-adapter", dispatcher),
                new RecordingProtocolAdapterOutput());

        assertThat(adapter.adapterId()).isEqualTo("test-template-adapter");
    }

    @Test
    void lifecycle_producesTheExpectedAcknowledgments() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput callbacks = new RecordingProtocolAdapterOutput();
        final TestTemplateAdapter adapter = new TestTemplateAdapter(
                TestProtocolAdapterInput.create("test-template-adapter", dispatcher), callbacks);

        adapter.start();
        adapter.connect();
        dispatcher.drainAll();
        adapter.disconnect();
        adapter.stop();
        dispatcher.drainAll();

        assertThat(callbacks.invocations()).containsExactly("started", "connected", "disconnected", "stopped");
    }

    @Test
    void poll_producesOneReusedDataPointPerNode() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput callbacks = new RecordingProtocolAdapterOutput();
        final TestTemplateAdapter adapter = new TestTemplateAdapter(
                TestProtocolAdapterInput.create("test-template-adapter", dispatcher), callbacks);

        adapter.pollBatch(List.of(new TestNode("temperature"), new TestNode("pressure")));

        dispatcher.drainAll();
        // A single dataPoint per node completes each poll itself — the template adds no pollComplete.
        assertThat(callbacks.invocations()).containsExactly("dataPoint:temperature", "dataPoint:pressure");
        assertThat(callbacks.dataPoints()).hasSize(2);

        final DataPoint first = callbacks.dataPoints().get(0);
        assertThat(first.getTagName()).isEqualTo("temperature");
        assertThat(first.getTagValue()).isEqualTo(1L);
        assertThat(first.treatTagValueAsJson()).isFalse();

        final DataPoint second = callbacks.dataPoints().get(1);
        assertThat(second.getTagName()).isEqualTo("pressure");
        assertThat(second.getTagValue()).isEqualTo(2L);
    }

    @Test
    void subscribe_pushesADataPointForTheSubscribedNode() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput callbacks = new RecordingProtocolAdapterOutput();
        final TestTemplateAdapter adapter = new TestTemplateAdapter(
                TestProtocolAdapterInput.create("test-template-adapter", dispatcher), callbacks);

        adapter.addSubscriptionBatch(List.of(new TestNode("humidity")));

        dispatcher.drainAll();
        assertThat(callbacks.invocations()).containsExactly("dataPoint:humidity");
        assertThat(callbacks.dataPoints()).hasSize(1);
        assertThat(callbacks.dataPoints().get(0).getTagName()).isEqualTo("humidity");
    }

    @Test
    void write_acknowledgesWithASuccessfulWriteResult() {
        final ManualDispatcher dispatcher = new ManualDispatcher();
        final RecordingProtocolAdapterOutput callbacks = new RecordingProtocolAdapterOutput();
        final TestTemplateAdapter adapter = new TestTemplateAdapter(
                TestProtocolAdapterInput.create("test-template-adapter", dispatcher), callbacks);

        adapter.writeBatch(List.of(
                new WriteEntry(new TestNode("setpoint"), new TestDataPointFactory().create("setpoint", 21.5d))));

        dispatcher.drainAll();
        assertThat(callbacks.invocations()).containsExactly("writeResult:setpoint:success");
    }
}
