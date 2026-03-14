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
package com.hivemq.adapter.sdk.api.tag;

import org.jetbrains.annotations.NotNull;

public interface Tag {

    @NotNull
    TagDefinition getDefinition();

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    /** Returns the ID of the adapter instance that owns this tag. Returns empty string for old-style tag implementations. */
    @NotNull
    default String getScope() {
        return "";
    }

    /** Sets the adapter instance scope. No-op for old-style tag implementations. */
    default void setScope(final @NotNull String scope) {
        // no-op for old-style implementations
    }

}
