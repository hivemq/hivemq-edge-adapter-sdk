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

import com.hivemq.adapter.sdk.api.schema.ItemSchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.ObjectSchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.ScalarType;
import com.hivemq.adapter.sdk.api.schema.Schema;
import com.hivemq.adapter.sdk.api.schema.SchemaBuilder;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SchemaBuilderImpl extends AbstractSchemaBuilder<SchemaBuilderImpl> implements SchemaBuilder {

    private final @Nullable Consumer<Schema> onBuild;

    public SchemaBuilderImpl() {
        this(null);
    }

    public SchemaBuilderImpl(final @Nullable Consumer<Schema> onBuild) {
        super("SchemaBuilder");
        this.onBuild = onBuild;
    }

    // ── Structure-defining calls ─────────────────────────────────────────────

    @Override
    public @NotNull SchemaBuilder any() {
        return (SchemaBuilder) doAny();
    }

    @Override
    public @NotNull SchemaBuilder scalar(final @NotNull ScalarType type) {
        return (SchemaBuilder) doScalar(type);
    }

    @Override
    public @NotNull ObjectSchemaBuilder<SchemaBuilder> startObject() {
        return doStartObject((SchemaBuilder) this);
    }

    @Override
    public @NotNull ItemSchemaBuilder<SchemaBuilder> startArray() {
        return doStartArray((SchemaBuilder) this);
    }

    @Override
    public @NotNull SchemaBuilder schema(final @NotNull Schema schema) {
        return (SchemaBuilder) doSchema(schema);
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    @Override
    public @NotNull SchemaBuilder nullable() {
        return (SchemaBuilder) doNullable(true);
    }

    @Override
    public @NotNull SchemaBuilder nullable(final boolean nullable) {
        return (SchemaBuilder) doNullable(nullable);
    }

    // ── Range constraints ────────────────────────────────────────────────────

    @Override
    public @NotNull SchemaBuilder minimum(final long minimum) {
        return (SchemaBuilder) doMinimum(minimum);
    }

    @Override
    public @NotNull SchemaBuilder maximum(final long maximum) {
        return (SchemaBuilder) doMaximum(maximum);
    }

    @Override
    public @NotNull SchemaBuilder minimum(final double minimum) {
        return (SchemaBuilder) doMinimum(minimum);
    }

    @Override
    public @NotNull SchemaBuilder maximum(final double maximum) {
        return (SchemaBuilder) doMaximum(maximum);
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    @Override
    public @NotNull SchemaBuilder title(final @NotNull String title) {
        return (SchemaBuilder) doTitle(title);
    }

    @Override
    public @NotNull SchemaBuilder description(final @NotNull String description) {
        return (SchemaBuilder) doDescription(description);
    }

    @Override
    public @NotNull SchemaBuilder readable() {
        return (SchemaBuilder) doReadable(true);
    }

    @Override
    public @NotNull SchemaBuilder readable(final boolean readable) {
        return (SchemaBuilder) doReadable(readable);
    }

    @Override
    public @NotNull SchemaBuilder writable() {
        return (SchemaBuilder) doWritable(true);
    }

    @Override
    public @NotNull SchemaBuilder writable(final boolean writable) {
        return (SchemaBuilder) doWritable(writable);
    }

    // ── Terminal ─────────────────────────────────────────────────────────────

    @Override
    public @NotNull Schema build() {
        final Schema schema = buildSchema();
        if (onBuild != null) {
            onBuild.accept(schema);
        }
        return schema;
    }
}
