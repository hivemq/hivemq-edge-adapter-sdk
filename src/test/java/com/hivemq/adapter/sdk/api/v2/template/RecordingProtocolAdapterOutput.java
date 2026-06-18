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
import com.hivemq.adapter.sdk.api.v2.model.BrowseContinuation;
import com.hivemq.adapter.sdk.api.v2.model.BrowseResultEntry;
import com.hivemq.adapter.sdk.api.v2.model.ErrorScope;
import com.hivemq.adapter.sdk.api.v2.model.VerifyOutcome;
import com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Records every reported invocation in order. Single-threaded test double — pairs with
 * {@link ManualDispatcher}, which drains on the calling thread.
 */
final class RecordingProtocolAdapterOutput implements ProtocolAdapterOutput {

    private final @NotNull List<String> invocations = new ArrayList<>();
    private final @NotNull List<DataPoint> dataPoints = new ArrayList<>();
    private final @NotNull List<VerifyOutcome> verifyOutcomes = new ArrayList<>();
    private final @NotNull List<List<BrowseResultEntry>> browseResults = new ArrayList<>();

    @Override
    public void started() {
        invocations.add("started");
    }

    @Override
    public void stopped() {
        invocations.add("stopped");
    }

    @Override
    public void connected() {
        invocations.add("connected");
    }

    @Override
    public void disconnected() {
        invocations.add("disconnected");
    }

    @Override
    public void error(final @NotNull ErrorScope scope, final @NotNull String reason) {
        invocations.add("error:" + scope + ":" + reason);
    }

    @Override
    public void verifyResult(final @NotNull Node node, final @NotNull VerifyOutcome outcome) {
        invocations.add("verifyResult:" + node.nodeId());
        verifyOutcomes.add(outcome);
    }

    @Override
    public void dataPoint(final @NotNull Node node, final @NotNull DataPoint value) {
        invocations.add("dataPoint:" + node.nodeId());
        dataPoints.add(value);
    }

    @Override
    public void nodeError(final @NotNull Node node, final @NotNull String reason, final boolean spontaneous) {
        invocations.add("nodeError:" + node.nodeId() + ":" + reason + ":" + spontaneous);
    }

    @Override
    public void writeResult(final @NotNull Node node, final boolean success, final @Nullable String reason) {
        invocations.add("writeResult:" + node.nodeId() + ":" + (success ? "success" : "failure:" + reason));
    }

    @Override
    public void browsePage(
            final int requestId,
            final @NotNull List<BrowseResultEntry> entries,
            final @Nullable BrowseContinuation continuation) {
        invocations.add("browsePage:" + requestId + ":" + (continuation == null ? "last" : "more"));
        browseResults.add(entries);
    }

    @Override
    public void browseError(final int requestId, final @NotNull String reason) {
        invocations.add("browseError:" + requestId + ":" + reason);
    }

    @NotNull List<String> invocations() {
        return invocations;
    }

    @NotNull List<DataPoint> dataPoints() {
        return dataPoints;
    }

    @NotNull List<VerifyOutcome> verifyOutcomes() {
        return verifyOutcomes;
    }

    @NotNull List<List<BrowseResultEntry>> browseResults() {
        return browseResults;
    }
}
