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

import org.jetbrains.annotations.Nullable;

/**
 * Represents the type and constraints of a value. Describes what the value is — a scalar of a specific kind,
 * a structured object with named properties, an ordered array of elements, or an unconstrained value — and
 * whether {@code null} is valid.
 * <p>
 * {@code Schema} is a sealed interface. Its implementations are fully immutable records.
 */
public sealed interface Schema permits AnySchema, ScalarSchema, ObjectSchema, ArraySchema {

    /**
     * @return short human-readable label, or {@code null} if not set.
     */
    @Nullable String title();

    /**
     * @return longer human-readable explanation, or {@code null} if not set.
     */
    @Nullable String description();

    /**
     * @return whether {@code null} is a valid value; default {@code false}.
     */
    boolean nullable();

    /**
     * @return whether clients may read the value; default {@code true}.
     */
    boolean readable();

    /**
     * @return whether clients may write the value; default {@code false}.
     */
    boolean writable();
}
