package com.hivemq.adapter.sdk.api.exceptions;

import org.jetbrains.annotations.NotNull;

public class TagDefinitionParseException extends RuntimeException {
    public TagDefinitionParseException(@NotNull final String message) {
        super(message);
    }
}
