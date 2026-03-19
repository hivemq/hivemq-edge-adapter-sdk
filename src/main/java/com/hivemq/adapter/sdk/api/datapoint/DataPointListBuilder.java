package com.hivemq.adapter.sdk.api.datapoint;

import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

public interface DataPointListBuilder {

    @NotNull DataPointBuilder<DataPointListBuilder> addDataPoint(@NotNull Tag tag);

    void publish();
}
