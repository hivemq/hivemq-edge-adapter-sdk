package com.hivemq.adapter.sdk.api.datapoint;

import org.jetbrains.annotations.NotNull;

public interface DataPointListBuilder {

    DataPointBuilder<DataPointListBuilder> dataPoint(final @NotNull String tagName);

    void send();
}
