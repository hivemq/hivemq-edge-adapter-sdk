package com.hivemq.adapter.sdk.api;

import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.adapter.sdk.api.data.ProtocolAdapterDataSample;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PollingProtocolAdapter extends ProtocolAdapter {

    @NotNull CompletableFuture<? extends ProtocolAdapterDataSample> poll(@NotNull PollingContext pollingContext);

    @NotNull List<? extends PollingContext> getSubscriptions();

    int getPollingIntervalMillis();

    int getMaxPollingErrorsBeforeRemoval();

    default void onSamplerClosed() {

    }

}
