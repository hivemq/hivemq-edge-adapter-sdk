package com.hivemq.adapter.sdk.api.writing;

import com.hivemq.adapter.sdk.api.config.WriteContext;
import org.jetbrains.annotations.NotNull;

public interface WriteInput<P extends WritePayload, C extends WriteContext> {

    @NotNull
    P getWritePayload();

    @NotNull
    C getWriteContext();

}
