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
 * Builder for defining a single property inside an {@link ObjectSchemaBuilder}.
 * <p>
 * Provides structure-defining calls, annotations, and shorthand methods that delegate through
 * {@link #endProperty()} to the containing object builder.
 *
 * @param <P> the parent builder type of the containing {@link ObjectSchemaBuilder}.
 */
public interface PropertySchemaBuilder<P> {

    // ── Property-specific ────────────────────────────────────────────────────

    /**
     * Mark this property as required. Equivalent to {@code required(true)}.
     */
    @NotNull PropertySchemaBuilder<P> required();

    /**
     * Set whether this property must be present in the object.
     */
    @NotNull PropertySchemaBuilder<P> required(boolean required);

    // ── Structure-defining calls ─────────────────────────────────────────────

    /**
     * No type restriction; any value is valid for this property.
     */
    @NotNull PropertySchemaBuilder<P> any();

    /**
     * A single primitive type for this property.
     */
    @NotNull PropertySchemaBuilder<P> scalar(@NotNull ScalarType type);

    /**
     * Begin a nested object as this property's value impl.
     */
    @NotNull ObjectSchemaBuilder<PropertySchemaBuilder<P>> startObject();

    /**
     * Begin a nested array as this property's value impl.
     */
    @NotNull ItemSchemaBuilder<PropertySchemaBuilder<P>> startArray();

    /**
     * Use an already-complete {@link Schema} as this property's value impl.
     * Annotation methods must not be called after this.
     */
    @NotNull PropertySchemaBuilder<P> schema(@NotNull Schema schema);

    // ── Structure-defining flag ──────────────────────────────────────────────

    /**
     * Mark this property's value as nullable. Equivalent to {@code nullable(true)}.
     */
    @NotNull PropertySchemaBuilder<P> nullable();

    /**
     * Set whether {@code null} is a valid value for this property. Default is {@code false}.
     */
    @NotNull PropertySchemaBuilder<P> nullable(boolean nullable);

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar types.
     */
    @NotNull PropertySchemaBuilder<P> minimum(long minimum);

    /**
     * Set the inclusive upper bound for numeric scalar types.
     */
    @NotNull PropertySchemaBuilder<P> maximum(long maximum);

    /**
     * Set the inclusive lower bound for floating-point scalar types.
     */
    @NotNull PropertySchemaBuilder<P> minimum(double minimum);

    /**
     * Set the inclusive upper bound for floating-point scalar types.
     */
    @NotNull PropertySchemaBuilder<P> maximum(double maximum);

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label for this property.
     */
    @NotNull PropertySchemaBuilder<P> title(@NotNull String title);

    /**
     * Set a longer human-readable explanation for this property.
     */
    @NotNull PropertySchemaBuilder<P> description(@NotNull String description);

    /**
     * Mark this property as readable. Equivalent to {@code readable(true)}.
     */
    @NotNull PropertySchemaBuilder<P> readable();

    /**
     * Set whether clients may read this property's value. Default is {@code true}.
     */
    @NotNull PropertySchemaBuilder<P> readable(boolean readable);

    /**
     * Mark this property as writable. Equivalent to {@code writable(true)}.
     */
    @NotNull PropertySchemaBuilder<P> writable();

    /**
     * Set whether clients may write this property's value. Default is {@code false}.
     */
    @NotNull PropertySchemaBuilder<P> writable(boolean writable);

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Return to the containing {@link ObjectSchemaBuilder}.
     */
    @NotNull ObjectSchemaBuilder<P> endProperty();

    // ── Shorthands (delegate through endProperty()) ──────────────────────────

    /**
     * Shorthand for {@code endProperty().property(key)}.
     */
    @NotNull PropertySchemaBuilder<P> property(@NotNull String key);

    /**
     * Shorthand for {@code endProperty().additionalProperties(additionalProperties)}.
     */
    @NotNull ObjectSchemaBuilder<P> additionalProperties(boolean additionalProperties);

    /**
     * Shorthand for {@code endProperty().endObject()}.
     */
    @NotNull P endObject();
}
