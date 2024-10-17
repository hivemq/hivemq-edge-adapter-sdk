package com.hivemq.adapter.sdk.api.config;

import com.hivemq.adapter.sdk.api.config.mapping.FieldMapping;
import org.jetbrains.annotations.NotNull;

public interface BidirectionalProtocolAdapterConfig extends ProtocolAdapterConfig {

    @NotNull
    FieldMapping getFieldMapping();

}
