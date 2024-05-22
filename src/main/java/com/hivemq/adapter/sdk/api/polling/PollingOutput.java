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

import com.hivemq.adapter.sdk.api.data.DataPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Output parameter provided to the poll method of an {@link PollingProtocolAdapter}.
 * <p>
 * It can be used to
 * <ul>
 *   <li>Add data points
 *   <li>Finish the polling
 *   <li>Failing the polling
 * </ul>
 */
public interface PollingOutput {

    /**
     * Adds a data point to this sample.
     *
     * @param tagName  the name for the tag of this data point
     * @param tagValue the value of this data point
     */
    void addDataPoint(final @NotNull String tagName, final @NotNull Object tagValue);

    /**
     * Adds the given data point to this sample.
     *
     * @param dataPoint the data point to add.
     */
    void addDataPoint(final @NotNull DataPoint dataPoint);

    /**
     * Signals Edge that all data points are added and the further processing is done.
     * If no data points were added until this point, no publish will be created.
     */
    void finish();

    /**
     * Signals that something went wrong during polling.
     * As a result no publish is created from data. Data added before calling fail() will not be processed further.
     *
     * @param t Throwable indicating what went wrong.
     * @param errorMessage an optional error message delivering further insights.
     */
    void fail(@NotNull Throwable t, @Nullable String errorMessage);

    /**
     * Signals that something went wrong during polling.
     * As a result no publish is created from data. Data added before calling fail() will not be processed further.
     *
     * @param errorMessage a message indicating what went wrong.
     */
    void fail(@NotNull String errorMessage);

}
