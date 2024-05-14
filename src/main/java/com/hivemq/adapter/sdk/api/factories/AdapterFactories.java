package com.hivemq.adapter.sdk.api.factories;

import com.hivemq.extension.sdk.api.annotations.NotNull;

/**
 * This class offers access to factory classes for implementations of SDK interfaces.
 */
public interface AdapterFactories {

    @NotNull AdapterSubscriptionFactory adapterSubscriptionFactory();

    @NotNull PayloadFactory payloadFactory();


    @NotNull DataPointFactory dataPointFactory();
}
