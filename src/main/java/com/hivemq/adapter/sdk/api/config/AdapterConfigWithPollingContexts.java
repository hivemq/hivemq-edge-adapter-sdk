package com.hivemq.adapter.sdk.api.config;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AdapterConfigWithPollingContexts {

    @NotNull List<? extends PollingContext> getPollingContexts();

}
