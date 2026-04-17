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
