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
package com.hivemq.adapter.sdk.api.schema.impl;

import com.hivemq.adapter.sdk.api.schema.AnySchema;
import com.hivemq.adapter.sdk.api.schema.ScalarSchema;
import com.hivemq.adapter.sdk.api.schema.ScalarType;
import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for {@link SchemaBuilderImpl}, {@link PropertySchemaBuilderImpl}, and
 * {@link ItemSchemaBuilderImpl}. Holds all structure-definition and annotation state.
 * Subclasses add only navigation ({@code endObject}, {@code endArray}, etc.) and
 * {@link SchemaBuilderImpl} alone adds {@code build()}.
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

    final Self doScalar(final ScalarType type) {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.SCALAR;
        struct.scalarType = type;
        return self();
    }

    final Self doAny() {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.ANY;
        return self();
    }

    final Self doSchema(final Schema schema) {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.SCHEMA;
        struct.prebuiltSchema = schema;
        return self();
    }

    /**
     * Internal helper for {@code startObject()}. Creates an {@link ObjectSchemaBuilderImpl}
     * parameterised on the SDK interface type {@code I} (not the schema type) so that
     * {@code endObject()} returns the interface type the caller expects.
     *
     * @param parent the parent cast to its SDK interface type
     * @param <I>    the SDK interface type of the parent builder
     */
    final <I> ObjectSchemaBuilderImpl<I> doStartObject(final I parent) {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.OBJECT;
        final var ob = new ObjectSchemaBuilderImpl<>(parent);
        struct.objectBuilder = ob;
        return ob;
    }

    /**
     * Internal helper for {@code startArray()}. Returns the {@link ItemSchemaBuilderImpl}
     * parameterised on the SDK interface type {@code I}.
     *
     * @param parent the parent cast to its SDK interface type
     * @param <I>    the SDK interface type of the parent builder
     */
    final <I> ItemSchemaBuilderImpl<I> doStartArray(final I parent) {
        checkNoDoubleStructure();
        struct.kind = SchemaStructure.Kind.ARRAY;
        final var ab = new ArraySchemaBuilderImpl<>(parent);
        struct.arrayBuilder = ab;
        return ab.items;
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    final Self doNullable(final boolean nullable) {
        struct.nullable = nullable;
        return self();
    }

    // ── Range constraints ────────────────────────────────────────────────────

    final Self doMinimum(final Number minimum) {
        struct.minimum = minimum;
        return self();
    }

    final Self doMaximum(final Number maximum) {
        struct.maximum = maximum;
        return self();
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    final Self doTitle(final String title) {
        ann.title = title;
        return self();
    }

    final Self doDescription(final String description) {
        ann.description = description;
        return self();
    }

    final Self doReadable(final boolean readable) {
        ann.readable = readable;
        return self();
    }

    final Self doWritable(final boolean writable) {
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
        // structure-defining method (doScalar, doStartObject, doStartArray, doSchema).
        cached = switch (struct.kind) {
            case ANY, ANY_DEFAULT ->
                new AnySchema(ann.title, ann.description, struct.nullable, ann.readable, ann.writable);
            case SCALAR ->
                new ScalarSchema(
                        struct.scalarType, struct.minimum, struct.maximum,
                        ann.title, ann.description, struct.nullable, ann.readable, ann.writable);
            case OBJECT -> struct.objectBuilder.buildSchema(ann, struct.nullable);
            case ARRAY -> struct.arrayBuilder.buildSchema(ann, struct.nullable);
            case SCHEMA -> struct.prebuiltSchema;
            case null -> throw new IllegalStateException(name + ": no structure defined");
        };
        return cached;
    }
}
