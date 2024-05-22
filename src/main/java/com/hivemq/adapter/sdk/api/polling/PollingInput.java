package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.config.PollingContext;
import org.jetbrains.annotations.NotNull;

/**
 * Input Object for the poll() method of an {@link PollingProtocolAdapter} containing information tu use during the poll.
 */

public interface PollingInput {

    /**
     * @return the polling context that contains information on the mqtt processing for this poll.
     */
    @NotNull PollingContext getPollingContext();

}
