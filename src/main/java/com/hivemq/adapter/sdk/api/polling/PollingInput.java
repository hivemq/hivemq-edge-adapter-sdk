package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.extension.sdk.api.annotations.NotNull;

/**
 * Input Object for the poll() method of an {@link PollingProtocolAdapter} containing information tu use during the poll.
 *
 * @author Daniel Krüger
 */

public interface PollingInput {

    /**
     * @return the polling context that contains information on the mqtt processing for this poll.
     */
    @NotNull PollingContext getPollingContext();

}
