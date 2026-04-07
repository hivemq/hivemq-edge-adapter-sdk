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
import com.hivemq.adapter.sdk.api.schema.PropertySchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.ScalarType;
import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.NotNull;

final class PropertySchemaBuilderImpl<P> extends AbstractSchemaBuilder<PropertySchemaBuilderImpl<P>>
        implements PropertySchemaBuilder<P> {

    private final ObjectSchemaBuilderImpl<P> parentObject;
    private final String key;

    PropertySchemaBuilderImpl(final ObjectSchemaBuilderImpl<P> parentObject, final String key) {
        super("property '" + key + "'");
        this.parentObject = parentObject;
        this.key = key;
    }

    // ── Property-specific ────────────────────────────────────────────────────

    @Override
    public @NotNull PropertySchemaBuilder<P> required() {
        return required(true);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> required(final boolean required) {
        if (required) {
            parentObject.required.add(key);
        } else {
            parentObject.required.remove(key);
        }
        return self();
    }

    // ── Structure-defining calls ─────────────────────────────────────────────

    @Override
    public @NotNull PropertySchemaBuilder<P> any() {
        return doAny();
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> scalar(final @NotNull ScalarType type) {
        return doScalar(type);
    }

    @Override
    public @NotNull ObjectSchemaBuilder<PropertySchemaBuilder<P>> startObject() {
        return doStartObject((PropertySchemaBuilder<P>) this);
    }

    @Override
    public @NotNull ItemSchemaBuilder<PropertySchemaBuilder<P>> startArray() {
        return doStartArray((PropertySchemaBuilder<P>) this);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> schema(final @NotNull Schema schema) {
        return doSchema(schema);
    }

    // ── Nullable flag ────────────────────────────────────────────────────────

    @Override
    public @NotNull PropertySchemaBuilder<P> nullable() {
        return doNullable(true);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> nullable(final boolean nullable) {
        return doNullable(nullable);
    }

    // ── Range constraints ────────────────────────────────────────────────────

    @Override
    public @NotNull PropertySchemaBuilder<P> minimum(final long minimum) {
        return doMinimum(minimum);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> maximum(final long maximum) {
        return doMaximum(maximum);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> minimum(final double minimum) {
        return doMinimum(minimum);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> maximum(final double maximum) {
        return doMaximum(maximum);
    }

    // ── Annotations ──────────────────────────────────────────────────────────

    @Override
    public @NotNull PropertySchemaBuilder<P> title(final @NotNull String title) {
        return doTitle(title);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> description(final @NotNull String description) {
        return doDescription(description);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> readable() {
        return doReadable(true);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> readable(final boolean readable) {
        return doReadable(readable);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> writable() {
        return doWritable(true);
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> writable(final boolean writable) {
        return doWritable(writable);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Override
    public @NotNull ObjectSchemaBuilder<P> endProperty() {
        return parentObject;
    }

    // ── Shorthands ───────────────────────────────────────────────────────────

    @Override
    public @NotNull PropertySchemaBuilder<P> property(final @NotNull String key) {
        return endProperty().property(key);
    }

    @Override
    public @NotNull ObjectSchemaBuilder<P> additionalProperties(final boolean additionalProperties) {
        return endProperty().additionalProperties(additionalProperties);
    }

    @Override
    public @NotNull P endObject() {
        return endProperty().endObject();
    }
}
