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
package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This interface is used for protocol adapters which implement polling algorithms from PLC endpoints.
 * The scheduling is done by the adapter framework in HiveMQ Edge.
 */
public interface PollingProtocolAdapter extends ProtocolAdapter {

    /**
     * This method gets invoked by the HiveMQ Edge adapter framework to collect data points from the plc.
     * @param pollingInput input object containing information what and how to poll the data
     * @param pollingOutput output object to add data for this poll and to signalize edge polling is done or failed.
     */
    void poll(@NotNull PollingInput pollingInput, @NotNull PollingOutput pollingOutput);

    /**
     * This method gets invoked by HiveMQ Edge to receive all polling contexts for this adapter.
     * Adapters can return multiple contexts  in order to poll from multiple endpoints. For each context
     * the poll method will be indicated separately.
     * @return A list of polling contexts for this adapter instance.
     */
    @NotNull List<? extends PollingContext> getPollingContexts();

    /**
     * @return an integer representing the milliseconds between starts of polls
     */
    int getPollingIntervalMillis();

    /**
     * @return an integer representing a upper limit of consecutive errors during a poll.
     * If this limit is exceeded, the polling will not be scheduled for this adapter anymore
     * and the adapter gets stopped.
     */
    int getMaxPollingErrorsBeforeRemoval();
}
