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
public interface SchemaBuilder {

    // ── Structure-defining calls ──────────────────────────────────────────────

    /**
     * No type restriction; any value is valid.
     */
    @NotNull SchemaBuilder any();

    /**
     * A single primitive type.
     */
    @NotNull SchemaBuilder scalar(@NotNull ScalarType type);

    /**
     * Begin a structured object with named properties. Returns an {@link ObjectSchemaBuilder}
     * whose {@link ObjectSchemaBuilder#endObject()} returns back to this builder.
     */
    @NotNull ObjectSchemaBuilder<SchemaBuilder> startObject();

    /**
     * Begin an ordered array. Returns an {@link ItemSchemaBuilder} so the element schema can be
     * defined immediately.
     */
    @NotNull ItemSchemaBuilder<SchemaBuilder> startArray();

    /**
     * Use an already-complete {@link Schema} as-is. Annotation methods must not be called after
     * this — the schema carries its own annotations.
     */
    @NotNull SchemaBuilder schema(@NotNull Schema schema);

    // ── Structure-defining flag ──────────────────────────────────────────────

    /**
     * Mark this schema as nullable. Equivalent to {@code nullable(true)}.
     */
    @NotNull SchemaBuilder nullable();

    /**
     * Set whether {@code null} is a valid value. Default is {@code false}.
     */
    @NotNull SchemaBuilder nullable(boolean nullable);

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar types.
     */
    @NotNull SchemaBuilder minimum(long minimum);

    /**
     * Set the inclusive upper bound for numeric scalar types.
     */
    @NotNull SchemaBuilder maximum(long maximum);

    /**
     * Set the inclusive lower bound for floating-point scalar types.
     */
    @NotNull SchemaBuilder minimum(double minimum);

    /**
     * Set the inclusive upper bound for floating-point scalar types.
     */
    @NotNull SchemaBuilder maximum(double maximum);

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label.
     */
    @NotNull SchemaBuilder title(@NotNull String title);

    /**
     * Set a longer human-readable explanation.
     */
    @NotNull SchemaBuilder description(@NotNull String description);

    /**
     * Mark this schema as readable. Equivalent to {@code readable(true)}.
     */
    @NotNull SchemaBuilder readable();

    /**
     * Set whether clients may read the value. Default is {@code true}.
     */
    @NotNull SchemaBuilder readable(boolean readable);

    /**
     * Mark this schema as writable. Equivalent to {@code writable(true)}.
     */
    @NotNull SchemaBuilder writable();

    /**
     * Set whether clients may write the value. Default is {@code false}.
     */
    @NotNull SchemaBuilder writable(boolean writable);

    // ── Terminal ─────────────────────────────────────────────────────────────

    /**
     * Build the final immutable {@link Schema}, recursively building any nested structures.
     */
    @NotNull Schema build();
}
