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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Interface for data collected by a protocol adapter.
 * One sample can contain multiple data points (= tag values).
 * <p>
 * The actual data points can be added via the {@link #addDataPoint(DataPoint)} method.
 */
public interface ProtocolAdapterDataSample {
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
     * @return a map which maps tagName to the list of data points for this tagName
     * @deprecated We dropped support for multiple values per tag name a long time ago. Adapters are not supposed to buffer data. Also, each datapoint carries the information of the tag it belongs to.
     * Method will be removed in 2026.10. Switch to using getDataPointsList() instead.
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2026.10")
    @Deprecated()
    Map<String, List<DataPoint>> getDataPoints();

    /**
     * @return list of data points for the read tags
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotNull
    List<DataPoint> getDataPointList();
}
