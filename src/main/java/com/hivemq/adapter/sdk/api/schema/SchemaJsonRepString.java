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
 * Converts between {@link Schema} objects and JSON Schema documents represented as strings.
 * <p>
 * This interface lives in the Adapter SDK and has no dependency on Jackson. The implementation
 * (with Jackson support) lives in hivemq-edge.
 */
public interface SchemaJsonRepString {

    /**
     * Returns a JSON Schema representation of the given schema as a JSON string.
     */
    @NotNull String toJsonSchemaString(@NotNull Schema schema);

    /**
     * Parses a JSON Schema string and reconstructs the corresponding {@link Schema}.
     */
    @NotNull Schema fromJsonSchemaString(@NotNull String json);
}
