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
import org.jetbrains.annotations.Nullable;

/**
 * Builder for array-level attributes (element count bounds).
 * <p>
 * Reached via {@link ItemSchemaBuilder#endItem()} after defining the element schema.
 *
 * @param <P> the parent builder type returned by {@link #endArray()}.
 */
public final class ArraySchemaBuilder<P> {

    final P parent;
    final ItemSchemaBuilder<P> items;

    @Nullable
    Integer minContains;

    @Nullable
    Integer maxContains;

    ArraySchemaBuilder(final P parent) {
        this.parent = parent;
        this.items = new ItemSchemaBuilder<>(this);
    }

    /**
     * Set the minimum number of elements.
     */
    public @NotNull ArraySchemaBuilder<P> minContains(final int min) {
        this.minContains = min;
        return this;
    }

    /**
     * Set the maximum number of elements.
     */
    public @NotNull ArraySchemaBuilder<P> maxContains(final int max) {
        this.maxContains = max;
        return this;
    }

    /**
     * Return to the parent builder.
     */
    public @NotNull P endArray() {
        return parent;
    }

    ArraySchema buildSchema(final SchemaAnnotations ann, final boolean nullable) {
        return new ArraySchema(
                items.buildSchema(),
                minContains,
                maxContains,
                ann.title,
                ann.description,
                nullable,
                ann.readable,
                ann.writable);
    }
}
