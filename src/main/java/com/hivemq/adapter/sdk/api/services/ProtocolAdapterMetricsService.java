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
package com.hivemq.adapter.sdk.api.services;

import org.jetbrains.annotations.NotNull;

/**
 * Service class to increment and decrement adapter specific metrics.
 */
public interface ProtocolAdapterMetricsService {


    /**
     * Increments the counter for successful reads. NOTE: For {@link com.hivemq.adapter.sdk.api.polling.PollingProtocolAdapter} this is done by the framework.
     */
    void incrementReadPublishSuccess();

    /**
     * Increments the counter for failed reads. NOTE: For {@link com.hivemq.adapter.sdk.api.polling.PollingProtocolAdapter} this is done by the framework.
     */
    void incrementReadPublishFailure();

    /**
     * Increments the metrics for failed connection attempts.
     */
    void incrementConnectionFailure();

    /**
     * Increments the metrics for successful connection attempts.
     */
    void incrementConnectionSuccess();

    /**
     * Custom counter can be incremented via this method.
     * @param metricName the name of the custom counter, which will be prefixed with
     *                   "com.hivemq.edge.protocol-adapters.ADAPTER_TYPE.ADAPTER_ID."
     */
    void increment(@NotNull String metricName);


}
