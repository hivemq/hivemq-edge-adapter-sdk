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

import java.util.List;
import java.util.Map;

/**
 * A structured value with named, typed properties.
 * <p>
 * The order of properties in a value does not matter — the map preserves insertion order only as a
 * convenience for display and serialisation.
 */
public record ObjectSchema(
        @NotNull Map<String, Schema> properties,
        @NotNull List<String> required,
        boolean additionalProperties,
        @Nullable String title,
        @Nullable String description,
        boolean nullable,
        boolean readable,
        boolean writable
) implements Schema {}
