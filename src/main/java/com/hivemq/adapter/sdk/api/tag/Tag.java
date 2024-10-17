package com.hivemq.adapter.sdk.api.tag;

import org.jetbrains.annotations.NotNull;

public interface Tag<T> {

    @NotNull
    T getTagAddress();

    @NotNull
    String getTagName();


}
