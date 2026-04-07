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
 * Builder for array-level attributes (element count bounds).
 * <p>
 * Reached via {@link ItemSchemaBuilder#endItem()} after defining the element impl.
 *
 * @param <P> the parent builder type returned by {@link #endArray()}.
 */
public interface ArraySchemaBuilder<P> {

    /**
     * Set the minimum number of elements.
     */
    @NotNull ArraySchemaBuilder<P> minContains(int min);

    /**
     * Set the maximum number of elements.
     */
    @NotNull ArraySchemaBuilder<P> maxContains(int max);

    /**
     * Return to the parent builder.
     */
    @NotNull P endArray();
}
