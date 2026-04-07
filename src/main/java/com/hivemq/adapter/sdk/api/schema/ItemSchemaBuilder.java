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
public interface ItemSchemaBuilder<P> {

    // ── Structure-defining calls ─────────────────────────────────────────────

    /**
     * No type restriction; any value is valid for array elements.
     */
    @NotNull ItemSchemaBuilder<P> any();

    /**
     * A single primitive type for array elements.
     */
    @NotNull ItemSchemaBuilder<P> scalar(@NotNull ScalarType type);

    /**
     * Begin a nested object as the element impl.
     */
    @NotNull ObjectSchemaBuilder<ItemSchemaBuilder<P>> startObject();

    /**
     * Begin a nested array as the element impl.
     */
    @NotNull ItemSchemaBuilder<ItemSchemaBuilder<P>> startArray();

    /**
     * Use an already-complete {@link Schema} as the element impl.
     * Annotation methods must not be called after this.
     */
    @NotNull ItemSchemaBuilder<P> schema(@NotNull Schema schema);

    // ── Structure-defining flag ──────────────────────────────────────────────

    /**
     * Mark array elements as nullable. Equivalent to {@code nullable(true)}.
     */
    @NotNull ItemSchemaBuilder<P> nullable();

    /**
     * Set whether {@code null} is a valid element value. Default is {@code false}.
     */
    @NotNull ItemSchemaBuilder<P> nullable(boolean nullable);

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar element types.
     */
    @NotNull ItemSchemaBuilder<P> minimum(long minimum);

    /**
     * Set the inclusive upper bound for numeric scalar element types.
     */
    @NotNull ItemSchemaBuilder<P> maximum(long maximum);

    /**
     * Set the inclusive lower bound for floating-point scalar element types.
     */
    @NotNull ItemSchemaBuilder<P> minimum(double minimum);

    /**
     * Set the inclusive upper bound for floating-point scalar element types.
     */
    @NotNull ItemSchemaBuilder<P> maximum(double maximum);

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label for the element impl.
     */
    @NotNull ItemSchemaBuilder<P> title(@NotNull String title);

    /**
     * Set a longer human-readable explanation for the element impl.
     */
    @NotNull ItemSchemaBuilder<P> description(@NotNull String description);

    /**
     * Mark the element schema as readable. Equivalent to {@code readable(true)}.
     */
    @NotNull ItemSchemaBuilder<P> readable();

    /**
     * Set whether clients may read element values. Default is {@code true}.
     */
    @NotNull ItemSchemaBuilder<P> readable(boolean readable);

    /**
     * Mark the element schema as writable. Equivalent to {@code writable(true)}.
     */
    @NotNull ItemSchemaBuilder<P> writable();

    /**
     * Set whether clients may write element values. Default is {@code false}.
     */
    @NotNull ItemSchemaBuilder<P> writable(boolean writable);

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Return to the containing {@link ArraySchemaBuilder}.
     */
    @NotNull ArraySchemaBuilder<P> endItem();

    // ── Shorthands (delegate through endItem()) ──────────────────────────────

    /**
     * Shorthand for {@code endItem().minContains(min)}.
     */
    @NotNull ArraySchemaBuilder<P> minContains(int min);

    /**
     * Shorthand for {@code endItem().maxContains(max)}.
     */
    @NotNull ArraySchemaBuilder<P> maxContains(int max);

    /**
     * Shorthand for {@code endItem().endArray()}.
     */
    @NotNull P endArray();
}
