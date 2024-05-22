package com.hivemq.adapter.sdk.api.factories;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import org.jetbrains.annotations.NotNull;

public interface DataPointFactory {

    /**
     * @param tagName the name for this data point
     * @param tagValue the value for this data point
     * @return a {@link DataPoint} containing the name and value.
     */
   @NotNull DataPoint create(final @NotNull String tagName, final @NotNull Object tagValue);
}
