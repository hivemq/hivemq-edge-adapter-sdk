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

import com.hivemq.adapter.sdk.api.schema.ArraySchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.ItemSchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.ObjectSchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.ScalarType;
import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.NotNull;

final class ItemSchemaBuilderImpl<P> extends AbstractSchemaBuilder<ItemSchemaBuilderImpl<P>>
        implements ItemSchemaBuilder<P> {

    private final ArraySchemaBuilderImpl<P> parentArray;

    ItemSchemaBuilderImpl(final ArraySchemaBuilderImpl<P> parentArray) {
        super("array element");
        this.parentArray = parentArray;
        this.struct.kind = SchemaStructure.Kind.ANY_DEFAULT;
    }

    // ── Structure-defining calls ─────────────────────────────────────────────

    @Override
    public @NotNull ItemSchemaBuilder<P> any() {
        return doAny();
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> scalar(final @NotNull ScalarType type) {
        return doScalar(type);
    }

    @Override
    public @NotNull ObjectSchemaBuilder<ItemSchemaBuilder<P>> startObject() {
        return doStartObject((ItemSchemaBuilder<P>) this);
    }

    @Override
    public @NotNull ItemSchemaBuilder<ItemSchemaBuilder<P>> startArray() {
        return doStartArray((ItemSchemaBuilder<P>) this);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> schema(final @NotNull Schema schema) {
        return doSchema(schema);
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    @Override
    public @NotNull ItemSchemaBuilder<P> nullable() {
        return doNullable(true);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> nullable(final boolean nullable) {
        return doNullable(nullable);
    }

    // ── Range constraints ────────────────────────────────────────────────────

    @Override
    public @NotNull ItemSchemaBuilder<P> minimum(final long minimum) {
        return doMinimum(minimum);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> maximum(final long maximum) {
        return doMaximum(maximum);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> minimum(final double minimum) {
        return doMinimum(minimum);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> maximum(final double maximum) {
        return doMaximum(maximum);
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    @Override
    public @NotNull ItemSchemaBuilder<P> title(final @NotNull String title) {
        return doTitle(title);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> description(final @NotNull String description) {
        return doDescription(description);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> readable() {
        return doReadable(true);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> readable(final boolean readable) {
        return doReadable(readable);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> writable() {
        return doWritable(true);
    }

    @Override
    public @NotNull ItemSchemaBuilder<P> writable(final boolean writable) {
        return doWritable(writable);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Override
    public @NotNull ArraySchemaBuilder<P> endItem() {
        return parentArray;
    }

    // ── Shorthands ───────────────────────────────────────────────────────────

    @Override
    public @NotNull ArraySchemaBuilder<P> minContains(final int min) {
        return endItem().minContains(min);
    }

    @Override
    public @NotNull ArraySchemaBuilder<P> maxContains(final int max) {
        return endItem().maxContains(max);
    }

    @Override
    public @NotNull P endArray() {
        return endItem().endArray();
    }
}
