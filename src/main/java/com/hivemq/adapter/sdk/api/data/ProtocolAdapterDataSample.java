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
package com.hivemq.adapter.sdk.api.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for data collected by a protocol adapter.
 * One sample can contain multiple data points (= tag values).
 * <p>
 * The actual data points can be added via the {@link #addDataPoint(DataPoint)} method.
 */
public interface ProtocolAdapterDataSample {
    /**
     * @return the {@link PollingContext} containing information how the data gets published by the broker.
     */
    @JsonIgnore
    @NotNull
    PollingContext getPollingContext();

    /**
     * @return the timestamp when this data sample was taken.
     */
    @JsonIgnore
    @NotNull
    Long getTimestamp();

    /**
     * Adds a new data point to this sample.
     *
     * @param tagName  the name for this data point.
     * @param tagValue the value for this data point.
     */
    void addDataPoint(@NotNull String tagName, @NotNull Object tagValue);

    /**
     * Adds a new data point to this sample.
     *
     * @param dataPoint the data point.
     */
    void addDataPoint(@NotNull DataPoint dataPoint);

    /**
     * Sets/Overwrites all data points of the sample with the given argument.
     *
     * @param list the new list of data points for this sample.
     */
    void setDataPoints(@NotNull List<DataPoint> list);

    /**
     * @return the list of data points in this sample.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotNull
    List<DataPoint> getDataPoints();
}
