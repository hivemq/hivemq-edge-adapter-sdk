/*
 * Copyright 2023-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.adapter.sdk.api.v2.node;

import org.jetbrains.annotations.NotNull;

/**
 * The declared access capabilities of a node.
 *
 * @param readable     whether the node's value can be read at all.
 * @param writable     whether the node's value can be written.
 * @param pollable     whether the value can be polled; meaningful only if {@code readable} is
 *                     {@link AccessTriState#YES}.
 * @param subscribable whether the value can be subscribed to; meaningful only if {@code readable} is
 *                     {@link AccessTriState#YES}.
 */
public record AccessFlags(
        @NotNull AccessTriState readable,
        @NotNull AccessTriState writable,
        @NotNull AccessTriState pollable,
        @NotNull AccessTriState subscribable) {

    /**
     * @return a builder for {@link AccessFlags}. The four capabilities are all {@link AccessTriState} and so are
     *         trivially transposed when passed positionally to the canonical constructor; the builder names each
     *         at the call site. Every capability defaults to {@link AccessTriState#NO} until set.
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Names each {@link AccessFlags} capability at the call site instead of relying on positional order.
     */
    public static final class Builder {
        private @NotNull AccessTriState readable = AccessTriState.NO;
        private @NotNull AccessTriState writable = AccessTriState.NO;
        private @NotNull AccessTriState pollable = AccessTriState.NO;
        private @NotNull AccessTriState subscribable = AccessTriState.NO;

        private Builder() {}

        public @NotNull Builder readable(final @NotNull AccessTriState readable) {
            this.readable = readable;
            return this;
        }

        public @NotNull Builder writable(final @NotNull AccessTriState writable) {
            this.writable = writable;
            return this;
        }

        public @NotNull Builder pollable(final @NotNull AccessTriState pollable) {
            this.pollable = pollable;
            return this;
        }

        public @NotNull Builder subscribable(final @NotNull AccessTriState subscribable) {
            this.subscribable = subscribable;
            return this;
        }

        public @NotNull AccessFlags build() {
            return new AccessFlags(readable, writable, pollable, subscribable);
        }
    }
}
