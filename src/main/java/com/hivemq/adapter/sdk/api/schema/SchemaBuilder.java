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

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Root builder for constructing {@link Schema} objects.
 * <p>
 * Each builder accepts exactly one structure-defining call ({@link #any()}, {@link #scalar},
 * {@link #startObject()}, {@link #startArray()}, or {@link #schema}). Calling a second one
 * throws {@link IllegalStateException}.
 * <p>
 * Annotations ({@link #title}, {@link #description}, {@link #readable()}, {@link #writable()})
 * and the {@link #nullable()} flag may appear in any order relative to the structure-defining call.
 * <p>
 * Example:
 * <pre>{@code
 * Schema schema = schemaBuilder
 *     .startObject()
 *         .property("rpm")
 *             .required()
 *             .scalar(ScalarType.LONG)
 *             .title("Motor Speed")
 *             .readable(true).writable(false)
 *         .property("label")
 *             .scalar(ScalarType.STRING)
 *             .nullable()
 *     .endObject()
 *     .build();
 * }</pre>
 */
public final class SchemaBuilder extends AbstractSchemaBuilder<SchemaBuilder> {

    private final @Nullable Consumer<Schema> onBuild;

    public SchemaBuilder() {
        this(null);
    }

    public SchemaBuilder(final @Nullable Consumer<Schema> onBuild) {
        super("SchemaBuilder");
        this.onBuild = onBuild;
    }

    // ── Terminal ─────────────────────────────────────────────────────────────

    /**
     * Build the final immutable {@link Schema}, recursively building any nested structures.
     */
    public @NotNull Schema build() {
        final Schema schema = buildSchema();
        if (onBuild != null) {
            onBuild.accept(schema);
        }
        return schema;
    }
}
