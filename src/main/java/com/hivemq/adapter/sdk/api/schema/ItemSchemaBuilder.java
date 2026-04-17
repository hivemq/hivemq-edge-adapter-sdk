/*
 * Copyright 2019-present HiveMQ GmbH
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
package com.hivemq.adapter.sdk.api.schema;

import org.jetbrains.annotations.NotNull;

/**
 * Builder for defining the element schema of an {@link ArraySchema}.
 * <p>
 * Entered directly from {@link SchemaBuilder#startArray()}, {@link PropertySchemaBuilder#startArray()},
 * or {@link ItemSchemaBuilder#startArray()}.
 * <p>
 * Annotations and structure-defining calls on this builder apply to the array's <em>items</em>,
 * not to the array itself. Annotations for the array go on the parent builder after
 * {@link #endArray()}.
 *
 * @param <P> the parent builder type of the enclosing builder.
 */
public final class ItemSchemaBuilder<P> extends AbstractSchemaBuilder<ItemSchemaBuilder<P>> {

    private final ArraySchemaBuilder<P> parentArray;

    ItemSchemaBuilder(final ArraySchemaBuilder<P> parentArray) {
        super("array element");
        this.parentArray = parentArray;
        this.struct.kind = SchemaStructure.Kind.ANY_DEFAULT;
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Return to the containing {@link ArraySchemaBuilder}.
     */
    public @NotNull ArraySchemaBuilder<P> endItem() {
        return parentArray;
    }

    // ── Shorthands (delegate through endItem()) ──────────────────────────────

    /**
     * Shorthand for {@code endItem().minContains(min)}.
     */
    public @NotNull ArraySchemaBuilder<P> minContains(final int min) {
        return endItem().minContains(min);
    }

    /**
     * Shorthand for {@code endItem().maxContains(max)}.
     */
    public @NotNull ArraySchemaBuilder<P> maxContains(final int max) {
        return endItem().maxContains(max);
    }

    /**
     * Shorthand for {@code endItem().endArray()}.
     */
    public @NotNull P endArray() {
        return endItem().endArray();
    }
}
