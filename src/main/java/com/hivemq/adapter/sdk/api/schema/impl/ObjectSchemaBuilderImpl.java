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

import com.hivemq.adapter.sdk.api.schema.ObjectSchema;
import com.hivemq.adapter.sdk.api.schema.ObjectSchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.PropertySchemaBuilder;
import com.hivemq.adapter.sdk.api.schema.Schema;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

final class ObjectSchemaBuilderImpl<P> implements ObjectSchemaBuilder<P> {

    final P parent;
    final Map<String, PropertySchemaBuilderImpl<P>> properties = new LinkedHashMap<>();
    final Set<String> required = new LinkedHashSet<>();
    boolean additionalProperties = true;

    ObjectSchemaBuilderImpl(final P parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull PropertySchemaBuilder<P> property(final @NotNull String key) {
        final var prop = new PropertySchemaBuilderImpl<>(this, key);
        properties.put(key, prop);
        return prop;
    }

    @Override
    public @NotNull ObjectSchemaBuilder<P> additionalProperties(final boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    @Override
    public @NotNull P endObject() {
        return parent;
    }

    ObjectSchema buildSchema(final SchemaAnnotations ann, final boolean nullable) {
        final var built = new LinkedHashMap<String, Schema>();
        properties.forEach((key, prop) -> built.put(key, prop.buildSchema()));
        return new ObjectSchema(
                Collections.unmodifiableMap(built),
                List.copyOf(required),
                additionalProperties,
                ann.title,
                ann.description,
                nullable,
                ann.readable,
                ann.writable);
    }
}
