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
 * An ordered sequence of values whose elements share a single item impl.
 * <p>
 * {@link #items()} is itself a {@link Schema} — it may be any type, including a nested
 * {@link ObjectSchema} or {@link ArraySchema}. Unconstrained elements are represented by
 * {@link AnySchema}; {@link #items()} is never {@code null}.
 */
public record ArraySchema(
        @NotNull Schema items,
        @Nullable Integer minContains,
        @Nullable Integer maxContains,
        @Nullable String title,
        @Nullable String description,
        boolean nullable,
        boolean readable,
        boolean writable
) implements Schema {}
