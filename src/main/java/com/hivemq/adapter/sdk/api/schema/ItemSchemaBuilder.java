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

    // ── Structure-defining calls ─────────────────────────────────────────────

    /**
     * No type restriction; any value is valid for array elements.
     */
    public @NotNull ItemSchemaBuilder<P> any() {
        return doAny();
    }

    /**
     * A single primitive type for array elements.
     */
    public @NotNull ItemSchemaBuilder<P> scalar(final @NotNull ScalarType type) {
        return doScalar(type);
    }

    /**
     * Begin a nested object as the element schema.
     */
    public @NotNull ObjectSchemaBuilder<ItemSchemaBuilder<P>> startObject() {
        return doStartObject(this);
    }

    /**
     * Begin a nested array as the element schema.
     */
    public @NotNull ItemSchemaBuilder<ItemSchemaBuilder<P>> startArray() {
        return doStartArray(this);
    }

    /**
     * Use an already-complete {@link Schema} as the element schema.
     * Annotation methods must not be called after this.
     */
    public @NotNull ItemSchemaBuilder<P> schema(final @NotNull Schema schema) {
        return doSchema(schema);
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    /**
     * Mark array elements as nullable. Equivalent to {@code nullable(true)}.
     */
    public @NotNull ItemSchemaBuilder<P> nullable() {
        return doNullable(true);
    }

    /**
     * Set whether {@code null} is a valid element value. Default is {@code false}.
     */
    public @NotNull ItemSchemaBuilder<P> nullable(final boolean nullable) {
        return doNullable(nullable);
    }

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar element types.
     */
    public @NotNull ItemSchemaBuilder<P> minimum(final long minimum) {
        return doMinimum(minimum);
    }

    /**
     * Set the inclusive upper bound for numeric scalar element types.
     */
    public @NotNull ItemSchemaBuilder<P> maximum(final long maximum) {
        return doMaximum(maximum);
    }

    /**
     * Set the inclusive lower bound for floating-point scalar element types.
     */
    public @NotNull ItemSchemaBuilder<P> minimum(final double minimum) {
        return doMinimum(minimum);
    }

    /**
     * Set the inclusive upper bound for floating-point scalar element types.
     */
    public @NotNull ItemSchemaBuilder<P> maximum(final double maximum) {
        return doMaximum(maximum);
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label for the element schema.
     */
    public @NotNull ItemSchemaBuilder<P> title(final @NotNull String title) {
        return doTitle(title);
    }

    /**
     * Set a longer human-readable explanation for the element schema.
     */
    public @NotNull ItemSchemaBuilder<P> description(final @NotNull String description) {
        return doDescription(description);
    }

    /**
     * Mark the element schema as readable. Equivalent to {@code readable(true)}.
     */
    public @NotNull ItemSchemaBuilder<P> readable() {
        return doReadable(true);
    }

    /**
     * Set whether clients may read element values. Default is {@code true}.
     */
    public @NotNull ItemSchemaBuilder<P> readable(final boolean readable) {
        return doReadable(readable);
    }

    /**
     * Mark the element schema as writable. Equivalent to {@code writable(true)}.
     */
    public @NotNull ItemSchemaBuilder<P> writable() {
        return doWritable(true);
    }

    /**
     * Set whether clients may write element values. Default is {@code false}.
     */
    public @NotNull ItemSchemaBuilder<P> writable(final boolean writable) {
        return doWritable(writable);
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
