package com.hivemq.adapter.sdk.api.schema;

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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Internal value object that holds the scalar-specific state for a builder:
 * the scalar type and optional range constraints. Mirrors the role of
 * {@link ObjectSchemaBuilder} and {@link ArraySchemaBuilder} for their
 * respective kinds.
 */
public class ScalarSchemaBuilder {

    final @NotNull ScalarType scalarType;
    @Nullable Number minimum;
    @Nullable Number maximum;

    public ScalarSchemaBuilder(final @NotNull ScalarType scalarType) {
        this.scalarType = scalarType;
    }

    boolean isNumeric() {
        return scalarType == ScalarType.LONG ||
                scalarType == ScalarType.ULONG ||
                scalarType == ScalarType.DOUBLE;
    }

    @NotNull ScalarSchema buildSchema(final @NotNull SchemaAnnotations ann, final boolean nullable) {
        return new ScalarSchema(scalarType, minimum, maximum,
                ann.title, ann.description, nullable, ann.readable, ann.writable);
    }
}
