package com.hivemq.adapter.sdk.api.writing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WriteOutput {

    /**
     * Signals Edge that writing is done.
     */
    void finish();

    /**
     * Signals that something went wrong during the writing.
     *
     * @param t Throwable indicating what went wrong.
     * @param errorMessage an optional error message delivering further insights.
     */
    void fail(@NotNull Throwable t, @Nullable String errorMessage, boolean retry);

    /**
     * Signals that something went wrong during the writing.
     *
     * @param errorMessage a message indicating what went wrong.
     */
    void fail(@NotNull String errorMessage, boolean retry);

}
