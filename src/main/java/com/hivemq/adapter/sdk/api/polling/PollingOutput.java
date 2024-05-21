package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.extension.sdk.api.annotations.NotNull;

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
     */
    void fail(@NotNull Throwable t);

}
