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
 * Builder for defining the properties of an {@link ObjectSchema}.
 * <p>
 * Entered via {@link SchemaBuilder#startObject()}, {@link PropertySchemaBuilder#startObject()},
 * or {@link ItemSchemaBuilder#startObject()}.
 *
 * @param <P> the parent builder type returned by {@link #endObject()}.
 */
public interface ObjectSchemaBuilder<P> {

    /**
     * Start a property builder for the given key.
     */
    @NotNull PropertySchemaBuilder<P> property(@NotNull String key);

    /**
     * Set whether undeclared properties are permitted. Default is {@code true}.
     */
    @NotNull ObjectSchemaBuilder<P> additionalProperties(boolean additionalProperties);

    /**
     * Return to the parent builder.
     */
    @NotNull P endObject();
}
