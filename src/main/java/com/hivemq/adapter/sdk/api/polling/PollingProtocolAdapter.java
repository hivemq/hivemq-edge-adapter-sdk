package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.List;

public interface PollingProtocolAdapter extends ProtocolAdapter {

    void poll(@NotNull PollingInput pollingInput, @NotNull PollingOutput pollingOutput);

    @NotNull List<? extends PollingContext> getSubscriptions();

    int getPollingIntervalMillis();

    int getMaxPollingErrorsBeforeRemoval();

    default void onSamplerClosed() {

    }

}
