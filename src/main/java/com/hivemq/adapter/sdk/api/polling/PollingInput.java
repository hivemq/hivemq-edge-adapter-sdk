package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.extension.sdk.api.annotations.NotNull;

public interface PollingInput {

    @NotNull PollingContext getPollingContext();

}
