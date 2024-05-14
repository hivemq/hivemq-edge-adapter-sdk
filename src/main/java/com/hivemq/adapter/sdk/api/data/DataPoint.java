package com.hivemq.adapter.sdk.api.data;

import com.hivemq.adapter.sdk.api.factories.AdapterFactories;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterInput;
import com.hivemq.extension.sdk.api.annotations.NotNull;

/**
 * Interface for data points gathered by a protocol adapter.
 * Instances of it can be created via {@link DataPointFactory} accessible via {@link AdapterFactories} in the {@link ProtocolAdapterInput}.
 */
public interface DataPoint {
    /**
     * @return the value of the data point.
     */
    @NotNull Object getTagValue();

    /**
     * @return the tag of the data point.
     */
    @NotNull String getTagName();
}
