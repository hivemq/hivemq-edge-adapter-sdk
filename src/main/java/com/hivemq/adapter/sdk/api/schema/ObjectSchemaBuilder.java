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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for defining the properties of an {@link ObjectSchema}.
 * <p>
 * Entered via {@link SchemaBuilder#startObject()}, {@link PropertySchemaBuilder#startObject()},
 * or {@link ItemSchemaBuilder#startObject()}.
 *
 * @param <P> the parent builder type returned by {@link #endObject()}.
 */
public final class ObjectSchemaBuilder<P> {

    final P parent;
    final Map<String, PropertySchemaBuilder<P>> properties = new LinkedHashMap<>();
    final Set<String> required = new LinkedHashSet<>();
    boolean additionalProperties = true;

    ObjectSchemaBuilder(final P parent) {
        this.parent = parent;
    }

    /**
     * Start a property builder for the given key.
     */
    public @NotNull PropertySchemaBuilder<P> property(final @NotNull String key) {
        final var prop = new PropertySchemaBuilder<>(this, key);
        properties.put(key, prop);
        return prop;
    }

    /**
     * Set whether undeclared properties are permitted. Default is {@code true}.
     */
    public @NotNull ObjectSchemaBuilder<P> additionalProperties(final boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    /**
     * Return to the parent builder.
     */
    public @NotNull P endObject() {
        return parent;
    }

    ObjectSchema buildSchema(final SchemaAnnotations ann, final boolean nullable) {
        final var propertiesMap = new LinkedHashMap<String, Schema>();
        properties.forEach((key, prop) -> propertiesMap.put(key, prop.buildSchema()));
        return new ObjectSchema(
                Collections.unmodifiableMap(propertiesMap),
                List.copyOf(required),
                additionalProperties,
                ann.title,
                ann.description,
                nullable,
                ann.readable,
                ann.writable);
    }
}
