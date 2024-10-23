package com.hivemq.adapter.sdk.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class TagNotFoundException extends RuntimeException{
    public TagNotFoundException(@NotNull final String message) {
        super(message);
    }
}
