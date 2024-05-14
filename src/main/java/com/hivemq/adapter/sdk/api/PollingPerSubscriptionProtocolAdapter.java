package com.hivemq.adapter.sdk.api;

import com.hivemq.adapter.sdk.api.config.PublishingConfig;
import com.hivemq.adapter.sdk.api.data.ProtocolAdapterDataSample;
import com.hivemq.extension.sdk.api.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PollingPerSubscriptionProtocolAdapter extends ProtocolAdapter {

    @NotNull CompletableFuture<? extends ProtocolAdapterDataSample> poll(@NotNull PublishingConfig publishingConfig);

    @NotNull List<? extends PublishingConfig> getSubscriptions();

    int getPollingIntervalMillis();

    int getMaxPollingErrorsBeforeRemoval();

    default void onSamplerClosed() {

    }

}
