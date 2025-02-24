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
package com.hivemq.adapter.sdk.api.factories;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import org.jetbrains.annotations.NotNull;

public interface DataPointFactory {

    /**
     * @param tagName the name for this data point
     * @param tagValue the value for this data point
     * @return a {@link DataPoint} containing the name and value.
     */
   @NotNull DataPoint create(final @NotNull String tagName, final @NotNull Object tagValue);

    /**
     * Creates a DataPoint where the tagValue is treated as a JSON object.
     * @param tagName the name for this data point
     * @param tagValue the value for this data point
     * @return a {@link DataPoint} containing the name and value.
     */
   @NotNull DataPoint createJsonDataPoint(final @NotNull String tagName, final @NotNull Object tagValue);
}
