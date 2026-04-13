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
 * Builder for defining a single property inside an {@link ObjectSchemaBuilder}.
 * <p>
 * Provides structure-defining calls, annotations, and shorthand methods that delegate through
 * {@link #endProperty()} to the containing object builder.
 *
 * @param <P> the parent builder type of the containing {@link ObjectSchemaBuilder}.
 */
public final class PropertySchemaBuilder<P> extends AbstractSchemaBuilder<PropertySchemaBuilder<P>> {

    private final ObjectSchemaBuilder<P> parentObject;
    private final String key;

    PropertySchemaBuilder(final ObjectSchemaBuilder<P> parentObject, final String key) {
        super("property '" + key + "'");
        this.parentObject = parentObject;
        this.key = key;
    }

    // ── Property-specific ────────────────────────────────────────────────────

    /**
     * Mark this property as required. Equivalent to {@code required(true)}.
     */
    public @NotNull PropertySchemaBuilder<P> required() {
        return required(true);
    }

    /**
     * Set whether this property must be present in the object.
     */
    public @NotNull PropertySchemaBuilder<P> required(final boolean required) {
        if (required) {
            parentObject.required.add(key);
        } else {
            parentObject.required.remove(key);
        }
        return self();
    }

    // ── Structure-defining calls ─────────────────────────────────────────────

    /**
     * No type restriction; any value is valid for this property.
     */
    public @NotNull PropertySchemaBuilder<P> any() {
        return doAny();
    }

    /**
     * A single primitive type for this property.
     */
    public @NotNull PropertySchemaBuilder<P> scalar(final @NotNull ScalarType type) {
        return doScalar(type);
    }

    /**
     * Begin a nested object as this property's value schema.
     */
    public @NotNull ObjectSchemaBuilder<PropertySchemaBuilder<P>> startObject() {
        return doStartObject(this);
    }

    /**
     * Begin a nested array as this property's value schema.
     */
    public @NotNull ItemSchemaBuilder<PropertySchemaBuilder<P>> startArray() {
        return doStartArray(this);
    }

    /**
     * Use an already-complete {@link Schema} as this property's value schema.
     * Annotation methods must not be called after this.
     */
    public @NotNull PropertySchemaBuilder<P> schema(final @NotNull Schema schema) {
        return doSchema(schema);
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    /**
     * Mark this property's value as nullable. Equivalent to {@code nullable(true)}.
     */
    public @NotNull PropertySchemaBuilder<P> nullable() {
        return doNullable(true);
    }

    /**
     * Set whether {@code null} is a valid value for this property. Default is {@code false}.
     */
    public @NotNull PropertySchemaBuilder<P> nullable(final boolean nullable) {
        return doNullable(nullable);
    }

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar types.
     */
    public @NotNull PropertySchemaBuilder<P> minimum(final long minimum) {
        return doMinimum(minimum);
    }

    /**
     * Set the inclusive upper bound for numeric scalar types.
     */
    public @NotNull PropertySchemaBuilder<P> maximum(final long maximum) {
        return doMaximum(maximum);
    }

    /**
     * Set the inclusive lower bound for floating-point scalar types.
     */
    public @NotNull PropertySchemaBuilder<P> minimum(final double minimum) {
        return doMinimum(minimum);
    }

    /**
     * Set the inclusive upper bound for floating-point scalar types.
     */
    public @NotNull PropertySchemaBuilder<P> maximum(final double maximum) {
        return doMaximum(maximum);
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label for this property.
     */
    public @NotNull PropertySchemaBuilder<P> title(final @NotNull String title) {
        return doTitle(title);
    }

    /**
     * Set a longer human-readable explanation for this property.
     */
    public @NotNull PropertySchemaBuilder<P> description(final @NotNull String description) {
        return doDescription(description);
    }

    /**
     * Mark this property as readable. Equivalent to {@code readable(true)}.
     */
    public @NotNull PropertySchemaBuilder<P> readable() {
        return doReadable(true);
    }

    /**
     * Set whether clients may read this property's value. Default is {@code true}.
     */
    public @NotNull PropertySchemaBuilder<P> readable(final boolean readable) {
        return doReadable(readable);
    }

    /**
     * Mark this property as writable. Equivalent to {@code writable(true)}.
     */
    public @NotNull PropertySchemaBuilder<P> writable() {
        return doWritable(true);
    }

    /**
     * Set whether clients may write this property's value. Default is {@code false}.
     */
    public @NotNull PropertySchemaBuilder<P> writable(final boolean writable) {
        return doWritable(writable);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    /**
     * Return to the containing {@link ObjectSchemaBuilder}.
     */
    public @NotNull ObjectSchemaBuilder<P> endProperty() {
        return parentObject;
    }

    // ── Shorthands (delegate through endProperty()) ──────────────────────────

    /**
     * Shorthand for {@code endProperty().property(key)}.
     */
    public @NotNull PropertySchemaBuilder<P> property(final @NotNull String key) {
        return endProperty().property(key);
    }

    /**
     * Shorthand for {@code endProperty().additionalProperties(additionalProperties)}.
     */
    public @NotNull ObjectSchemaBuilder<P> additionalProperties(final boolean additionalProperties) {
        return endProperty().additionalProperties(additionalProperties);
    }

    /**
     * Shorthand for {@code endProperty().endObject()}.
     */
    public @NotNull P endObject() {
        return endProperty().endObject();
    }
}
