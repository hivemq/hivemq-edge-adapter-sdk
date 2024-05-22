package com.hivemq.adapter.sdk.api.services;

import com.hivemq.extension.sdk.api.annotations.NotNull;

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
