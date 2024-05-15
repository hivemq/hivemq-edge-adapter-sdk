package com.hivemq.adapter.sdk.api.factories;

import com.hivemq.extension.sdk.api.annotations.NotNull;

/**
 * This class offers access to factory classes for implementations of SDK interfaces.
 */
public interface AdapterFactories {

    /**
     * @return a factory to create concrete instances of PollingContext
     */
    @NotNull PollingContextFactory adapterSubscriptionFactory();

    /**
     * @return a factory to create concrete instances of DataPoint
     */
    @NotNull DataPointFactory dataPointFactory();
}
