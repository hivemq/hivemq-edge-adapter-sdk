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

import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * The inseparable pair: {@link Node} is the protocol-specific half (the protocol adapter sees this only);
 * {@link Tag} is Edge's half. Created together, each holds a direct reference to the other — no lookup map
 * anywhere.
 */
public final class NodeTagPair {

    private final @NotNull Node node;
    private final @NotNull Tag tag;

    private NodeTagPair(final @NotNull Node node, final @NotNull Tag tag) {
        this.node = node;
        this.tag = tag;
    }

    /**
     * Create the pair. This is the only way a {@link Tag} comes into existence — the pair halves have no
     * public constructors.
     *
     * @param node         the protocol-specific node definition.
     * @param tagName      the tag name, unique within its adapter.
     * @param schema       the reused v1 {@link Schema} describing the tag's value shape.
     * @param pollable     whether the tag's value can be polled.
     * @param subscribable whether the tag's value can be subscribed to.
     * @return the new pair, with the {@link Tag} back-referencing the given {@link Node}.
     */
    public static @NotNull NodeTagPair create(
            final @NotNull Node node,
            final @NotNull String tagName,
            final @NotNull Schema schema,
            final boolean pollable,
            final boolean subscribable) {
        return new NodeTagPair(node, new Tag(tagName, schema, pollable, subscribable, node));
    }

    /**
     * @return the protocol-specific half of the pair.
     */
    public @NotNull Node node() {
        return node;
    }

    /**
     * @return Edge's half of the pair.
     */
    public @NotNull Tag tag() {
        return tag;
    }
}
