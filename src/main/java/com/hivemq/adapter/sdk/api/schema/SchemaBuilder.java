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

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Root builder for constructing {@link Schema} objects.
 * <p>
 * Each builder accepts exactly one structure-defining call ({@link #any()}, {@link #scalar},
 * {@link #startObject()}, {@link #startArray()}, or {@link #schema}). Calling a second one
 * throws {@link IllegalStateException}.
 * <p>
 * Annotations ({@link #title}, {@link #description}, {@link #readable()}, {@link #writable()})
 * and the {@link #nullable()} flag may appear in any order relative to the structure-defining call.
 * <p>
 * Example:
 * <pre>{@code
 * Schema schema = schemaBuilder
 *     .startObject()
 *         .property("rpm")
 *             .required()
 *             .scalar(ScalarType.LONG)
 *             .title("Motor Speed")
 *             .readable(true).writable(false)
 *         .property("label")
 *             .scalar(ScalarType.STRING)
 *             .nullable()
 *     .endObject()
 *     .build();
 * }</pre>
 */
public final class SchemaBuilder extends AbstractSchemaBuilder<SchemaBuilder> {

    private final @Nullable Consumer<Schema> onBuild;

    public SchemaBuilder() {
        this(null);
    }

    public SchemaBuilder(final @Nullable Consumer<Schema> onBuild) {
        super("SchemaBuilder");
        this.onBuild = onBuild;
    }

    // ── Structure-defining calls ─────────────────────────────────────────────

    /**
     * No type restriction; any value is valid.
     */
    public @NotNull SchemaBuilder any() {
        return doAny();
    }

    /**
     * A single primitive type.
     */
    public @NotNull SchemaBuilder scalar(final @NotNull ScalarType type) {
        return doScalar(type);
    }

    /**
     * Begin a structured object with named properties. Returns an {@link ObjectSchemaBuilder}
     * whose {@link ObjectSchemaBuilder#endObject()} returns back to this builder.
     */
    public @NotNull ObjectSchemaBuilder<SchemaBuilder> startObject() {
        return doStartObject(this);
    }

    /**
     * Begin an ordered array. Returns an {@link ItemSchemaBuilder} so the element schema can be
     * defined immediately.
     */
    public @NotNull ItemSchemaBuilder<SchemaBuilder> startArray() {
        return doStartArray(this);
    }

    /**
     * Use an already-complete {@link Schema} as-is. Annotation methods must not be called after
     * this — the schema carries its own annotations.
     */
    public @NotNull SchemaBuilder schema(final @NotNull Schema schema) {
        return doSchema(schema);
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    /**
     * Mark this schema as nullable. Equivalent to {@code nullable(true)}.
     */
    public @NotNull SchemaBuilder nullable() {
        return doNullable(true);
    }

    /**
     * Set whether {@code null} is a valid value. Default is {@code false}.
     */
    public @NotNull SchemaBuilder nullable(final boolean nullable) {
        return doNullable(nullable);
    }

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar types.
     */
    public @NotNull SchemaBuilder minimum(final long minimum) {
        return doMinimum(minimum);
    }

    /**
     * Set the inclusive upper bound for numeric scalar types.
     */
    public @NotNull SchemaBuilder maximum(final long maximum) {
        return doMaximum(maximum);
    }

    /**
     * Set the inclusive lower bound for floating-point scalar types.
     */
    public @NotNull SchemaBuilder minimum(final double minimum) {
        return doMinimum(minimum);
    }

    /**
     * Set the inclusive upper bound for floating-point scalar types.
     */
    public @NotNull SchemaBuilder maximum(final double maximum) {
        return doMaximum(maximum);
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label.
     */
    public @NotNull SchemaBuilder title(final @NotNull String title) {
        return doTitle(title);
    }

    /**
     * Set a longer human-readable explanation.
     */
    public @NotNull SchemaBuilder description(final @NotNull String description) {
        return doDescription(description);
    }

    /**
     * Mark this schema as readable. Equivalent to {@code readable(true)}.
     */
    public @NotNull SchemaBuilder readable() {
        return doReadable(true);
    }

    /**
     * Set whether clients may read the value. Default is {@code true}.
     */
    public @NotNull SchemaBuilder readable(final boolean readable) {
        return doReadable(readable);
    }

    /**
     * Mark this schema as writable. Equivalent to {@code writable(true)}.
     */
    public @NotNull SchemaBuilder writable() {
        return doWritable(true);
    }

    /**
     * Set whether clients may write the value. Default is {@code false}.
     */
    public @NotNull SchemaBuilder writable(final boolean writable) {
        return doWritable(writable);
    }

    // ── Terminal ─────────────────────────────────────────────────────────────

    /**
     * Build the final immutable {@link Schema}, recursively building any nested structures.
     */
    public @NotNull Schema build() {
        final Schema schema = buildSchema();
        if (onBuild != null) {
            onBuild.accept(schema);
        }
        return schema;
    }
}
