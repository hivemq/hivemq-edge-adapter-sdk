package com.hivemq.adapter.sdk.api.polling;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.extension.sdk.api.annotations.NotNull;

public interface PollingOutput {

    void addDataPoint(final @NotNull String tagName, final @NotNull Object tagValue);

    void addDataPoint(final @NotNull DataPoint dataPoint);

    void finish();

    void fail(@NotNull Throwable t);

}
