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
package com.hivemq.adapter.sdk.api2.node;

import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * Edge's half of the inseparable Node/Tag pair. Holds a direct reference to its {@link Node} — there is no
 * lookup map anywhere. Accessors only; no behavior.
 * <p>
 * Named under rule N1 because the v1 SDK ships {@code com.hivemq.adapter.sdk.api.tag.Tag}, a different
 * config-marker concept.
 * <p>
 * Instances are created only through {@link NodeTagPair#create(Node, String, Schema, boolean, boolean)} — the
 * pair halves have no public constructors.
 */
public final class Tag2 {

    private final @NotNull String name;
    private final @NotNull Schema schema;
    private final boolean pollable;
    private final boolean subscribable;
    private final @NotNull Node node;

    Tag2(
            final @NotNull String name,
            final @NotNull Schema schema,
            final boolean pollable,
            final boolean subscribable,
            final @NotNull Node node) {
        this.name = name;
        this.schema = schema;
        this.pollable = pollable;
        this.subscribable = subscribable;
        this.node = node;
    }

    /**
     * @return the tag name, unique within its adapter.
     */
    public @NotNull String name() {
        return name;
    }

    /**
     * @return the reused v1 {@link Schema} describing the tag's value shape, derived from the node's data
     *         type.
     */
    public @NotNull Schema schema() {
        return schema;
    }

    /**
     * @return whether the tag's value can be polled.
     */
    public boolean pollable() {
        return pollable;
    }

    /**
     * @return whether the tag's value can be subscribed to.
     */
    public boolean subscribable() {
        return subscribable;
    }

    /**
     * @return the protocol-specific half of the pair — the direct back-reference.
     */
    public @NotNull Node node() {
        return node;
    }
}
