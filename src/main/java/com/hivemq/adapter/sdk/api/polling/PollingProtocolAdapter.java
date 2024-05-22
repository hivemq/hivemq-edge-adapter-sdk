package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.List;

/**
 * This interface is used for protocol adapters which implement polling algorithms from PLC endpoints.
 * The scheduling is done by the adapter framework in HiveMQ Edge.
 */
public interface PollingProtocolAdapter extends ProtocolAdapter {

    /**
     * This method gets invoked by the HiveMQ Edge adapter framework to collect data points from the plc.
     * @param pollingInput input object containing information what and how to poll the data
     * @param pollingOutput output object to add data for this poll and to signalize edge polling is done or failed.
     */
    void poll(@NotNull PollingInput pollingInput, @NotNull PollingOutput pollingOutput);

    /**
     * This method gets invoked by HiveMQ Edge to receive all polling contexts for this adapter.
     * Adapters can return multiple contexts  in order to poll from multiple endpoints. For each context
     * the poll method will be indicated separately.
     * @return A list of polling contexts for this adapter instance.
     */
    @NotNull List<? extends PollingContext> getPollingContexts();

    /**
     * @return an integer representing the milliseconds between starts of polls
     */
    int getPollingIntervalMillis();

    /**
     * @return an integer representing a upper limit of consecutive errors during a poll.
     * If this limit is exceeded, the polling will not be scheduled for this adapter anymore
     * and the adapter gets stopped.
     */
    int getMaxPollingErrorsBeforeRemoval();

    /**
     * Optional method to implement callback logic in case the scheduling of an adapter gets stopped by the framework.
     */
    default void onSamplerClosed() {

    }

}
