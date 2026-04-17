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

import static java.util.Objects.requireNonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for {@link SchemaBuilder}, {@link PropertySchemaBuilder}, and
 * {@link ItemSchemaBuilder}. Holds all structure-definition and annotation state.
 * Subclasses add only navigation ({@code endObject}, {@code endArray}, etc.) and
 * {@link SchemaBuilder} alone adds {@code build()}.
 */
abstract class AbstractSchemaBuilder<Self extends AbstractSchemaBuilder<Self>> {

    private final String name;
    final SchemaStructure struct = new SchemaStructure();
    final SchemaAnnotations ann = new SchemaAnnotations();
    private @Nullable Schema cached;

    AbstractSchemaBuilder(final String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    final Self self() {
        return (Self) this;
    }

    final void checkNoDoubleStructure() {
        if (struct.kind != null && struct.kind != SchemaStructure.Kind.ANY_DEFAULT) {
            throw new IllegalStateException(name + ": structure already defined");
        }
    }

    // ── Structure-defining calls ─────────────────────────────────────────────

    /**
     * No type restriction; any value is valid.
     */
    public final @NotNull Self any() {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.ANY;
        return self();
    }

    /**
     * A single primitive type.
     */
    public final @NotNull Self scalar(final @NotNull ScalarType type) {
        requireNonNull(type, "type");
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.SCALAR;
        struct.scalarType = type;
        struct.scalarBuilder = new ScalarSchemaBuilder(type);
        return self();
    }

    /**
     * Use an already-complete {@link Schema} as-is. Annotation methods must not be called after
     * this — the schema carries its own annotations.
     */
    public final @NotNull Self schema(final @NotNull Schema schema) {
        requireNonNull(schema, "schema");
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.SCHEMA;
        struct.prebuiltSchema = schema;
        return self();
    }

    /**
     * Begin a structured object with named properties. Returns an {@link ObjectSchemaBuilder}
     * whose {@link ObjectSchemaBuilder#endObject()} returns back to this builder.
     */
    public final @NotNull ObjectSchemaBuilder<Self> startObject() {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.OBJECT;
        final var ob = new ObjectSchemaBuilder<>(self());
        struct.objectBuilder = ob;
        return ob;
    }

    /**
     * Begin an ordered array. Returns an {@link ItemSchemaBuilder} so the element schema can be
     * defined immediately.
     */
    public final @NotNull ItemSchemaBuilder<Self> startArray() {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.ARRAY;
        final var ab = new ArraySchemaBuilder<>(self());
        struct.arrayBuilder = ab;
        return ab.items;
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    /**
     * Mark this schema as nullable. Equivalent to {@code nullable(true)}.
     */
    public final @NotNull Self nullable() {
        return nullable(true);
    }

    /**
     * Set whether {@code null} is a valid value. Default is {@code false}.
     */
    public final @NotNull Self nullable(final boolean nullable) {
        struct.nullable = nullable;
        return self();
    }

    // ── Range constraints ────────────────────────────────────────────────────

    /**
     * Set the inclusive lower bound for numeric scalar types.
     */
    public final @NotNull Self minimum(final long minimum) {
        return minimum((Number) minimum);
    }

    /**
     * Set the inclusive lower bound for floating-point scalar types.
     */
    public final @NotNull Self minimum(final double minimum) {
        return minimum((Number) minimum);
    }

    final Self minimum(final Number minimum) {
        if (struct.kind != SchemaStructure.Kind.SCALAR) {
            throw new IllegalStateException(name + ": minimum() requires scalar()");
        }
        if (struct.scalarBuilder == null || !struct.scalarBuilder.isNumeric()) {
            throw new IllegalStateException(name + ": minimum() requires a numeric scalar type (LONG, ULONG, DOUBLE)");
        }
        struct.scalarBuilder.minimum = minimum;
        return self();
    }

    /**
     * Set the inclusive upper bound for numeric scalar types.
     */
    public final @NotNull Self maximum(final long maximum) {
        return maximum((Number) maximum);
    }

    /**
     * Set the inclusive upper bound for floating-point scalar types.
     */
    public final @NotNull Self maximum(final double maximum) {
        return maximum((Number) maximum);
    }

    final Self maximum(final Number maximum) {
        if (struct.kind != SchemaStructure.Kind.SCALAR) {
            throw new IllegalStateException(name + ": maximum() requires scalar()");
        }
        if (struct.scalarBuilder == null || !struct.scalarBuilder.isNumeric()) {
            throw new IllegalStateException(name + ": maximum() requires a numeric scalar type (LONG, ULONG, DOUBLE)");
        }
        struct.scalarBuilder.maximum = maximum;
        return self();
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    /**
     * Set a short human-readable label.
     */
    public final @NotNull Self title(final @NotNull String title) {
        requireNonNull(title, "title");
        ann.title = title;
        return self();
    }

    /**
     * Set a longer human-readable explanation.
     */
    public final @NotNull Self description(final @NotNull String description) {
        requireNonNull(description, "description");
        ann.description = description;
        return self();
    }

    /**
     * Mark this schema as readable. Equivalent to {@code readable(true)}.
     */
    public final @NotNull Self readable() {
        return readable(true);
    }

    /**
     * Set whether clients may read the value. Default is {@code true}.
     */
    public final @NotNull Self readable(final boolean readable) {
        ann.readable = readable;
        return self();
    }

    /**
     * Mark this schema as writable. Equivalent to {@code writable(true)}.
     */
    public final @NotNull Self writable() {
        return writable(true);
    }

    /**
     * Set whether clients may write the value. Default is {@code false}.
     */
    public final @NotNull Self writable(final boolean writable) {
        ann.writable = writable;
        return self();
    }

    // ── Build ────────────────────────────────────────────────────────────────

    @SuppressWarnings("NullAway")
    final Schema buildSchema() {
        if (cached != null) {
            return cached;
        }
        // The fields accessed in each case arm are guaranteed to be set by the corresponding
        // structure-defining method (scalar, startObject, startArray, schema).
        cached = switch (struct.kind) {
            case ANY, ANY_DEFAULT ->
                new AnySchema(ann.title, ann.description, struct.nullable, ann.readable, ann.writable);
            case SCALAR -> {
                if(struct.scalarBuilder != null) {
                    yield struct.scalarBuilder.buildSchema(ann, struct.nullable);
                } else {
                    throw new IllegalStateException(name + ": scalar builder is null");
                }
            }
            case OBJECT -> {
                if(struct.objectBuilder != null) {
                    yield struct.objectBuilder.buildSchema(ann, struct.nullable);
                } else {
                    throw new IllegalStateException(name + ": object builder is null");
                }
            }
            case ARRAY -> {
                if(struct.arrayBuilder != null) {
                    yield struct.arrayBuilder.buildSchema(ann, struct.nullable);
                } else {
                    throw new IllegalStateException(name + ": array builder is null");
                }
            }
            case SCHEMA -> struct.prebuiltSchema;
            case null -> throw new IllegalStateException(name + ": no structure defined");
        };
        return cached;
    }
}
