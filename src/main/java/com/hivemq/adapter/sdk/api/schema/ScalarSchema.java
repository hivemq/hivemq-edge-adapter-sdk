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
import org.jetbrains.annotations.Nullable;

/**
 * A single primitive type. The scalar type is expressed as one {@link ScalarType} enum constant.
 * Null-ability is a separate {@link #nullable()} field.
 * <p>
 * {@link #minimum()} and {@link #maximum()} are optional inclusive range constraints. They are
 * only meaningful on numeric types ({@code LONG}, {@code ULONG}, {@code DOUBLE}) and are
 * {@code null} when unconstrained.
 */
public record ScalarSchema(
        @NotNull ScalarType type,
        @Nullable Number minimum,
        @Nullable Number maximum,
        @Nullable String title,
        @Nullable String description,
        boolean nullable,
        boolean readable,
        boolean writable
) implements Schema {}
