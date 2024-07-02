package com.hivemq.adapter.sdk.api.writing;

import org.jetbrains.annotations.NotNull;

public interface WriteInput <T extends WritePayload>{

    @NotNull T getWritePayload();

}
